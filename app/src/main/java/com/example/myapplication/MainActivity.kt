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
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.PlayPage.PlayPageView
import com.example.myapplication.PlayPage.PlayPageViewModel
import com.example.myapplication.SongList.SongListView
import com.example.myapplication.SongList.SongListViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val notification = customizeNotification()
class MainActivity : ComponentActivity() {
    private lateinit var serviceConnection : ServiceConnection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                showNavPage()
                startWifiBLEService()
                startMusicPlayerService()
                notification.createCustomNotification(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
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
    private fun startBLEService(){
        val bleIntent = Intent(this, BluetoothLeService::class.java)
        startService(bleIntent)
    }
    private fun startWifiService(){
        val wifiIntent = Intent(this, WifiManagerService::class.java)
        startService(wifiIntent)
    }

    private val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions.entries.all { it.value }
                if (granted) {
                    Log.d("Permission", "Get all permission")
                    startWifiService()
                } else {
                    Log.d("Permission", "no permission")
                }
            }

    private val requestEnableBt =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("Bluetooth", "Bluetooth ON")
                startBLEService()
            } else {
                Log.d("Bluetooth", "Bluetooth off")
            }
        }
    private fun startWifiBLEService() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val permissions = arrayOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        requestMultiplePermissions.launch(permissions)
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestEnableBt.launch(enableBtIntent)
        }else startBLEService()
    }
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
    SongRepository.initSongList()
    val songListViewModel by remember { mutableStateOf(SongListViewModel.initSongList())}
    val playPageViewModel by remember { mutableStateOf(PlayPageViewModel.initSongList())}
    val songListView by remember { mutableStateOf(SongListView) }
    val playPageView by remember { mutableStateOf(PlayPageView) }

    var switchToPlayPage by remember { mutableStateOf(false) }
    LaunchedEffect(songListViewModel.onChangeSong.value){
        if(songListViewModel.onChangeSongIndex.value >= 0){
            playPageViewModel.setSong(songListViewModel.onChangeSongIndex.value, true)
            switchToPlayPage = true
        }
    }
    LaunchedEffect(playPageViewModel.getCurrentSongIndex().value){
        notification.updateNotification()
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
