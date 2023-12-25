package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import com.example.myapplication.Model.Song
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.PlayPage.PlayPageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicPlayerService: Service() {
    private var mediaPlayer = MediaPlayer()
    private var mediaPlayerReady = mutableStateOf(false)
    private lateinit var currentSong : Song

    override fun onBind(intent: Intent?): IBinder {
        return MusicBinder()
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
    inner class MusicBinder : Binder(){
        private lateinit var nowPlayingObserver : ((Boolean)->Unit)
        fun setNowPlayingObserver(observer:(Boolean)->Unit){
            nowPlayingObserver = observer
        }
        fun setCurrentSong(song : Song, initialize :Boolean = false){
            setMediaPlayer(song, initialize)
            currentSong = song
        }
        private fun setMediaPlayer(song: Song, initialize :Boolean = false){
            mediaPlayer.reset()
            mediaPlayer.setDataSource(SongRepository.getSongDataSource(song))
            mediaPlayer.prepare()
            mediaPlayer.setOnPreparedListener {
                mediaStart()
                PlayPageViewModel.readyToChangSong()
                if (initialize) mediaPause()
            }
            mediaPlayer.setOnCompletionListener {
                PlayPageViewModel.setSong(1)
            }
        }
        fun setMediaPosition(newPos:Int, based:Boolean=false){
            val new = if(based) getCurrentPosition()+newPos else newPos
            val duration = getDuration()

            if(new <= 0) mediaPlayer.seekTo(0)
            else if(new >= duration) mediaPlayer.seekTo(duration *1000)
            else mediaPlayer.seekTo(new*1000)
        }
        fun mediaStart(){
            mediaPlayer.start()
            nowPlayingObserver.invoke(mediaPlayer.isPlaying)
        }
        fun mediaPause(){
            mediaPlayer.pause()
            nowPlayingObserver.invoke(mediaPlayer.isPlaying)
        }
        fun getCurrentPosition():Int{
            return mediaPlayer.currentPosition/1000
        }
        fun getDuration():Int {
            return mediaPlayer.duration / 1000 + 1
        }
        fun getIsPlaying():Boolean{
            return mediaPlayer.isPlaying
        }
    }

}