@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

enum class Page{
    SONG, PLAY
}

@Composable
fun MainPage(
    modifier: Modifier = Modifier
) {
    var curPage by remember { mutableStateOf(Page.PLAY) }

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
                Page.SONG -> {songPage()}
                Page.PLAY -> {playPage()}
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
//                .background(color = Color.LightGray)
        ){
            MenuBar(
                songListClicked = {curPage = Page.SONG},
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

@Preview(showBackground = true)
@Composable
fun MaingPagePreview() {
    MyApplicationTheme {
        MainPage()
    }
}