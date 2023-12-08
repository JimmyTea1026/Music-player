package com.example.myapplication

class Song(){
    private val songTitle : String = ""
    private val author : String = ""
    private val path : String = ""
    private val songTime : Int = 0
    fun init(){

    }
    fun getTitle(): String {
        return songTitle
    }
    fun getAuthor(): String {
        return author
    }
    fun getTime(): Int {
        return songTime
    }
    fun getSong(): String {
        return songTitle
    }
}