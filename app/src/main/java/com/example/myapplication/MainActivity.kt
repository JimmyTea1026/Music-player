package com.example.myapplication

import android.app.Activity
import com.example.myapplication.BluetoothLeService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.PlayPage.MusicPlayerService
import com.example.myapplication.PlayPage.PlayPageView
import com.example.myapplication.PlayPage.PlayPageViewModel
import com.example.myapplication.SongList.SongListView
import com.example.myapplication.SongList.SongListViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var serviceConnection : ServiceConnection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                /* TODO:
                    1.通知欄
                    2.Service
                    3.Wifi連接 v
                    4.LBT v
                    5.切換歌曲動畫
                    6.轉橫向會壞掉
                    7.LiveDate 替換掉 LaunchedEffect
                 */
                SongRepository.initSongList(
                    assetManager= LocalContext.current.assets)
                showNavPage()
                createCustomNotification(this)
                requestPermissions()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    fun startBLEService(){
        val intent = Intent(this, BluetoothLeService::class.java)
        startService(intent)
    }
    fun startWifiService(){
        val intent = Intent(this, WifiManagerService::class.java)
        startService(intent)
    }
    fun startMusicPlayerService(){
        val intent = Intent(this, MusicPlayerService::class.java)
        serviceConnection = object : ServiceConnection{
            var musicBinder : MusicPlayerService.MusicBinder? = null
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                musicBinder = service as MusicPlayerService.MusicBinder
                PlayPageViewModel.setBinder(musicBinder!!)
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                musicBinder = null
            }
        }
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
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
    fun mainPage() {
        val songListView = SongListView
        val songListViewModel = SongListViewModel
        val playPageView = PlayPageView
        val playPageViewModel = PlayPageViewModel
        startMusicPlayerService()
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
}


fun createCustomNotification(context:Context){
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//        }
    val artist = PlayPageViewModel.getCurrentSong().getArtist()
    val title = PlayPageViewModel.getCurrentSong().getTitle()
    val cover = PlayPageViewModel.getCurrentSong().getCover()

    var playPauseIntent = Intent(context, MediaControlReceiver::class.java).apply {
        action = "PLAY_PAUSE_ACTION"
    }
    val playPausePendingIntent = PendingIntent.getBroadcast(context, 0,
        playPauseIntent, PendingIntent.FLAG_IMMUTABLE)
    val playPauseAction = Notification.Action(R.drawable.small_play, "", playPausePendingIntent)

    val nextIntent = Intent(context, MediaControlReceiver::class.java)
    nextIntent.action = "NEXT_ACTION"
    val nextPendingIntent = PendingIntent.getBroadcast(context, 0,
        nextIntent, PendingIntent.FLAG_IMMUTABLE)

    val preIntent = Intent(context, MediaControlReceiver::class.java)
    preIntent.action = "NEXT_ACTION"
    val prePendingIntent = PendingIntent.getBroadcast(context, 0,
        preIntent, PendingIntent.FLAG_IMMUTABLE)


    val channel = NotificationChannel("music", "MusicPlayer", NotificationManager.IMPORTANCE_LOW)
    val builder = Notification.Builder(context, "music")
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

    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
    notificationManager.notify(0, notification)
}
