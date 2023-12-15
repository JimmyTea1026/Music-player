package com.example.myapplication.SongList

import com.example.myapplication.MVVMDict
import com.example.myapplication.Model.Song
import com.example.myapplication.Model.SongRepository

class SongListViewModel(private val mvvmDict: MVVMDict) {
    private lateinit var songList : ArrayList<Song>
    private var observer: (()->Unit)? = null
    val candidate : MutableList<Int> = mutableListOf()

    init{
        MVVMDict.add("SongListViewModel", this)
    }
    fun initialize(){
        val re = MVVMDict.get("SongRepository") as SongRepository
        songList = re.getSongList()
    }
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