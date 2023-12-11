package com.example.myapplication

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.io.File

class SongListPage(context : Context?){
    private val songList : MutableList<Song> = mutableListOf<Song>()
    init{
        val assetManager = context!!.assets
        val path = "music"
        val assetList = assetManager.list(path)
        if (assetList != null) {
            for(artist in assetList){
                var artist = artist
                var coverPath = ""
                var songPath = ""
                var songTitle = ""

                val songs = assetManager.list("$path/$artist")
                if (songs != null) {
                    for (title in songs) {
                        songTitle = title
                        val files = assetManager.list("$path/$artist/$title")
                        if (files != null) {
                            for (file in files) {
                                val filePath = "$path/$artist/$title/$file"
                                if (filePath.endsWith(".png")) coverPath = filePath
                                else if (filePath.endsWith(".mp3")) songPath = filePath
                            }
                        }
                    }
                    val song: Song = Song()
                    song.setInformation(
                        songTitle = songTitle,
                        coverPath = coverPath,
                        songPath = songPath,
                        artist = artist
                    )
                    songList.add(song)
                }
            }
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
        val list = (1..100).toList()
        LazyColumn(
            modifier = modifier.padding(vertical = 4.dp),
        ){
            items(songList){ song ->
                Surface(
                    color = Color.DarkGray.copy(alpha = 0.1f),
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .height(75.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 10.dp, horizontal = 10.dp),

                    ){
                        Image(painter = painterResource(id = R.drawable.cover), contentDescription = "")
                        Box(modifier = Modifier
                            .weight(1.5f)
                            .fillMaxSize()
                            .padding(start = 15.dp),
                            contentAlignment = Alignment.Center
                        ){
                            Text(text = song.getTitle(),
                                textAlign = TextAlign.Center,
                                style = TextStyle(fontSize = 25.sp),
//                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Box(modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                            contentAlignment = Alignment.BottomEnd
                        ){
                            Text(text = song.getArtist(),
                                textAlign = TextAlign.Right,
                                style = TextStyle(fontSize = 13.sp),
//                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun songPagePreview(){
    MyApplicationTheme {
        SongListPage(null).songList()
    }
}