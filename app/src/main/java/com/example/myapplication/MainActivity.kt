package com.example.myapplication

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.PlayPage.PlayPageView
import com.example.myapplication.PlayPage.PlayPageViewModel
import com.example.myapplication.SongList.SongListView
import com.example.myapplication.SongList.SongListViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private lateinit var serviceConnection : ServiceConnection
    private lateinit var notificationManager : NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                /* TODO:
                    1.通知欄
                    2.Service v
                    3.Wifi連接 v
                    4.LBT v
                    5.切換歌曲動畫
                    6.轉橫向會壞掉
                    7.LiveDate 替換掉 LaunchedEffect
                 */
                SongRepository.initSongList(this.assets)
                showNavPage()
                requestPermissions()
                startMusicPlayerService()
                createCustomNotification()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
    private fun startBLEService(){
        val bleIntent = Intent(this, BluetoothLeService::class.java)
        startService(bleIntent)
    }
    private fun startWifiService(){
        val wifiIntent = Intent(this, WifiManagerService::class.java)
        startService(wifiIntent)
    }
    private fun startMusicPlayerService(){
        val musicIntent = Intent(this, MusicPlayerService::class.java)
        var musicBinder : MusicPlayerService.MusicBinder? = null
        serviceConnection = object : ServiceConnection{
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                musicBinder = service as MusicPlayerService.MusicBinder
                PlayPageViewModel.setBinder(musicBinder!!)
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                musicBinder = null
            }
        }

        bindService(musicIntent, serviceConnection, Context.BIND_AUTO_CREATE)

    }
    @Composable
    private fun showNavPage(){
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
    fun mainPage() {
        val songListViewModel by remember { mutableStateOf(SongListViewModel.initSongList())}
        val playPageViewModel by remember { mutableStateOf(PlayPageViewModel.initSongList())}
        val songListView = SongListView
        val playPageView = PlayPageView

        var switchToPlayPage by remember { mutableStateOf(false) }
        LaunchedEffect(songListViewModel.onChangeSong.value){
            if(songListViewModel.onChangeSongIndex.value >= 0){
                playPageViewModel.setSong(songListViewModel.onChangeSongIndex.value, true)
                switchToPlayPage = true
            }
        }
        LaunchedEffect(playPageViewModel.getCurrentSongIndex().value){
            updateNotification()
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
    fun MenuBar(songListClicked: () -> Unit, playClicked: () -> Unit,){
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

    private val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions.entries.all { it.value }
                if (granted) {
                    Log.d("Permission", "Get all permission")
                    startBLEService()
                    startWifiService()
                } else {
                    Log.d("Permission", "no permission")
                }
            }

    private val requestEnableBt =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("Bluetooth", "Bluetooth ON")
            } else {
                Log.d("Bluetooth", "Bluetooth off")
            }
        }
    private fun requestPermissions() {
        val bluetoothmanager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothmanager.adapter
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestEnableBt.launch(enableBtIntent)
        }

        val permissions = arrayOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        requestMultiplePermissions.launch(permissions)
    }

    private fun createCustomNotification(){
        val channelId = "music_channel"
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "MusicPlayer", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun updateNotification(){
        val currentSong = PlayPageViewModel.getCurrentSong()
        val artist = currentSong.getArtist()
        val title = currentSong.getTitle()
        var cover = currentSong.getCover()
        val builder = NotificationCompat.Builder(this, "music")
        val mediaSession = MediaSessionCompat(this, "tag")

        val playPauseIntent = Intent(this, NotificationControllerService::class.java).apply { action = "PLAY_PAUSE_ACTION" }
        val playPausePendingIntent = PendingIntent.getService(this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE)
        val playPauseAction = NotificationCompat.Action.Builder(
            R.drawable.small_play,
            "Play/Pause",
            playPausePendingIntent
        ).build()
        val preSongIntent = Intent(this, NotificationControllerService::class.java).apply { action = "PRE_ACTION" }
        val preSongPendingIntent = PendingIntent.getService(this, 1, preSongIntent, PendingIntent.FLAG_IMMUTABLE)
        val preSongAction = NotificationCompat.Action.Builder(
            R.drawable.pre,
            "preSong",
            preSongPendingIntent
        ).build()
        val nextSongIntent = Intent(this, NotificationControllerService::class.java).apply { action = "NEXT_ACTION" }
        val nextSongPendingIntent = PendingIntent.getService(this, 2, nextSongIntent, PendingIntent.FLAG_IMMUTABLE)
        val nextSongAction = NotificationCompat.Action.Builder(
            R.drawable.next,
            "nextSong",
            nextSongPendingIntent
        ).build()
        val plus15SecIntent = Intent(this, NotificationControllerService::class.java).apply { action = "+15_SECOND" }
        val plus15SecPendingIntent = PendingIntent.getService(this, 3, plus15SecIntent, PendingIntent.FLAG_IMMUTABLE)
        val plus15SecAction = NotificationCompat.Action.Builder(
            R.drawable.plus15,
            "+15",
            plus15SecPendingIntent
        ).build()
        val minus15SecIntent = Intent(this, NotificationControllerService::class.java).apply { action = "-15_SECOND" }
        val minus15SecPendingIntent = PendingIntent.getService(this, 4, minus15SecIntent, PendingIntent.FLAG_IMMUTABLE)
        val minus15SecAction = NotificationCompat.Action.Builder(
            R.drawable.minus15,
            "-15",
            minus15SecPendingIntent
        ).build()

        val notification = builder
            .setSmallIcon(R.drawable.music)
            .setContentTitle(title)
            .setContentText(artist)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setLargeIcon(cover)
            .setStyle(
                MediaStyle()
                    .setShowActionsInCompactView(0,1,2) // 在壓縮視圖中顯示的操作按鈕索引
                    .setMediaSession(mediaSession.sessionToken)
            )
            .addAction(minus15SecAction)
            .addAction(preSongAction)
            .addAction(playPauseAction)
            .addAction(nextSongAction)
            .addAction(plus15SecAction)

        notificationManager.notify(0, notification.build())
    }
}
