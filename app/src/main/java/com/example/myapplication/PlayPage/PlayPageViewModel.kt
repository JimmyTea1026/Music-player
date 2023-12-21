package com.example.myapplication.PlayPage

import android.media.MediaPlayer
import androidx.compose.runtime.mutableStateOf
import com.example.myapplication.Model.Song
import com.example.myapplication.Model.SongRepository

object PlayPageViewModel{
    private var currentSongIndex = mutableStateOf(0)
    private var currentSongObserver: (()->Unit)? = null
    private var nowPlayingObserver: ((Boolean)->Unit)? = null
    private var songList : ArrayList<Song>
    private var currentSong : Song
    private var mediaPlayerReady = mutableStateOf(false)
    var mediaPlayer = MediaPlayer()
    init{
        songList = SongRepository.getSongList()
        currentSong = songList[currentSongIndex.value]
        setMediaPlayer()
    }

    fun addCurrentSongObserver(observer:()->Unit){
        currentSongObserver = observer
    }
    fun addNowPlayingObserver(observer:(Boolean)->Unit){
        nowPlayingObserver = observer
    }
    fun setMediaPlayer(){
        mediaPlayer.reset()
        mediaPlayerReady.value = false
        mediaPlayer.setDataSource(SongRepository.getSongDataSource(currentSong))
        mediaPlayer.prepare()
        mediaPlayer.setOnPreparedListener{
            mediaPlayerReady.value = true
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
            mediaPlayer.pause()
        }
        mediaPlayer.setOnCompletionListener {
            setSong(1)
        }

    }
    fun mediaPlayerStartPause(){
        if(mediaPlayerReady.value){
            if(mediaPlayer.isPlaying) mediaPlayerPause()
            else mediaPlayerStart()
        }
    }
    fun mediaPlayerStart(){
        mediaPlayer.start()
        nowPlayingObserver?.invoke(mediaPlayer.isPlaying)
    }
    fun mediaPlayerPause(){
        mediaPlayer.pause()
        nowPlayingObserver?.invoke(mediaPlayer.isPlaying)
    }
    fun setSong(nextIdx:Int, setIdx:Boolean=false){
        if(mediaPlayerReady.value){
            var nextSong = if(setIdx) nextIdx else currentSongIndex.value+nextIdx
            nextSong = if(nextSong >= songList.size) 0 else if(nextSong < 0) songList.size-1 else nextSong

            if(nextSong != currentSongIndex.value){
                mediaPlayer.stop()
                currentSongIndex.value = nextSong
                currentSong = songList[currentSongIndex.value]
                setMediaPlayer()
                nowPlayingObserver?.invoke(mediaPlayer.isPlaying)
                currentSongObserver?.invoke()
            }
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
    fun getCurrentPosition():Int{
        if(mediaPlayerReady.value) return mediaPlayer.currentPosition/1000
        else return 0
    }
    fun getDuration():Int{
        if(mediaPlayerReady.value) return mediaPlayer.duration/1000
        else return 1
    }
    fun getCurrentSong():Song{
        return currentSong
    }
}