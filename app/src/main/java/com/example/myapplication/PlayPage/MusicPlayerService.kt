package com.example.myapplication.PlayPage

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicPlayerService: Service() {

    inner class MusicBinder : Binder(){
        fun getService(): MusicPlayerService{
            return this@MusicPlayerService
        }
        fun test(){
            Log.d("", "test")
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return MusicBinder()
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("","musicService activate")
    }


}