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
        private var mediaPlayerJob: Job? = null
        private var lastRequestTime: Long = 0
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
        fun setNowPlayingObserver(observer:(Boolean)->Unit){
            nowPlayingObserver = observer
        }
        fun setCurrentSong(song : Song, initialize :Boolean = false):Boolean{
//            val curretTime = System.currentTimeMillis()
//            val n = curretTime - lastRequestTime
//            if(n < 2000) return false
//            lastRequestTime = curretTime
            currentSong = song
            setMediaPlayer(initialize)
            return true
        }
        private fun setMediaPlayer(initialize :Boolean = false){

            mediaPlayerJob?.cancel()
            mediaPlayer.reset()
            mediaPlayerReady.value = false

            mediaPlayer.setDataSource(SongRepository.getSongDataSource(currentSong))
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                mediaPlayerReady.value = true
                mediaStart()
                if (initialize) mediaPause()
            }
            mediaPlayer.setOnCompletionListener {
                PlayPageViewModel.setSong(1)
            }
        }
        fun setMediaPosition(newPos:Int, based:Boolean=false){
            if(mediaPlayerReady.value) {
                val new = if(based) getCurrentPosition()+newPos else newPos
                val duration = getDuration()

                if(new <= 0) mediaPlayer.seekTo(0)
                else if(new >= duration) mediaPlayer.seekTo(duration *1000)
                else mediaPlayer.seekTo(new*1000)
            }
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
            if(mediaPlayerReady.value) return mediaPlayer.currentPosition/1000
            else return 0
        }
        fun getDuration():Int{
            if(mediaPlayerReady.value) return mediaPlayer.duration/1000
            else return 1
        }
        fun getIsPlaying():Boolean{
            return mediaPlayer.isPlaying
        }
    }

}