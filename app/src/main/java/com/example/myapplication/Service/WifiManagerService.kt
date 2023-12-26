package com.example.myapplication.Service

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
import java.io.IOException
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

                try {
                    while (true) {
                        val message = reader.readLine()
                        Log.i("收到訊息", message)
                        if (message == "exit") break
                        else mediaPlayerController(message)
                    }
                } catch (e: IOException) {
                    e.printStackTrace() // 也可以使用 Log 進行記錄
                } finally {
                    // 在這裡關閉資源，確保在例外情況下也能正確地關閉資源
                    try {
                        reader.close()
                        clientSocket.close()
                        serverSocket.close()
                        stopSelf()
                        Log.i("service","Wifi Service Shutdown")
                    } catch (e: IOException) {
                        e.printStackTrace() // 也可以使用 Log 進行記錄
                    }
                }
            }
        }
    }

    private fun mediaPlayerController(cmd:String){
        when (cmd) {
            "p" -> PlayPageViewModel.mediaPlayerStartPause()
            "pre" -> PlayPageViewModel.setSong(-1)
            "next" -> PlayPageViewModel.setSong(1)
            "+15" -> PlayPageViewModel.setMediaPosition(15, true)
            "-15" -> PlayPageViewModel.setMediaPosition(-15, true)
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
