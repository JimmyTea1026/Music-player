package com.example.myapplication.PlayPage

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.myapplication.Model.Song
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.MusicPlayerService

object PlayPageViewModel{
    private var currentSongIndex = mutableStateOf(0)
    private lateinit var currentSong : Song
    private lateinit var currentSongObserver: (()->Unit)
    private lateinit var songList : ArrayList<Song>
    private lateinit var musicBinder: MusicPlayerService.MusicBinder
    private val nowPlaying = mutableStateOf(false)
    private val nowPlayingObserver: (Boolean)->Unit = {isPlaying->
        nowPlaying.value = isPlaying
    }
    fun initSongList():PlayPageViewModel{
        songList = SongRepository.getSongList()
        currentSong = songList[currentSongIndex.value]
        musicBinder.setCurrentSong(currentSong, initialize = true)
        return this@PlayPageViewModel
    }

    fun addCurrentSongObserver(observer:()->Unit){
        currentSongObserver = observer
    }

    fun mediaPlayerStartPause(){
        if(musicBinder.getIsPlaying()) mediaPlayerPause()
        else mediaPlayerStart()
    }
    fun mediaPlayerStart(){
        musicBinder.mediaStart()
    }
    fun mediaPlayerPause(){
        musicBinder.mediaPause()
    }
    fun setSong(nextIdx:Int, setIdx:Boolean=false){
        var nextSong = if(setIdx) nextIdx else currentSongIndex.value+nextIdx
        nextSong = if(nextSong >= songList.size) 0 else if(nextSong < 0) songList.size-1 else nextSong
        if(nextSong != currentSongIndex.value){
            currentSongIndex.value = nextSong
            currentSong = songList[currentSongIndex.value]
            musicBinder.setCurrentSong(currentSong)
            currentSongObserver.invoke()
        }
    }
    fun setMediaPosition(newPos: Int, based : Boolean = false) {
        musicBinder.setMediaPosition(newPos, based)
    }
    fun getDuration(): Int{
        return musicBinder.getDuration()
    }
    fun getCurrentPosition(): Int{
        return musicBinder.getCurrentPosition()
    }
    fun getCurrentSong():Song?{
        return if(::currentSong.isInitialized) currentSong else null
    }
    fun getNowPlaying():MutableState<Boolean>{
        return nowPlaying
    }
    fun setBinder(binder : MusicPlayerService.MusicBinder){
        musicBinder = binder
        musicBinder.setNowPlayingObserver(nowPlayingObserver)
    }
}