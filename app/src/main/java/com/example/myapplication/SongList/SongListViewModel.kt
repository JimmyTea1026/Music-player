package com.example.myapplication.SongList

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.myapplication.Model.SongRepository

object SongListViewModel {
    private var observer: (()->Unit)? = null
    private var songList = SongRepository.getSongList()
    val candidate : MutableList<Int> = mutableListOf()
    val onChangeSongIndex : MutableState<Int> = mutableStateOf(-1)
    val changeSong:(Int) -> Unit = { songIndex-> onChangeSongIndex.value = songIndex }

    fun addObserver(observer: ()->Unit){
        this.observer = observer
    }
    fun search(searchTitle:String){
        if (searchTitle=="") candidate.clear()
        else{
            songList.forEachIndexed{index, song ->
                val songTitle = song.getTitle().lowercase()
                searchTitle.lowercase()
                if(songTitle.contains(searchTitle))
                    candidate.add(index)
            }
        }
        observer?.invoke()
    }

}