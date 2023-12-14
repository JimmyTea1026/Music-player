package com.example.myapplication.Model
import android.graphics.Bitmap
import android.graphics.Path
import android.media.MediaMetadataRetriever

class Song() {
    private var songPath: String = ""
    private var songTitle: String = ""
    private var artist: String = ""
    private var coverPath: String = ""
    fun setInformation(songTitle:String, coverPath:String, songPath: String
    , artist: String){
        this.songTitle = songTitle
        this.songPath = songPath
        this.coverPath = coverPath
        this.artist = artist
    }
    fun getTitle(): String {
        return songTitle
    }

    fun getArtist(): String {
        return artist
    }

    fun getCoverPath(): String {
        return coverPath
    }

    fun getPath(): String{
        return songPath
    }
}