package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val songRepository = SongRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                songRepository.setContext(this)
                songRepository.initSongList()
                showNavPage()
            }
        }
    }
    @Composable
    fun showNavPage(){
        val coroutineScope = rememberCoroutineScope()
        val switchToMainPage = remember{ mutableStateOf(false) }

        LaunchedEffect(Unit){
            coroutineScope.launch {
                delay(1000)
                switchToMainPage.value = true
            }
        }
        Crossfade(targetState = switchToMainPage.value, animationSpec = tween(durationMillis = 1000),
            label = "navToMain"
        )
        { switchPage ->
            when(switchPage){
                true -> mainPage()
                false -> navPage()
            }
        }
    }

    enum class Page{
        SONGLIST, PLAY
    }
    @Composable
    fun mainPage(
        modifier: Modifier = Modifier
    ) {
        var curPage by remember { mutableStateOf(Page.SONGLIST) }
        var playPage by remember{ mutableStateOf(PlayPage(this, songRepository))}
        val changeSong:(Int) -> Unit = { nextSong->
            curPage = Page.PLAY
            playPage.setSong(nextSong)
        }
        var songListPage by remember{mutableStateOf(SongListPage(this, songRepository, changeSong))}
        createCustomNotification(playPage)

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Crossfade(targetState = curPage, animationSpec = tween(durationMillis = 300),
                    label = ""
                )
                { page ->
                    when(page){
                        Page.SONGLIST -> songListPage.showPage()
//                        Page.SONGLIST -> customNotification(playPage)
                        Page.PLAY -> playPage.showPage()
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ){
                MenuBar(
                    songListClicked = {curPage = Page.SONGLIST},
                    playClicked = {
                        curPage = Page.PLAY
                    }
                )
            }
        }
    }

    @Composable
    fun MenuBar(
        songListClicked: () -> Unit,
        playClicked: () -> Unit,
        modifier: Modifier = Modifier
    ){
        Row(
            modifier = Modifier
                .height(70.dp)
                .padding(horizontal = 3.dp, vertical = 10.dp)

        ){
            Button(
                onClick = {  songListClicked()  },
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                ),
            ) {
                Text("Song List")
            }
            Button(onClick = { playClicked() },
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                ),
            ) {
                Text("Now Playing")
            }
        }
    }
    @Composable
    fun navPage(){
        Surface(color = Color.Blue.copy(0.1f)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.spotify),
                    contentDescription = "Icon",
                    modifier = Modifier.size(300.dp),
                )
            }
        }
    }

    @Composable
    fun customNotification(playPage: PlayPage?){
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Yellow.copy(0.05f))
                .height(100.dp)
                .width(500.dp)
        ) {
            Row {
                playPage?.coverImage(
                    Modifier.weight(1f).height(50.dp).width(60.dp))
                playPage?.songInformation(
                    Modifier.weight(1f),
                    fontSize = 15
                )
                playPage?.progressBar(
                    Modifier.weight(1f).fillMaxWidth(),
                    fontSize = 8
                )
                playPage?.buttons(
                    Modifier.weight(1f).fillMaxWidth()
                )

            }

        }
    }

    fun createCustomNotification(playPage: PlayPage){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("music", "MusicPlayer", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        val builder = Notification.Builder(this, "music")
        builder.setSmallIcon(R.drawable.music)

            .setContentTitle("MusicPlayer")
            .setContentText("Now playing")

    }
}