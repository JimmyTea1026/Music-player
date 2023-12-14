package com.example.myapplication

import android.content.Context

class SongRepository {
    private val songList = ArrayList<Song>()
    private lateinit var context:Context
    fun getSongList():ArrayList<Song>{
        return songList
    }
    fun setContext(cxt: Context){
        context = cxt
    }
    fun initSongList(){
        val assetManager = context.assets
        val path = "music"
        val assetList = assetManager.list(path)
        if (assetList != null) {
            for (artist in assetList) {
                var artist = artist
                var coverPath = ""
                var songPath = ""
                var songTitle = ""

                val songs = assetManager.list("$path/$artist")
                if (songs != null) {
                    for (title in songs) {
                        songTitle = title
                        val files = assetManager.list("$path/$artist/$title")
                        if (files != null) {
                            for (file in files) {
                                val filePath = "$path/$artist/$title/$file"
                                if (filePath.endsWith(".mp3")) songPath = filePath
                                else coverPath = filePath
                            }
                        }
                        val song: Song = Song()
                        song.setInformation(
                            songTitle = songTitle,
                            coverPath = coverPath,
                            songPath = songPath,
                            artist = artist
                        )
                        songList += song
                    }
                }
            }
        }
    }
}