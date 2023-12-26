package com.example.myapplication.Model

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


object SongRepository {
    private val songList = ArrayList<Song>()
    private lateinit var assetManager : AssetManager
    fun getSongList():ArrayList<Song>{
        return songList
    }
    @Composable
    fun initSongList(){
        songList.clear()
        this.assetManager = LocalContext.current.assets
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
    fun getSongDataSource(song: Song) : AssetFileDescriptor {
        val path = song.getPath()
        val des = assetManager.openFd(path)
        return des
    }
}