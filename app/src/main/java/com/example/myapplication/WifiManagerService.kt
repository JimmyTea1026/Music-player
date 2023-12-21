package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.myapplication.PlayPage.PlayPageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket


class WifiManagerService : Service() {
    override fun onCreate() {
        super.onCreate()
        val serverSocket = ServerSocket(8888)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        Log.i("service","Wifi Service activate")
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                val clientSocket: Socket = serverSocket.accept()
                Log.i("", "Client connected: ${clientSocket.inetAddress}")
                val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                while(true){
                    val message = reader.readLine()
                    Log.i("收到訊息", message)
                    if(message == "exit") break
                    else mediaPlayerController(message)
                }

                reader.close()
                clientSocket.close()
                serverSocket.close()
                stopSelf()
                Log.i("service","Wifi Service Shutdown")
            }
        }
    }

    fun mediaPlayerController(cmd:String){
        if(cmd == "p") PlayPageViewModel.mediaPlayerStartPause()
        else if(cmd == "pre") PlayPageViewModel.setSong(-1)
        else if(cmd == "next") PlayPageViewModel.setSong(1)
        else if(cmd == "+15") PlayPageViewModel.setMediaPosition(15, true)
        else if(cmd == "-15") PlayPageViewModel.setMediaPosition(-15, true)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
