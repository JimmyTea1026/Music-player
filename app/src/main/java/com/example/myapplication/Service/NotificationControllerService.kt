package com.example.myapplication.Service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.myapplication.PlayPage.PlayPageViewModel

class NotificationControllerService : Service() {
    private val viewModel = PlayPageViewModel
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (intent.action) {
                "PLAY_PAUSE_ACTION" -> {
                    Log.i("","playPause")
                    viewModel.mediaPlayerStartPause()
                }
                "PRE_ACTION" -> {
                    Log.i("", "PRE")
                    viewModel.setSong(-1)
                }
                "NEXT_ACTION" -> {
                    Log.i("", "NEXT")
                    viewModel.setSong(1)
                }
                "+15_SECOND" -> {
                    Log.i("", "+15")
                    viewModel.setMediaPosition(15, true)
                }
                "-15_SECOND" -> {
                    Log.i("", "-15")
                    viewModel.setMediaPosition(-15, true)
                }
                else -> {}
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
