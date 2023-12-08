package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme


@Composable
fun playPage(
    modifier: Modifier = Modifier
) {
    var title = "アイドル"
    var author = "YOASOBI"
    var time = 100

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)

    ){
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.cover),
                contentDescription = "Icon",
                modifier = Modifier
                    .height(400.dp)
                    .width(500.dp)
            )
        }

        // information
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 36.sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                    )
                Text(
                    text = author,
                    style = TextStyle(fontSize = 16.sp),
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                )
            }
        }

        // buttons
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 10.dp)
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                    ){
                        val iconSize = 50.dp
                        IconButton(
                            modifier = Modifier.weight(.1f),
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowLeft, contentDescription = "Last Song",
                                modifier = Modifier.size(iconSize),
                            )
                        }

                        val checkedState = remember{ mutableStateOf(true) }
                        IconToggleButton(
                            modifier = Modifier.weight(.1f),
                            checked = checkedState.value,
                            onCheckedChange = {checkedState.value=it}
                        ) {
                            val playIcon = painterResource(id = R.drawable.play1)
                            val pauseIcon = painterResource(id = R.drawable.pause)
                            var icon = if (checkedState.value) playIcon else pauseIcon
                            Icon(
                                painter = icon, contentDescription = "Play/Pause",
                                modifier = Modifier.size(iconSize),
                            )
                        }

                        IconButton(
                            modifier = Modifier.weight(.1f),
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowRight, contentDescription = "Next Song",
                                modifier = Modifier.size(iconSize),
                            )
                    }
                }
            }
        }

        // progress Bar & volumn Bar
        Box(
            modifier = Modifier
                .weight(1f)
//                .background(color = Color.Yellow)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ){
            Column(

            ) {
                LinearProgressIndicator(
                    progress = 0.5f,
                    modifier = Modifier
                )
            }

        }
    }
}
@Preview(showBackground = true)
@Composable
fun playPagePreview(){
    MyApplicationTheme {
        playPage()
    }
}