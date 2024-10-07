package com.hung.musicstreamingapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.data.model.Song

@Composable
fun musicCardItem(
    song: Song
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(55.dp)
    ) {
        Card(
            modifier = Modifier
                .width(65.dp)
                .height(55.dp)
                .padding(0.dp, 0.dp, 10.dp, 10.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.yaemiko),
                contentDescription = "journey",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

        }
        Row(
            Modifier
                .fillMaxSize()
                .padding(0.dp, 0.dp, 10.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Music 1",
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Author 1", fontWeight = FontWeight.Light)
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.more_vert_24),
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = "Play"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun show() {
    Column(
        Modifier.fillMaxSize()
    ) {
        musicCardItem(song = Song())
    }
}