package com.example.myapplication.PlayPage

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.example.myapplication.Model.Song
import com.example.myapplication.Model.SongRepository
import kotlinx.coroutines.delay
import java.io.IOException

object PlayPageViewModel{
    private var currentSongIndex = mutableStateOf(0)
    private var currentSongObserver: (()->Unit)? = null
    private var songList : ArrayList<Song>
    var currentSong : Song
    var mediaPlayer : MediaPlayer
    init{
        songList = SongRepository.getSongList()
        currentSong = songList[currentSongIndex.value]
        mediaPlayer = MediaPlayer()
        initMediaPlayer()
    }

    fun addCurrentSongObserver(observer:()->Unit){
        currentSongObserver = observer
    }
    fun initMediaPlayer(){
        try{
            mediaPlayer.reset()
            mediaPlayer.setOnCompletionListener {
                setSong(1)
            }
            mediaPlayer.setDataSource(SongRepository.getSongDataSource(currentSong))
            mediaPlayer.prepare()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    fun mediaPlayerPause(){mediaPlayer.pause()}
    fun mediaPlayerStart(){
        mediaPlayer.start()
    }
    fun setSong(nextIdx:Int, setIdx:Boolean=false){
        var nextSong = if(setIdx) nextIdx else currentSongIndex.value+nextIdx
        if(nextSong >= songList.size){
            nextSong = 0
        }
        else if(nextSong < 0){
            nextSong = songList.size-1
        }
        if(nextSong != currentSongIndex.value){
            mediaPlayer.stop()
            currentSongIndex.value = nextSong
            currentSong = songList[currentSongIndex.value]
            initMediaPlayer()
            mediaPlayer.start()
            mediaPlayer.seekTo(0)
        }
        currentSongObserver?.invoke()
    }
    fun setMediaPosition(newPos:Int){
        mediaPlayer.seekTo(newPos)
    }
    fun getCurrentPosition():Int{
        return mediaPlayer.currentPosition/1000
    }
    fun getDuration():Int{
        return mediaPlayer.duration/1000
    }
}