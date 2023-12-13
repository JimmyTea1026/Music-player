package com.example.myapplication

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


class PlayPage(context:Context?, songList: ArrayList<Song>){
    val assetManager = context!!.assets
    val songList = songList
    val currentSong = mutableStateOf(0)
    var song = songList[0]
    val mediaPlayer = MediaPlayer()
    init{
        initMediaPlayer()
    }
    fun initMediaPlayer(){
        try{
            mediaPlayer.reset()
            val path = song.getPath()
            val des = assetManager.openFd(path)
            mediaPlayer.setDataSource(des)
            mediaPlayer.prepare()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun setSong(nextIdx:Int){
        var nextSong = nextIdx
        if(nextSong >= songList.size){
            nextSong = 0
        }
        else if(nextSong < 0){
            nextSong = songList.size-1
        }
        if(nextSong != currentSong.value){
            currentSong.value = nextSong
            song = songList[currentSong.value]
            initMediaPlayer()
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
        }
    }

    @Composable
    fun showPage(
        modifier: Modifier = Modifier
    ) {
        val resetPage = remember{ mutableStateOf(false) }
        LaunchedEffect(key1 = this.currentSong.value){
            resetPage.value = true
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ){
            coverImage(
                Modifier
                    .padding(top = 20.dp)
                    .height(400.dp)
                    .width(500.dp))
            songInformation(
                Modifier
                    .weight(1f)
                    .fillMaxSize())
            buttons(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                )
            progressBar(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                )
        }
    }
    @Composable
    fun coverImage(
        modifer : Modifier = Modifier
    ){
        val coverPath = song.getCoverPath()
        val cover = BitmapFactory.decodeStream(assetManager.open(coverPath))

        Box(modifier = modifer) {
            Image(
                bitmap = cover.asImageBitmap(),
                contentDescription = "Icon",
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
    @Composable
    fun songInformation(
        modifer : Modifier = Modifier
    ){
        var title = song.getTitle()
        var artist = song.getArtist()
        Box(modifier = modifer){
            Column(modifier = modifer) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 30.sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                Text(
                    text = artist,
                    style = TextStyle(fontSize = 16.sp),
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                )
            }
        }
    }
    @Composable
    fun buttons(
        modifer : Modifier = Modifier
    ){
        Box(modifier = modifer){
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    val iconSize = 50.dp
                    IconButton(
                        modifier = Modifier.weight(.1f),
                        onClick = { setSong(currentSong.value-1) }
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft, contentDescription = "Last Song",
                            modifier = Modifier.size(iconSize),
                        )
                    }

                    val checkedState = remember{ mutableStateOf(mediaPlayer.isPlaying) }
                    IconToggleButton(
                        modifier = Modifier.weight(.1f),
                        checked = checkedState.value,
                        onCheckedChange = {
                                if(checkedState.value) {
                                    mediaPlayer.pause()
                                    checkedState.value = false
                                }
                                else{
                                    mediaPlayer.start()
                                    checkedState.value = true
                                }
                            },
                    ) {
                        val playIcon = painterResource(id = R.drawable.play)
                        val pauseIcon = painterResource(id = R.drawable.pause)
                        var icon = if (checkedState.value) pauseIcon else playIcon
                        Icon(
                            painter = icon, contentDescription = "Play/Pause",
                            modifier = Modifier.size(iconSize),
                        )
                    }

                    IconButton(
                        modifier = Modifier.weight(.1f),
                        onClick = { setSong(currentSong.value+1) }
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowRight, contentDescription = "Next Song",
                            modifier = Modifier.size(iconSize),
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun progressBar(
        modifer : Modifier = Modifier
    ){
        val duration = mediaPlayer.duration/1000
        var curPos by remember { mutableStateOf(0) }
        var isPlaying by remember{ mutableStateOf(mediaPlayer.isPlaying) }
        LaunchedEffect(isPlaying) {
            while (true) {
                curPos = mediaPlayer.currentPosition/1000
                delay(100)
            }
        }
        val progress = curPos.toFloat()/duration.toFloat()
        Box(modifier = modifer){
            Column() {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                )

                Row(){
                    val curMinute : Int = curPos/60
                    val curSecond : Int = curPos%60
                    val curStrMin : String = "0$curMinute"
                    val curStrSec : String = if(curSecond>=10) "$curSecond" else "0$curSecond"
                    Text(
                        text = "$curStrMin : $curStrSec",
                        style = TextStyle(fontSize = 12.sp),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(end = 15.dp)
                    )

                    val minute : Int = duration/60
                    val second : Int = duration%60
                    val strMin : String = "0$minute"
                    val strSec : String = if(second>=10) "$second" else "0$second"
                    Text(
                        text = "$strMin : $strSec",
                        style = TextStyle(fontSize = 12.sp),
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(end = 5.dp)
                    )
                }
            }
        }
    }
}
