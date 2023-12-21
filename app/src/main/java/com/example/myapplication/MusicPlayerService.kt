package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicPlayerService: Service() {
    override fun onCreate() {
        super.onCreate()
        Log.i("MusicPlayerService","musicService activate")
    }
    inner class MusicBinder : Binder(){
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }

    }
    override fun onBind(intent: Intent?): IBinder {
        return MusicBinder()
    }
}