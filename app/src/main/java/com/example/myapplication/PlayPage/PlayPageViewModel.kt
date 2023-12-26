package com.example.myapplication.PlayPage

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.myapplication.Model.Song
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.Service.MusicPlayerService

object PlayPageViewModel{
    private var currentSongIndex = mutableStateOf(-1)
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
        currentSongIndex.value = 0
        currentSong = songList[currentSongIndex.value]
        musicBinder.initMediaPlayer(currentSong)
        return this@PlayPageViewModel
    }

    fun addCurrentSongObserver(observer:()->Unit){
        currentSongObserver = observer
    }

    fun mediaPlayerStartPause(){
        if(nowPlaying.value) mediaPlayerPause()
        else mediaPlayerStart()
    }
    fun mediaPlayerStart(){
        musicBinder.mediaStart()
    }
    fun mediaPlayerPause(){
        musicBinder.mediaPause()
    }
    fun setSong(nextIdx:Int, setIdx:Boolean=false){
        var nextSongIdx = if(setIdx) nextIdx else currentSongIndex.value+nextIdx
        nextSongIdx = if(nextSongIdx >= songList.size) 0 //boundary check
        else if(nextSongIdx < 0) songList.size-1 else nextSongIdx

        if(nextSongIdx != currentSongIndex.value){
            val nextSong = songList[nextSongIdx]
            if(musicBinder.setCurrentSong(nextSong)){
                currentSongIndex.value = nextSongIdx
                currentSong = songList[currentSongIndex.value]
            }
        }
    }
    fun readyToChangSong(){
        currentSongObserver.invoke()
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
    fun getCurrentSong():Song{
        return currentSong
    }
    fun getCurrentSongIndex():MutableState<Int>{
        return currentSongIndex
    }
    fun getNowPlaying():MutableState<Boolean>{
        return nowPlaying
    }
    fun setBinder(binder : MusicPlayerService.MusicBinder){
        musicBinder = binder
        musicBinder.setNowPlayingObserver(nowPlayingObserver)
    }
}