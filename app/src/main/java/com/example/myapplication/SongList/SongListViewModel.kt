//package com.example.myapplication.SongList
//
//import com.example.myapplication.MVVMDict
//import com.example.myapplication.Model.SongRepository
//
//class SongListViewModel(private val mvvmDict: MVVMDict) {
//    private val candidate = arrayListOf<Int>()
//    private val re = MVVMDict.get("SongRepository")
//    init{
//        MVVMDict.add("SongListViewModel", this)
//    }
//    fun search(searchTitle:String){
//        if (searchTitle=="") candidate.clear()
//        else{
//            songList.forEachIndexed{index, song ->
//                val songTitle = song.getTitle().lowercase()
//                searchTitle.lowercase()
//                if(songTitle.contains(searchTitle))
//                    candidate += index
//            }
//            return candidate
//        }
//    }
//}