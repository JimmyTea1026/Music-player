package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SelectPage()
                }
            }
        }
    }
}

@Composable
fun SelectPage(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        SearchBar(
            modifier
                .fillMaxWidth()
                .height(80.dp))
        SongList(
            modifier
                .fillMaxWidth()
                .weight(1f))
        MenuBar(
            modifier
                .fillMaxWidth()
                .height(80.dp))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
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
fun SongList(
    modifier: Modifier = Modifier,
    names : List<String> = List(1000){"$it"}
){
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)){
        items(names){ name ->
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(text = "Hello, ")
                    Text(text = name)
                }
            }
        }
    }
}

@Composable
fun MenuBar(
    modifier: Modifier = Modifier
){
    Row(
        modifier = Modifier
            .height(70.dp)
            .padding(horizontal = 3.dp, vertical = 10.dp)

    ){
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .weight(1f)

        ) {
            Text("Song List")
        }
        Button(onClick = { /*TODO*/ },
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Now Playing")
        }

    }

}
//@Preview(showBackground = true)
@Composable
fun SearchBarPreview(){
    SearchBar()
}
@Preview(showBackground = true)
@Composable
fun SelectPagePreview(){
    SelectPage()
}