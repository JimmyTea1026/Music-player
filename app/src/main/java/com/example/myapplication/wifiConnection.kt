package com.example.myapplication

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.myapplication.PlayPage.PlayPageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

@Composable
fun wifiConnection(){
    fun createServer(): ServerSocket {
        return ServerSocket(8888)
    }
    var serverSocket by remember { mutableStateOf<ServerSocket>(createServer()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                val clientSocket: Socket = serverSocket.accept()
                Log.d("", "Client connected: ${clientSocket.inetAddress}")
                val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                while(true){
                    val message = reader.readLine()
                    Log.d("收到訊息", message)
                    if(message == "exit") break
                    else mediaPlayerController(message)
                }

                reader.close()
                clientSocket.close()
                serverSocket.close()
            }
        }
    }

}

fun mediaPlayerController(cmd:String){
    if(cmd == "p") {
        if(PlayPageViewModel.mediaPlayer.isPlaying) PlayPageViewModel.mediaPlayerPause()
        else PlayPageViewModel.mediaPlayerStart()
    }
    else if(cmd == "pre") PlayPageViewModel.setSong(-1)
    else if(cmd == "next") PlayPageViewModel.setSong(1)
    else if(cmd == "+15") PlayPageViewModel.setMediaPosition(15, true)
    else if(cmd == "-15") PlayPageViewModel.setMediaPosition(-15, true)
}