package com.example.myapplication

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
//                test(this)

                val coroutineScope = rememberCoroutineScope()
                val switchToMainPage = remember{ mutableStateOf(false) }

                LaunchedEffect(Unit){
                    coroutineScope.launch {
                        delay(1000)
                        switchToMainPage.value = true
                    }
                }
                Crossfade(targetState = switchToMainPage.value, animationSpec = tween(durationMillis = 500),
                    label = "navToMain"
                )
                { switchPage ->
                    when(switchPage){
                        true -> mainPage()
                        false -> navPage()
                    }
                }
            }
        }
    }
    private fun test(context: Context){
        var media = MediaPlayer()
        val assetManager = context.assets
        val des = assetManager.openFd("music/アイドル/song.mp3")
        media.setDataSource(des)
        media.prepare()
        media.start()
    }

    enum class Page{
        SONGLIST, PLAY
    }
    @Composable
    fun mainPage(
        modifier: Modifier = Modifier
    ) {

        var curPage by remember { mutableStateOf(Page.SONGLIST) }
        val songPage = SongListPage(this)
        val playPage = PlayPage(this)

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                when(curPage){
                    Page.SONGLIST -> {songPage.showPage()}
                    Page.PLAY -> {playPage.showPage()}
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
        Surface(color = Color.LightGray) {
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
}


@Preview(showBackground = true)
@Composable
fun MaingPagePreview() {
    MyApplicationTheme {
        MainActivity().mainPage()
    }
}

@Preview(showBackground = true)
@Composable
fun navPagePreview(){
    MyApplicationTheme{
        MainActivity().navPage()
    }
}
