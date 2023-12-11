package com.example.myapplication

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme


class PlayPage(context:Context?){
//    val mediaPlayer = MediaPlayer()
//    var currentSong : Song = Song()
//    val assetManager = context.assets
//    fun setSong(song: Song){
//        if(song.getTitle() != currentSong.getTitle()){
//            currentSong = song
//            val des = assetManager.openFd(currentSong.getPath())
//            mediaPlayer.setDataSource(des)
//            mediaPlayer.prepare()
//        }
//    }
    @Composable
    fun showPage(
        modifier: Modifier = Modifier
    ) {
//        var currentSong = remember { mutableStateOf(currentSong) }
        var title = "アイドル"
        var author = "YOASOBI"
        var time = 100
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
                    .padding(top = 10.dp))
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
        Box(modifier = modifer) {
            Image(
                painter = painterResource(id = R.drawable.cover),
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
        var title = "アイドル"
        var author = "YOASOBI"
        Box(modifier = modifer){
            Column(modifier = modifer) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 36.sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                Text(
                    text = author,
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
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft, contentDescription = "Last Song",
                            modifier = Modifier.size(iconSize),
                        )
                    }

                    val checkedState = remember{ mutableStateOf(true) }
                    IconToggleButton(
                        modifier = Modifier.weight(.1f),
                        checked = checkedState.value,
                        onCheckedChange = {checkedState.value=it}
                    ) {
                        val playIcon = painterResource(id = R.drawable.play)
                        val pauseIcon = painterResource(id = R.drawable.pause)
                        var icon = if (checkedState.value) playIcon else pauseIcon
                        Icon(
                            painter = icon, contentDescription = "Play/Pause",
                            modifier = Modifier.size(iconSize),
                        )
                    }

                    IconButton(
                        modifier = Modifier.weight(.1f),
                        onClick = { /*TODO*/ }
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
        Box(modifier = modifer){
            Column() {
                LinearProgressIndicator(
                    progress = 0.5f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                )
                val minute : Int = 0/60
                val second : Int = 0%60
                val str_min : String = "0$minute"
                val str_sec : String = if(second>10) "$second" else "0$second"
                Text(
                    text = "$str_min : $str_sec",
                    style = TextStyle(fontSize = 12.sp),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 5.dp)
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun playPagePreview(){
    MyApplicationTheme {
        val pp = PlayPage(null)
        pp.showPage()
    }
}