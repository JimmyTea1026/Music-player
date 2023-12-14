package com.example.myapplication.Model
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Path
import android.media.MediaMetadataRetriever

class Song() {
    private var songPath: String = ""
    private var songTitle: String = ""
    private var artist: String = ""
    private lateinit var cover : Bitmap
    fun setInformation(songTitle:String, cover:Bitmap, songPath: String
    , artist: String){
        this.songTitle = songTitle
        this.songPath = songPath
        this.artist = artist
        this.cover = cover
    }
    fun getTitle(): String {
        return songTitle
    }

    fun getArtist(): String {
        return artist
    }

    fun getCover(): Bitmap {
        return cover
    }

    fun getPath(): String{
        return songPath
    }
}