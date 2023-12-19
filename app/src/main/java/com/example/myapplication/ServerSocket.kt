package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket

class SocketServer: Service() {
    var serverAllowed : Boolean = true
    override fun onCreate() {
        super.onCreate()
        Log.d("","create")
        Thread(runnable).start()
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    override fun onDestroy() {
        super.onDestroy()
        serverAllowed = false
    }
    private var runnable = Runnable{
        val serverSocket: ServerSocket = ServerSocket(1234)
        val accept: Socket = serverSocket.accept()
        Thread{ response(accept) }.start()
    }
    private fun response(accept:Socket){
        try{
            Log.d("", "init")
            val bufferReaderIn: BufferedReader = BufferedReader(InputStreamReader(accept.getInputStream()))
            val out:PrintWriter = PrintWriter(BufferedWriter(OutputStreamWriter(accept.getOutputStream())), true)
            while(serverAllowed){
                val msg = bufferReaderIn.readLine()
                if(TextUtils.isEmpty(msg)){
                    Log.d("", "over")
                    break
                }
                Log.d("msg", msg)
                val msgOP = ""

            }
            bufferReaderIn.close()
            out.close()
            accept.close()
        }catch (e:Exception){
            Log.e("error", e.message.toString())
        }
    }

}