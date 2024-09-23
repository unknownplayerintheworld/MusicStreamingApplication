package com.hung.musicstreamingapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.data.model.Song

@Composable
fun itemHalfMusic(
    titleName: String,
    list : List<Song>
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp - 10.dp
    Column(
        Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = titleName, color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "arrow right")
            }
        }
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            items(list.size) {
                Card(
                    modifier = Modifier
                        .width(screenWidth / 2)
                        .height(200.dp)
                        .padding(0.dp, 0.dp, 10.dp, 0.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 5.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LoadImageFromNetwork(avatarLink = list[it].imageUrl)
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black
                                        ),
                                        startY = 200f
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp), contentAlignment = Alignment.BottomStart
                        ) {
                            Text(
                                list[it].name,
                                style = TextStyle(color = Color.White, fontSize = 16.sp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp), contentAlignment = Alignment.BottomEnd
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.play_circle_24),
                                contentDescription = "Play",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun LoadImageFromNetwork(avatarLink: String) {
    AsyncImage(
        model = avatarLink,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}
@Preview(showBackground = true)
@Composable
fun Preview() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){
//        itemHalfMusic()
    }

}