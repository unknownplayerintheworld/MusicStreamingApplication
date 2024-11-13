package com.hung.musicstreamingapplication.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.ui.animation.JourneyItemText
import com.hung.musicstreamingapplication.ui.components.itemRowMusic
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel
import kotlinx.coroutines.delay

@SuppressLint("SuspiciousIndentation")
@Composable
    fun HeaderHome(
        list: List<Song>,
        navController:NavHostController,
        musicVM: MusicViewModel
    ) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp - 10.dp
    var listsong by remember{
        mutableStateOf(emptyList<Song>())
    }
    listsong = list
    val listState = rememberLazyListState()
    LaunchedEffect(Unit){
        while (true) {
            // Kiểm tra nếu đã cuộn đến cuối danh sách
            if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1) {
                listState.animateScrollToItem(0) // Cuộn về đầu
            } else {
                listState.animateScrollToItem(listState.firstVisibleItemIndex + 1)
            }
            delay(2000) // Thay đổi thời gian cuộn theo nhu cầu của bạn
        }
    }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.Transparent)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ){
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    verticalAlignment = Alignment.CenterVertically

                ){
                    Text(text = stringResource(id = R.string.journey), color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp))
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Mic", modifier = Modifier.size(25.dp), tint = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = {
                    navController.navigate("search")
                }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search",tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(25.dp))
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    state = listState
                ) {
                    items(listsong.size) {
                        Card(
                            modifier = Modifier
                                .width(screenWidth/2)
                                .height(200.dp)
                                .padding(0.dp, 0.dp, 10.dp, 0.dp)
                                .clickable {
                                    musicVM.startMusicService(listsong[it])
                                    navController.navigate("playing")
                                },
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
                                        LocalContext.current).data(listsong[it].imageUrl).crossfade(true).build()),
                                    contentDescription = "journey",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black
                                                ),
                                                startY = 300f
                                            )
                                        )
                                )
                                Box(
                                    Modifier.fillMaxSize().background(
                                        Color.Black.copy(alpha = 0.15f)
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp), contentAlignment = Alignment.BottomStart
                                ) {
                                    Text(
                                        listsong[it].name,
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
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                        .padding(10.dp), contentAlignment = Alignment.TopStart
                                ){
                                    Column(
                                        Modifier
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color.Gray.copy(alpha = 0.3f))
                                            .padding(5.dp)
                                    ) {
                                        JourneyItemText()
                                    }
                                }
                            }
                        }
                    }
                }

                }
            }
        }

@Composable
fun RecentTrack(
    list: List<Song>?,
    navController: NavHostController,
    musicVM: MusicViewModel,
    loginVM:LoginViewModel
) {
    var listsong by remember {
        mutableStateOf(emptyList<Song>())
    }
    listsong = list ?: emptyList() // Gán list nếu không null, ngược lại là danh sách rỗng

    // Chia danh sách thành các nhóm, mỗi nhóm 4 phần tử
    val groupedSongs = listsong.chunked(4)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .height(325.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("recentlyDetail")
                }
                .padding(0.dp, 0.dp, 0.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.recently),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "arrow right")
            }
        }

        // Kiểm tra nếu danh sách rỗng hoặc null
        if (listsong.isEmpty()) {
            // Hiển thị văn bản khi danh sách trống
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_songs_available), // Chuỗi văn bản thay thế
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            // Hiển thị LazyRow chứa các nhóm nếu có phần tử
            LazyRow(
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(groupedSongs) { songGroup ->
                    // Mỗi item trong LazyRow sẽ là một nhóm chứa tối đa 4 bài hát
                    Column(
                        Modifier.fillParentMaxSize()
                    ) {
                        songGroup.forEach { song ->
                            Spacer(modifier = Modifier.height(2.dp))
                            itemRowMusic(musicVM, navController, loginVM,song)
                            Spacer(modifier = Modifier.height(2.dp))
//                            Row(
//                                Modifier
//                                    .fillMaxWidth()
//                                    .clip(RoundedCornerShape(10.dp))
//                                    .clickable {
//                                        musicVM.startMusicService(song)
//                                        navController.navigate("playing")
//                                    }
//                                    .height(60.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Card(
//                                    modifier = Modifier
//                                        .width(60.dp)
//                                        .height(60.dp)
//                                        .padding(0.dp, 0.dp, 10.dp, 15.dp),
//                                    shape = RoundedCornerShape(10.dp),
//                                    elevation = CardDefaults.cardElevation(
//                                        defaultElevation = 5.dp
//                                    )
//                                ) {
//                                    Image(
//                                        painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
//                                            LocalContext.current).data(song.imageUrl).crossfade(true).build()),
//                                        contentDescription = "recently",
//                                        contentScale = ContentScale.Crop,
//                                        modifier = Modifier.fillMaxSize()
//                                    )
//                                }
//
//                                Row(
//                                    Modifier
//                                        .fillMaxSize()
//                                        .padding(0.dp, 0.dp, 10.dp, 15.dp),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Column {
//                                        Text(
//                                            text = song.name,
//                                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 3.dp),
//                                            fontWeight = FontWeight.Bold
//                                        )
//                                        Text(
//                                            text = song.authorName ?: "Unknown",
//                                            fontWeight = FontWeight.Light,
//                                            fontSize = 13.sp
//                                        )
//                                    }
//                                    IconButton(onClick = { /*TODO*/ }) {
//                                        Icon(
//                                            painter = painterResource(id = R.drawable.more_vert_24),
//                                            contentDescription = "More options"
//                                        )
//                                    }
//                                }
//                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Recommend(
    playlist: List<Playlist>,
    navController: NavHostController,
    musicVM:MusicViewModel
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
            Text(text = stringResource(id = R.string.recommend), color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "arrow right")
            }
        }
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            items(playlist.size) {
                Card(
                    modifier = Modifier
                        .width((screenWidth / 3) + screenWidth / 8)
                        .height(200.dp)
                        .clickable {
                            if(playlist[it].name.isNullOrEmpty()){

                            } else {
                                navController.navigate("playlist")
                                musicVM.playlistClicked(playlist[it])
                            }
                        }
                        .padding(0.dp, 0.dp, 10.dp, 0.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 5.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
                                LocalContext.current).data(
                                    if(!playlist[it].imageUrl.isNullOrEmpty()){
                                        playlist[it].imageUrl
                                    }else{
                                        R.drawable.d3650077420d928b587a1feb77fa94cb
                                    }
                                ).crossfade(true).build()),
                            contentDescription = "journey",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
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
                            playlist[it].name?.let { it1 ->
                                Text(
                                    it1,
                                    style = TextStyle(color = Color.White, fontSize = 16.sp)
                                )
                            }
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
fun HotAlbum(
    list: List<Album>,
    navController: NavHostController,
    musicVM: MusicViewModel
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
            Text(text = stringResource(id = R.string.hotAlbum), color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "arrow right")
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
                        .clickable {
                            navController.navigate("playlist")
                            musicVM.albumClicked(album = list[it])
                        }
                        .padding(0.dp, 0.dp, 10.dp, 0.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 5.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(list[it].imageUrl),
                            contentDescription = "hot album",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
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
fun Trending(
    list: List<Song>,
    musicVM: MusicViewModel,
    navController: NavHostController
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp - 10.dp

    // Lấy bài đầu tiên
    val featuredSong = list.firstOrNull()

    // Chia danh sách còn lại thành nhóm 4 bài
    val groupedSongs = list.drop(1).chunked(4)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .height(590.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.trending),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "arrow right")
            }
        }

        // Hiển thị bài hát đầu tiên
        featuredSong?.let { song ->
            Card(
                modifier = Modifier
                    .width(screenWidth)
                    .height(200.dp)
                    .clickable {
                        musicVM.startMusicService(song = song)
                        navController.navigate("playing")
                    }
                    .padding(10.dp, 0.dp, 0.dp, 0.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(song.imageUrl),
                        contentDescription = song.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black
                                    ),
                                    startY = 300f
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp), contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = song.name,
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

        // Hiển thị các nhóm bài hát còn lại trong LazyRow
        LazyRow(
            contentPadding = PaddingValues(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 50.dp),
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
        ) {
            items(groupedSongs) { group ->
                Column(
                    Modifier.fillParentMaxWidth()
                ) {
                    group.forEach { song ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    musicVM.startMusicService(song)
                                    navController.navigate("playing")
                                }
                                .height(60.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(60.dp)
                                    .padding(0.dp, 0.dp, 10.dp, 15.dp),
                                shape = RoundedCornerShape(10.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(song.imageUrl),
                                    contentDescription = song.name,
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .padding(0.dp, 0.dp, 0.dp, 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = song.name,
                                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 3.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = song.authorName ?: "Unknown Author", fontWeight = FontWeight.Light, fontSize = 13.sp)
                                }
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.more_vert_24),
                                        contentDescription = "Play"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
//    @Preview(showBackground = true)
//    @Composable
//    fun Preview1(){
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.background)
//        ) {
//            HeaderHome(emptyList(), navController = NavHostController(LocalContext.current), musicVM = MusicViewModel())
//            RecentTrack(emptyList())
//            Recommend(Playlist())
//        }
//    }
//    @Preview(showBackground = true)
//    @Composable
//    fun PreviewHomePlaylist(){
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.background)
//        ){
//            HotAlbum(emptyList())
//            Trending(emptyList())
//        }
//    }