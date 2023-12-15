package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.PlayPage.PlayPage
import com.example.myapplication.SongList.SongListPage
import com.example.myapplication.SongList.SongListView
import com.example.myapplication.SongList.SongListViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object MVVMDict {
    private val mvvmMap: MutableMap<String, Any> = mutableMapOf()
    fun add(key: String, viewModel: Any) {
        mvvmMap[key] = viewModel
    }
    fun get(key: String): Any? {
        return mvvmMap[key]
    }

    fun getMap():MutableMap<String, Any>{
        return mvvmMap
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                initDict()
                initMVVM()
                showNavPage()
            }
        }
    }
    fun initDict(){
        val songRepository = SongRepository(MVVMDict)
        songRepository.setContext(this)
        songRepository.initSongList()
        SongListView(MVVMDict)
        SongListViewModel(MVVMDict)
    }
    fun initMVVM(){
        (MVVMDict.get("SongListView") as SongListView).initialize()
        (MVVMDict.get("SongListViewModel") as SongListViewModel).initialize()
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
        val songRepository = MVVMDict.get("SongRepository") as SongRepository
        val songListView = MVVMDict.get("SongListView") as SongListView
        val songListViewModel = MVVMDict.get("SongListViewModel") as SongListViewModel
        var curPage by remember { mutableStateOf(Page.SONGLIST) }
        LaunchedEffect(songListViewModel.onChangeSongIndex.value){
            if(songListViewModel.onChangeSongIndex.value >= 0) curPage = Page.PLAY
            //
        }

        var playPage by remember{ mutableStateOf(PlayPage(this, songRepository))}


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
                        Page.SONGLIST -> songListView.showPage()
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
                    Modifier
                        .weight(1f)
                        .height(50.dp)
                        .width(60.dp))
                playPage?.songInformation(
                    Modifier.weight(1f),
                    fontSize = 15
                )
                playPage?.progressBar(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    fontSize = 8
                )
                playPage?.buttons(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
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