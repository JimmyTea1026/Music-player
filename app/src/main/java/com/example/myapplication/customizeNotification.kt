package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.myapplication.PlayPage.PlayPageViewModel

class customizeNotification {
    private lateinit var notificationManager : NotificationManager
    private lateinit var context: Context
    fun createCustomNotification(cxt: Context){
        context = cxt
        val channelId = "music"
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "MusicPlayer", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun updateNotification(){
        val currentSong = PlayPageViewModel.getCurrentSong()
        val artist = currentSong.getArtist()
        val title = currentSong.getTitle()
        var cover = currentSong.getCover()
        val builder = NotificationCompat.Builder(context, "music")
        val mediaSession = MediaSessionCompat(context, "tag")

        val playPauseIntent = Intent(context, NotificationControllerService::class.java).apply { action = "PLAY_PAUSE_ACTION" }
        val playPausePendingIntent = PendingIntent.getService(context, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE)
        val playPauseAction = NotificationCompat.Action.Builder(
            R.drawable.small_play,
            "Play/Pause",
            playPausePendingIntent
        ).build()
        val preSongIntent = Intent(context, NotificationControllerService::class.java).apply { action = "PRE_ACTION" }
        val preSongPendingIntent = PendingIntent.getService(context, 1, preSongIntent, PendingIntent.FLAG_IMMUTABLE)
        val preSongAction = NotificationCompat.Action.Builder(
            R.drawable.pre,
            "preSong",
            preSongPendingIntent
        ).build()
        val nextSongIntent = Intent(context, NotificationControllerService::class.java).apply { action = "NEXT_ACTION" }
        val nextSongPendingIntent = PendingIntent.getService(context, 2, nextSongIntent, PendingIntent.FLAG_IMMUTABLE)
        val nextSongAction = NotificationCompat.Action.Builder(
            R.drawable.next,
            "nextSong",
            nextSongPendingIntent
        ).build()
        val plus15SecIntent = Intent(context, NotificationControllerService::class.java).apply { action = "+15_SECOND" }
        val plus15SecPendingIntent = PendingIntent.getService(context, 3, plus15SecIntent, PendingIntent.FLAG_IMMUTABLE)
        val plus15SecAction = NotificationCompat.Action.Builder(
            R.drawable.plus15,
            "+15",
            plus15SecPendingIntent
        ).build()
        val minus15SecIntent = Intent(context, NotificationControllerService::class.java).apply { action = "-15_SECOND" }
        val minus15SecPendingIntent = PendingIntent.getService(context, 4, minus15SecIntent, PendingIntent.FLAG_IMMUTABLE)
        val minus15SecAction = NotificationCompat.Action.Builder(
            R.drawable.minus15,
            "-15",
            minus15SecPendingIntent
        ).build()

        val notification = builder
            .setSmallIcon(R.drawable.music)
            .setContentTitle(title)
            .setContentText(artist)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setLargeIcon(cover)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2) // 在壓縮視圖中顯示的操作按鈕索引
                    .setMediaSession(mediaSession.sessionToken)
            )
            .addAction(minus15SecAction)
            .addAction(preSongAction)
            .addAction(playPauseAction)
            .addAction(nextSongAction)
            .addAction(plus15SecAction)

        notificationManager.notify(0, notification.build())
    }
}