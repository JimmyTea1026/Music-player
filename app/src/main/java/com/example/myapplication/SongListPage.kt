package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.io.File

class SongListPage{
    private val songList : MutableList<Song> = mutableListOf<Song>()
    init{
        initSongList()
    }
    fun initSongList(){
        val path = "D:\\Compal\\Code\\MusicPlayer\\song"
        val dirs = File(path).listFiles()
        dirs.forEach{ dir ->
            val newSong : Song = Song(dir)
            songList.add(newSong)
        }
    }
    @Composable
    fun showPage(
        modifier: Modifier = Modifier
    ){
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            searchBar(
                modifier
                    .fillMaxWidth()
                    .height(80.dp))
            songList(
                modifier
                    .fillMaxWidth()
                    .weight(1f))
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun searchBar(
        modifier: Modifier = Modifier
    ){
        TextField(
            value = "",
            onValueChange = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            placeholder = {
                Text("search")
            },
            modifier = modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        )
    }
    @Composable
    fun songList(
        modifier: Modifier = Modifier,
    ){
        var songs = emptyList<Song>()
        val path =  "D:/Compal/Code/MusicPlayer/song/"
        getSongs(path, songs)
        LazyColumn(modifier = modifier.padding(vertical = 4.dp)){
            items(songs){ song ->
                Surface(
                    color = Color.Blue.copy(alpha = 0.2f),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)) {
                        Text(text = "Hello, ")

                    }
                }
            }
        }
    }

    fun getSongs(path: String, songs:List<Song>){
        val folders = File(path).listFiles()
        if (folders != null) {
            for (folder in folders) {
                var folders = folder.listFiles()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun songPagePreview(){
    MyApplicationTheme {
        val sp = SongListPage()
        sp.showPage()
    }
}