package com.example.myapplication.PlayPage

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.MVVMDict
import com.example.myapplication.Model.Song
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.SongList.SongListViewModel
import kotlinx.coroutines.delay
import java.io.IOException

class PlayPageViewModel(private val mvvmDict:MVVMDict) {
    private val context = mvvmDict.get("Context") as Context
    private val assetManager = context.assets
    private var currentSongIndex = mutableStateOf(0)
    private var currentSongObserver: (()->Unit)? = null
    lateinit var currentSong : Song
    private lateinit var songList : ArrayList<Song>
    val mediaPlayer = MediaPlayer()
    init{
        mvvmDict.add("PlayPageViewModel", this)
    }
    fun addCurrentSongObserver(observer:()->Unit){
        currentSongObserver = observer
    }
    fun initialize(){
        songList = (MVVMDict.get("SongRepository") as SongRepository).getSongList()
        currentSong = songList[currentSongIndex.value]
        initMediaPlayer()
    }
    fun initMediaPlayer(){
        try{
            mediaPlayer.reset()
            val path = currentSong.getPath()
            val des = assetManager.openFd(path)
            mediaPlayer.setDataSource(des)
            mediaPlayer.prepare()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    fun mediaPlayerPause(){mediaPlayer.pause()}
    fun mediaPlayerStart(){mediaPlayer.start()}
    fun setSong(nextIdx:Int){
        var nextSong = currentSongIndex.value+nextIdx
        if(nextSong >= songList.size){
            nextSong = 0
        }
        else if(nextSong < 0){
            nextSong = songList.size-1
        }
        if(nextSong != currentSongIndex.value){
            currentSongIndex.value = nextSong
            currentSong = songList[currentSongIndex.value]
            initMediaPlayer()
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
        }
        currentSongObserver?.invoke()
    }
    @Composable
    fun songOver(){
        LaunchedEffect(mediaPlayer.currentPosition){
            while(true){
                if(mediaPlayer.currentPosition == mediaPlayer.duration) setSong(1)
                delay(500)
            }
        }
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