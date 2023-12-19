package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.myapplication.PlayPage.PlayPageViewModel

class MediaControlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == "PLAY_PAUSE_ACTION") {
            Log.d("", "ss")
//            PlayPageViewModel.mediaPlayerStartPause()
        } else if (action == "NEXT_ACTION") {
            PlayPageViewModel.setSong(1)
        } else if (action == "PREVIOUS_ACTION") {
            PlayPageViewModel.setSong(-1)
        }
        // 可以根據需要處理更多的操作
    }
}