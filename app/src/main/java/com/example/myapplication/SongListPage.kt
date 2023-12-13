package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.io.File

class SongListPage(context : Context?, songList : ArrayList<Song>, changeSong: (Int) -> Unit){
    val assetManager = context!!.assets
    val songList = songList
    val changeSong = changeSong

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
            showSongList(
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
    fun showSongList(
        modifier: Modifier = Modifier,
    ){
        LazyColumn(
            modifier = modifier.padding(vertical = 4.dp),
        ){
            items(songList.size){index ->
                val song = songList[index]
                Surface(
                    color = Color.Blue.copy(alpha = 0.05f),
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .height(75.dp)
                        .clickable {
                            changeSong(index)
                        }
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 10.dp, horizontal = 10.dp),

                    ){
                        val coverPath = song.getCoverPath()
                        val cover = BitmapFactory.decodeStream(assetManager.open(coverPath))
                        Image(bitmap = cover.asImageBitmap(), contentDescription = "")
                        Box(modifier = Modifier
                            .weight(2f)
                            .fillMaxSize()
                            .padding(start = 35.dp),
                            contentAlignment = Alignment.Center
                        ){
                            Text(text = song.getTitle(),
                                textAlign = TextAlign.Center,
                                style = TextStyle(fontSize = 20.sp),
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
                                color = Color.DarkGray.copy(alpha = 0.7f)
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
//        SongListPage(null, "").songList()
    }
}