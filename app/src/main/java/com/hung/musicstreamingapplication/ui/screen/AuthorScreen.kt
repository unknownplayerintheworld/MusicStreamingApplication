package com.hung.musicstreamingapplication.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.data.model.Author
import com.hung.musicstreamingapplication.ui.components.CardAlbumItem
import com.hung.musicstreamingapplication.ui.components.CustomButton
import com.hung.musicstreamingapplication.ui.components.itemRowMusic
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun AuthorScreen(
    musicVM: MusicViewModel,
    navController: NavHostController,
    author: Author
) {
    val hotSongArtists by musicVM.hotAuthorSong.collectAsState()
    val playlistArtists by musicVM.playlistAuthor.collectAsState()
    val albumArtists by musicVM.albumAuthor.collectAsState()
    LaunchedEffect(author) {
        musicVM.getAuthorHotSong(author.id)
        musicVM.getAlbumAuthor(author)
        musicVM.getRelatedAuthor(author)
        musicVM.getPlaylistsAuthor(author)
    }

    var offsetY by remember { mutableStateOf(0f) }
    var alpha by remember { mutableStateOf(1f) }

    // Get screen height using LocalConfiguration
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageHeight = screenHeight * 1 / 2 // Calculate 2/3 of the screen height

    // Top section (image and buttons) that should stay fixed
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(2f)
            .background(Color.Transparent)
    ) {
        // Back and More buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack() // Go back
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = { /* Add action */ },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }
    }

    // Content section: make this scrollable
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header (image and buttons)
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val screenwidth = LocalConfiguration.current.screenWidthDp.dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(
                                LocalContext.current
                            ).data(author.avatar).crossfade(true).build()
                        ),
                        contentDescription = author.bio,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(imageHeight) // Set height to 1/2 of the screen
                            .graphicsLayer(
                                translationY = offsetY, // Move the image upwards
                                alpha = alpha.coerceAtLeast(0f) // Fade out based on scroll
                            )
                    )
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            author.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "1M followers",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CustomButton(
                                onClick = { /* Play action */ },
                                color = MaterialTheme.colorScheme.primary,
                                textColor = Color.White,
                                text = stringResource(id = R.string.play),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .width(screenwidth / 2 - 20.dp)
                                    .height(40.dp),
                                enabled = true
                            )
                            CustomButton(
                                onClick = { /* Follow action */ },
                                color = Color.Black.copy(alpha = 0.4f),
                                textColor = Color.White,
                                text = stringResource(id = R.string.follow),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .width(screenwidth / 2 - 20.dp)
                                    .height(40.dp),
                                enabled = true
                            )
                        }
                    }
                }
            }
        }

        // Hot songs section
        item {
            Column(
                Modifier
                    .padding(start = 15.dp, top = 15.dp, end = 10.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.hot_song),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable { /* More action */ }
                    )
                }
                LazyColumn(
                    Modifier
                        .padding(vertical = 10.dp)
                        .height(330.dp)
                ) {
                    items(hotSongArtists.size) {
                        itemRowMusic(musicVM = musicVM, navController = navController, song = hotSongArtists[it])
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomButton(
                        onClick = { /* Follow action */ },
                        color = Color.Gray.copy(alpha = 0.4f),
                        textColor = Color.White,
                        text = stringResource(id = R.string.more),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .width(screenWidth / 2 - 20.dp)
                            .height(40.dp),
                        enabled = true
                    )
                }
            }
        }

        // Album section
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 10.dp, end = 10.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = "Album",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp
                )
                LazyRow(
                    Modifier
                        .padding(vertical = 10.dp)
                ) {
                    items(albumArtists.size) {
                        CardAlbumItem(navController = navController, musicVM = musicVM, album = albumArtists[it])
                    }
                }
            }
        }

        // Playlist section
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 10.dp, end = 10.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = "Playlist",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp
                )
                LazyRow(
                    Modifier
                        .padding(vertical = 10.dp)
                ) {
                    items(playlistArtists.size) {
                        CardAlbumItem(navController = navController, musicVM = musicVM, playlist = playlistArtists[it])
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewAuthor() {
//    AuthorScreen()
}