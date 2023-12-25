package com.example.myapplication.PlayPage

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object PlayPageView {
    private val viewModel = PlayPageViewModel
    private val currentSongChanged = mutableStateOf(1)
    private val currentSongObserver: ()->Unit = {
        currentSongChanged.value += 1
    }

    init{
        viewModel.addCurrentSongObserver(currentSongObserver)
    }
    @Composable
    fun showPage(
        modifier: Modifier = Modifier
    ) {
        if(LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Row(modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
            ){
                coverImage(modifier = Modifier
                    .size(350.dp)
                )
                Box(modifier = Modifier.weight(3f).padding(start = 15.dp)){
                    Column(modifier = Modifier
                        .fillMaxSize()) {
                        songInformation(
                            modifier = Modifier.weight(1f),
                            fontSize = 26)
                        buttons(modifier = Modifier.weight(1f))
                        progressBar(modifier = Modifier.weight(1f) ,
                            fontSize = 12)
                    }
                }

            }
        }
        else{
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
                        .fillMaxSize(),
                    fontSize = 26
                )
                buttons(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(top = 10.dp),
                )
                progressBar(
                    Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    fontSize = 12
                )
            }
        }
    }
    @Composable
    fun coverImage(
        modifier : Modifier = Modifier
    ){
        var cover by remember { mutableStateOf<Bitmap>(viewModel.getCurrentSong().getCover()) }
        LaunchedEffect(currentSongChanged.value){
            cover = viewModel.getCurrentSong().getCover()
        }

        val coroutineScope = rememberCoroutineScope()
        var lastEventTimestamp by remember{ mutableLongStateOf(0L) }
        Crossfade(targetState = cover, label = "changeSong") { nextCover->
            Box(modifier = modifier
            ) {
                Image(
                    bitmap = nextCover.asImageBitmap(),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, _, _ ->
                                val currentTimestamp = System.currentTimeMillis()
                                if (currentTimestamp - lastEventTimestamp > 100L) {
                                    lastEventTimestamp = currentTimestamp
                                    coroutineScope.launch {
                                        delay(200)
                                        if (pan.x > 50) viewModel.setSong(-1)
                                        else if (pan.x < -50) viewModel.setSong(1)
                                    }
                                }
                            }
                        }
                )
            }
        }
    }
    @Composable
    fun songInformation(
        modifier : Modifier = Modifier,
        fontSize : Int
    ){
        var currentSong by remember{ mutableStateOf(viewModel.getCurrentSong()) }
        LaunchedEffect(currentSongChanged.value){
            currentSong = viewModel.getCurrentSong()
        }
        Box(modifier = modifier) {
            Crossfade(targetState = currentSong, label = "changeSong") { song ->
                Column(modifier = modifier) {
                    Text(
                        text = song.getTitle(),
                        style = TextStyle(fontSize = fontSize.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    )
                    Text(
                        text = song.getArtist(),
                        style = TextStyle(fontSize = (fontSize / 2).sp),
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    )
                }
            }
        }
    }
    @Composable
    fun buttons(
        modifier : Modifier = Modifier
    ){
        Box(modifier = modifier){
            Box(
                modifier = Modifier
                ,
                contentAlignment = Alignment.Center
            ){
                Row(
                    modifier = Modifier
                    ,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    val iconSize = 50.dp
                    IconButton(
                        modifier = Modifier.weight(.1f),
                        onClick = { viewModel.setSong(-1) }
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft, contentDescription = "Last Song",
                            modifier = Modifier.size(iconSize),
                        )
                    }

                    var isPlaying by remember{ mutableStateOf(false) }
                    LaunchedEffect(key1 = viewModel.getNowPlaying().value){
                        isPlaying = viewModel.getNowPlaying().value
                    }
                    IconToggleButton(
                        modifier = Modifier.weight(.1f),
                        checked = isPlaying,
                        onCheckedChange = {
                            viewModel.mediaPlayerStartPause()
                        },
                    ) {
                        val playIcon = painterResource(id = R.drawable.play)
                        val pauseIcon = painterResource(id = R.drawable.pause)
                        var icon = if (isPlaying) pauseIcon else playIcon
                        Icon(
                            painter = icon, contentDescription = "Play/Pause",
                            modifier = Modifier.size(iconSize),
                        )
                    }

                    IconButton(
                        modifier = Modifier.weight(.1f),
                        onClick = {
                            viewModel.setSong(1)
                        }
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
        modifier : Modifier = Modifier,
        fontSize: Int
    ){
        var duration by remember { mutableStateOf(1) }
        var curPos by remember { mutableStateOf(0) }
        LaunchedEffect(viewModel.getNowPlaying().value) {
            while (true) {
                curPos = viewModel.getCurrentPosition()
                duration = viewModel.getDuration()
                delay(500)
            }
        }
        var isUserChangingSlider by remember{ mutableStateOf(false) }
        var sliderValue by remember{ mutableStateOf(0f) }
        var playingWhenChange by remember{ mutableStateOf(false) }

        Box(modifier = modifier){
            Column() {
                Slider(
                    value = if (isUserChangingSlider) sliderValue else curPos.toFloat() / duration.toFloat(),
                    onValueChange = {newValue ->
                        curPos = (newValue*duration).toInt()
                        sliderValue = newValue
                        if(viewModel.getNowPlaying().value) playingWhenChange = true
                        isUserChangingSlider = true
                        viewModel.mediaPlayerPause()
                    },
                    onValueChangeFinished = {
                        val newPos = (sliderValue*duration).toInt()
                        viewModel.setMediaPosition(newPos)
                        if(playingWhenChange) viewModel.mediaPlayerStart()
                        isUserChangingSlider = false
                        playingWhenChange = false
                    }
                )
                Row(){
                    val curMinute : Int = curPos/60
                    val curSecond : Int = curPos%60
                    val curStrMin : String = "0$curMinute"
                    val curStrSec : String = if(curSecond>=10) "$curSecond" else "0$curSecond"
                    Text(
                        text = "$curStrMin : $curStrSec",
                        style = TextStyle(fontSize = fontSize.sp),
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
                        style = TextStyle(fontSize = fontSize.sp),
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