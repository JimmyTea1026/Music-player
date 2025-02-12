package com.example.myapplication.SongList

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Model.Song
import com.example.myapplication.Model.SongRepository
import com.example.myapplication.PlayPage.PlayPageViewModel

object SongListView{
    private var viewModel = SongListViewModel
    private val searchCandidateChanged = mutableStateOf(false)
    private val observer: ()->Unit = {searchCandidateChanged.value = !searchCandidateChanged.value}
    init{
        viewModel.addObserver(observer)
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
                    .height(80.dp),
            )
            showSongList(
                modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun searchBar(
        modifier: Modifier = Modifier,
    ){
        val inputText = remember{ mutableStateOf("") }
        TextField(
            value = inputText.value,
            onValueChange = {
                inputText.value = it
                viewModel.search(inputText.value)
            },
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
                .heightIn(min = 56.dp),
        )
    }
    @Composable
    fun showSongList(
        modifier: Modifier = Modifier,
    ){
        val songList = viewModel.songList
        val candidate by remember { mutableStateOf(viewModel.candidate) }
        LaunchedEffect(searchCandidateChanged.value) {
            candidate.clear()
            candidate.addAll(viewModel.candidate)
        }
        LazyColumn(
            modifier = modifier.padding(vertical = 4.dp),
        ){
            items(songList.size){index ->
                val song = songList[index]
                val color = if (index in candidate) Color.Red.copy(alpha = 0.1f) else Color.Blue.copy(alpha = 0.05f)
                Surface(
                    color = color,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .height(75.dp)
                        .clickable {
                            viewModel.onSongClicked(index)
                        }
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 10.dp, horizontal = 10.dp),

                        ){
                        val cover = song.getCover()
                        Image(
                            bitmap = cover.asImageBitmap(),
                            contentDescription = "",
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .width(100.dp)
                                .height(100.dp)
                        )
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

