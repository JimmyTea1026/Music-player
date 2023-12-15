package com.example.myapplication.Model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.myapplication.MVVMDict

class SongRepository(private val mvvmDict: MVVMDict) {
    private val songList = ArrayList<Song>()
    private val context = MVVMDict.get("Context") as Context
    init{
        mvvmDict.add("SongRepository", this)
    }
    fun getSongList():ArrayList<Song>{
        return songList
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
                        val cover = BitmapFactory.decodeStream(assetManager.open(coverPath))
                        song.setInformation(
                            songTitle = songTitle,
                            cover = cover,
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