package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.renderscript.ScriptGroup.Input
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.PlayPage.PlayPageView
import com.example.myapplication.PlayPage.PlayPageViewModel
import com.example.myapplication.SongList.SongListView
import com.example.myapplication.SongList.SongListViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                /* TODO:
                    1.通知欄
                    2.Service
                    3.Wifi連接
                    4.LBT
                    5.切換歌曲動畫
                    6.轉橫向會壞掉
                    7.LiveDate 替換掉 LaunchedEffect
                 */
                SongRepository.initSongList()
                showNavPage()
                wifiConnection()
                createCustomNotification()
            }
        }
    }
    fun createCustomNotification(){
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//        }
        val artist = PlayPageViewModel.currentSong.getArtist()
        val title = PlayPageViewModel.currentSong.getTitle()
        val cover = PlayPageViewModel.currentSong.getCover()

        var playPauseIntent = Intent(this, MediaControlReceiver::class.java).apply {
            action = "PLAY_PAUSE_ACTION"
        }
        val playPausePendingIntent = PendingIntent.getBroadcast(this, 0,
            playPauseIntent, PendingIntent.FLAG_IMMUTABLE)
        val playPauseAction = Notification.Action(R.drawable.small_play, "", playPausePendingIntent)

        val nextIntent = Intent(this, MediaControlReceiver::class.java)
        nextIntent.action = "NEXT_ACTION"
        val nextPendingIntent = PendingIntent.getBroadcast(this, 0,
            nextIntent, PendingIntent.FLAG_IMMUTABLE)

        val preIntent = Intent(this, MediaControlReceiver::class.java)
        preIntent.action = "NEXT_ACTION"
        val prePendingIntent = PendingIntent.getBroadcast(this, 0,
            preIntent, PendingIntent.FLAG_IMMUTABLE)


        val channel = NotificationChannel("music", "MusicPlayer", NotificationManager.IMPORTANCE_LOW)
        val builder = Notification.Builder(this, "music")
        val notification = builder
            .setSmallIcon(R.drawable.music)
            .setContentTitle(title)
            .setContentText(artist)
            .setOngoing(true)
            .setVisibility(Notification.VISIBILITY_PRIVATE)
            .setLargeIcon(cover)
            .addAction(R.drawable.pre, "Previous", prePendingIntent) // #0
            .addAction(R.drawable.small_play, "Play/Pause", playPausePendingIntent) // #1
            .addAction(R.drawable.next, "Next", nextPendingIntent) // #2
            // Apply the media style template
//            .setStyle(MediaNotificationCompat.MediaStyle()
//                .setShowActionsInCompactView(1 /* #1: pause button \*/)
//                .setMediaSession(mediaSession.getSessionToken()))
            .build()

        val notificationManager = this.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(0, notification)
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
@Composable
fun mainPage(
    modifier: Modifier = Modifier
) {
    val songListView = SongListView
    val songListViewModel = SongListViewModel
    val playPageView = PlayPageView
    val playPageViewModel = PlayPageViewModel
    var switchToPlayPage by remember { mutableStateOf(false) }
    LaunchedEffect(songListViewModel.onChangeSongIndex.value){
        if(songListViewModel.onChangeSongIndex.value >= 0){
            playPageViewModel.setSong(songListViewModel.onChangeSongIndex.value, true)
            switchToPlayPage = true
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Crossfade(targetState = switchToPlayPage, animationSpec = tween(durationMillis = 300),
                label = ""
            )
            { page ->
                when(page){
                    false -> songListView.showPage()
                    true -> playPageView.showPage()
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ){
            MenuBar(
                songListClicked = {switchToPlayPage = false},
                playClicked = {
                    switchToPlayPage = true
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
        Button(
            onClick = { playClicked() },
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
fun wifiConnection(){
    fun createServer():ServerSocket{
        return ServerSocket(8888)
    }
    var serverSocket by remember { mutableStateOf<ServerSocket?>(null) }
    val coroutineScope = rememberCoroutineScope()
    if(serverSocket == null){
        LaunchedEffect(true){
            coroutineScope.launch {
                withContext(Dispatchers.IO){
                    serverSocket = createServer()
                    val clientSocket:Socket = serverSocket!!.accept()
                    Log.d("", "Client connected: ${clientSocket.inetAddress}")
                    val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                    var message = ""
                    while(true){
                        message = reader.readLine()
                        Log.d("接收到訊息", message)
                        if(message == "exit") break
                        else mediaPlayerController(message)
                    }

                    reader.close()
                    clientSocket.close()
                    serverSocket?.close()
                }
            }
        }
    }
}

fun mediaPlayerController(cmd:String){
    if(cmd == "p") {
        if(PlayPageViewModel.mediaPlayer.isPlaying) PlayPageViewModel.mediaPlayerPause()
        else PlayPageViewModel.mediaPlayerStart()
    }
    else if(cmd == "pre") PlayPageViewModel.setSong(-1)
    else if(cmd == "next") PlayPageViewModel.setSong(1)
    else if(cmd == "+15") PlayPageViewModel.setMediaPosition(15, true)
    else if(cmd == "-15") PlayPageViewModel.setMediaPosition(-15, true)
}