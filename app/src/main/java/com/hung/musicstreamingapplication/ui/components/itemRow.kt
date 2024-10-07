package com.hung.musicstreamingapplication.ui.components

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.Timestamp
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Author
import com.hung.musicstreamingapplication.data.model.Comment
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun itemRowMusic(
    musicVM:MusicViewModel,
    navController: NavHostController,
    song: Song
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                musicVM.startMusicService(song)
                navController.navigate("playing")
            }
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
                    LocalContext.current).data(song.imageUrl).crossfade(true).build()),
                contentDescription = "recently",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            Modifier
                .fillMaxSize()
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = song.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp
                )
                Text(
                    text = song.authorName ?: "Unknown",
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.more_vert_24),
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
    if(showBottomSheet){
        ModalBottomSheet(onDismissRequest = {
            showBottomSheet = false
        }, sheetState = sheetState) {
            Column(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(top = 15.dp)
                ) {
                    Box(
                        modifier = Modifier.width(100.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(
                                    LocalContext.current
                                ).data(
                                    song.imageUrl
                                ).crossfade(true).build()
                            ),
                            contentDescription = "journey",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(100.dp)
                                .height(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .padding(start = 15.dp, top = 0.dp, end = 5.dp, bottom = 10.dp)
                        )
                    }
                    Column(
                        Modifier
                            .fillMaxHeight()
                            .padding(start = 5.dp)
                    ) {
                        Text(
                            text = song.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(0.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "MUSA",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }
                HorizontalDivider()
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(start = 15.dp, top = 10.dp, end = 15.dp, bottom = 10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("search")
                                showBottomSheet = false
                            }
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        Spacer(Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.search_playlist), fontSize = 14.sp)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {

                                showBottomSheet = false
                            }
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Fav")
                        Spacer(Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.add_to_library), fontSize = 14.sp)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                showBottomSheet = false
                            }
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add to player"
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.add_to_player), fontSize = 14.sp)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                showBottomSheet = false
                            }
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        Spacer(Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.share), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
@Composable
fun itemRowMusicSearchPlaylist(
    musicVM:MusicViewModel,
    navController: NavHostController,
    song: Song,
    playlist: Playlist
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                musicVM.startMusicService(song)
                navController.navigate("playing")
            }
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
                    LocalContext.current).data(song.imageUrl).crossfade(true).build()),
                contentDescription = "recently",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            Modifier
                .fillMaxSize()
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = song.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp
                )
                Text(
                    text = song.authorName ?: "Unknown",
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = {
                musicVM.addSongToPlaylist(playlistID = playlist.id,songID = song.id)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_circle_24),
                    contentDescription = "Thêm bài hát",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
@Composable
fun itemRowPlaylist(
    navController: NavHostController,
    musicVM: MusicViewModel,
    playlist: Playlist
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                navController.navigate("playlist")
                musicVM.playlistClicked(playlist)
            }
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .size(75.dp)
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
                    LocalContext.current).data(if(!playlist.imageUrl.isNullOrEmpty()){
                        playlist.imageUrl
                    }
                    else{
                        R.drawable.playlist_default
                    }).crossfade(true).build()),
                contentDescription = "recently",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            Modifier
                .fillMaxSize()
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = playlist.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = playlist.author,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "More"
                )
            }
        }
    }
}

@Composable
fun itemRowAlbum(
    musicVM: MusicViewModel,
    navController: NavHostController,
    album: Album
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                navController.navigate("playlist")
                musicVM.albumClicked(album)
            }
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .size(75.dp)
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
                    LocalContext.current).data(album.imageUrl).crossfade(true).build()),
                contentDescription = "recently",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            Modifier
                .fillMaxSize()
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = album.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = album.authorName,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "More"
                )
            }
        }
    }
}

@Composable
fun itemRowAuthor(
    musicVM: MusicViewModel,
    navController: NavHostController,
    author: Author
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .clickable {
                musicVM.setAuthor(author)
                navController.navigate("author")
            }
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .size(60.dp)
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
                    LocalContext.current).data(author.avatar).crossfade(true).build()),
                contentDescription = "recently",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            Modifier
                .fillMaxSize()
                .padding(0.dp, 0.dp, 10.dp, 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = author.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = author.name,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "More options"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun itemRowMusicPlaylist(
    index: Int,
    navController: NavHostController,
    musicVM: MusicViewModel,
    song: Song,
    list: List<Song>,
    playlist: Playlist
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                navController.navigate("playing")
                musicVM.startMusicService(song, list, index)
            }
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.width(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = index.toString(),color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp, fontWeight = FontWeight.Light)
        }
        Spacer(
            modifier = Modifier.width(5.dp)
        )
        Row(
            Modifier
                .fillMaxSize()
                .padding(0.dp, 5.dp, 10.dp, 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = song.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
                Text(
                    text = song.authorName ?: "Unknown",
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 13.sp
                )
            }
            IconButton(onClick = {
                showBottomSheet = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
    if(showBottomSheet){
        ModalBottomSheet(onDismissRequest = {
            showBottomSheet = false
        }, sheetState = sheetState) {
            Column(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(top = 15.dp)
                ) {
                    Box(
                        modifier = Modifier.width(100.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(
                                    LocalContext.current
                                ).data(
                                    song.imageUrl
                                ).crossfade(true).build()
                            ),
                            contentDescription = "journey",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(90.dp)
                                .height(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .padding(start = 15.dp, top = 0.dp, end = 5.dp, bottom = 10.dp)
                        )
                    }
                    Column(
                        Modifier
                            .fillMaxHeight()
                            .padding(start = 5.dp)
                    ) {
                        Text(
                            text = song.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(0.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "MUSA",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }
                HorizontalDivider()
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(start = 15.dp, top = 10.dp, end = 15.dp, bottom = 10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("search")
                                showBottomSheet = false
                            }
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        Spacer(Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.search_playlist), fontSize = 14.sp)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {

                                showBottomSheet = false
                            }
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Fav")
                        Spacer(Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.add_to_library), fontSize = 14.sp)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                showBottomSheet = false
                            }
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add to player"
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.add_to_player), fontSize = 14.sp)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                showBottomSheet = false
                                musicVM.removeSongFromPlaylist(playlist = playlist, song = song)
                            }
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove from playlist"
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.remove_from_playlist), fontSize = 14.sp)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                showBottomSheet = false
                            }
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        Spacer(Modifier.width(10.dp))
                        Text(text = stringResource(id = R.string.share), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun itemRowLibrary(
    icon: ImageVector?= null,
    iconRes: Painter?= null,
    textRes: String,
    contentDescription : String,
    color : Color
) {
    Box(
        Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        )
        {
            if (iconRes != null) {
                Icon(
                    painter = iconRes,
                    contentDescription = contentDescription,
                    Modifier.size(30.dp),
                    tint = color
                )
            } else {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        tint = color,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = textRes,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

// comment
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun itemRowComment(
    song: Song,
    comment: Comment,
    musicVM: MusicViewModel,
    loginVM: LoginViewModel
) {
    val currentUser by loginVM.currentUser.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    val isFav = remember { mutableStateOf(-1) }
    var isExpanded by remember { mutableStateOf(false) }
    val isCommentCreator = remember { mutableStateOf(-1) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val commentStatus by musicVM.isSuccessfulComment.collectAsState()
    var childCommentList = remember {
        mutableStateOf<List<Comment>?>(emptyList())
    }
    LaunchedEffect(Unit){
        isFav.value = if (currentUser?.let { comment.interactiveUserIDs.contains(it.userID) } == true) 1 else 0
    }
    LaunchedEffect(commentStatus){
        childCommentList.value = musicVM.getChildComments(comment.id)
        Log.d("CHILD_CMT",childCommentList.value.toString())
        delay(5*1000*60)
    }
    LaunchedEffect(currentUser) {
        userid?.let { loginVM.getUser(it) }
        delay(5*1000*60)
    }
    LaunchedEffect(currentUser){
        isCommentCreator.value = if (currentUser?.let { comment.userID == currentUser!!.userID } == true) 1 else 0
    }
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ){
        Column {
            Spacer(Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(
                        LocalContext.current
                    ).data(
                        if(!comment?.imageUrl.isNullOrEmpty()){
                            comment.imageUrl
                        }else{
                            R.drawable.yaemiko
                        }
                    ).crossfade(true).build()
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentDescription = "Comment"
            )
        }
        Spacer(modifier = Modifier.width(7.dp))
        Column {
            Row(
                Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row{
                        Text(
                            if (!comment?.imageUrl.isNullOrEmpty()) {
                                comment.username
                            } else {
                                "User 1"
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "•",
                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            if (!comment?.content.isNullOrEmpty()) {
                                getTimeAgo(comment.created_at)
                            } else {
                                "NaN"
                            },
                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Row{
                        Text(
                            comment?.content.toString(),
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Row {
                        Row{
                            Icon(
                                imageVector = if (isFav.value == 1) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                },
                                contentDescription = "favourite",
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        Log.d("Click", "ấn")
                                        CoroutineScope(Dispatchers.Main).launch {
                                            Log.d("Clickable", "Click 1")
                                            if (isFav.value == 1) {
                                                isFav.value = musicVM.unFavComment(
                                                    comment.id,
                                                    currentUser!!.userID
                                                )
                                            } else {
                                                isFav.value = musicVM.favComment(
                                                    comment.id,
                                                    currentUser!!.userID
                                                )
                                            }
                                        }
                                    }
                                    .padding(end = 1.dp),
                                tint = if (isFav.value == 1) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(0.6f)
                                }
                            )
                            Text(
                                text = if (isFav.value == 1) {
                                    (comment.interactiveUserIDs.size + 1).toString()
                                } else {
                                    comment.interactiveUserIDs.size.toString()
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            stringResource(id = R.string.reply),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.clickable {
                                musicVM.setReplyingStatus(true,comment)
                            }
                        )
                    }
                    if (childCommentList.value?.isEmpty() == false) {
                        Row(
                            Modifier
                                .clickable {
                                    isExpanded = true
                                }
                        ) {
                            Text(
                                "Xem phản hồi",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "(" + childCommentList.value!!.size.toString() + ")",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            if (isExpanded && !childCommentList.value.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(start = 40.dp)
                        .fillMaxWidth()
                ) {
                    childCommentList.value?.forEach { childComment ->
                        itemRowCommentChild(
                            song,
                            comment = childComment,
                            musicVM = musicVM,
                            loginVM
                        )
                    }
                }
            }
        }
    }
    if(showBottomSheet){
        ModalBottomSheet(onDismissRequest = {
            showBottomSheet = false
        }, sheetState = sheetState) {
            Column(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 15.dp, vertical = 10.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = stringResource(id = R.string.comment_of) + comment.username,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 14.sp)
                }
                HorizontalDivider(modifier = Modifier.background(MaterialTheme.colorScheme.onBackground.copy(0.7f)))
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .clickable {
                                musicVM.setReplyingStatus(true, comment)
                                showBottomSheet = false
                            }){
                        Icon(
                            painter = painterResource(id = R.drawable.outline_mode_comment_24),
                            contentDescription = "reply")
                        Spacer(Modifier.width(20.dp))
                        Text(text = stringResource(id = R.string.reply),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp)
                    }
                    if(isCommentCreator.value == 1) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .clickable {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        musicVM.delComment(comment)
                                        musicVM.getComment(song)
                                    }
                                    showBottomSheet = false
                                }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete"
                            )
                            Spacer(Modifier.width(20.dp))
                            Text(
                                stringResource(id = R.string.del_comment),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun itemRowCommentChild(
    song: Song,
    comment: Comment,
    musicVM: MusicViewModel,
    loginVM: LoginViewModel
) {
    val currentUser by loginVM.currentUser.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    val isFav = remember { mutableStateOf(-1) }
    val isCommentCreator = remember { mutableStateOf(-1) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    LaunchedEffect(currentUser) {
        userid?.let { loginVM.getUser(it) }
        delay(5*1000*60)
    }
    LaunchedEffect(Unit){
        isFav.value = if (currentUser?.let { comment.interactiveUserIDs.contains(it.userID) } == true) 1 else 0
    }
    LaunchedEffect(currentUser){
        isCommentCreator.value = if (currentUser?.let { comment.userID == currentUser!!.userID } == true) 1 else 0
    }
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ){
        Column {
            Spacer(Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(
                        LocalContext.current
                    ).data(
                        if(!comment?.imageUrl.isNullOrEmpty()){
                            comment.imageUrl
                        }else{
                            R.drawable.yaemiko
                        }
                    ).crossfade(true).build()
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentDescription = "Comment"
            )
        }
        Spacer(modifier = Modifier.width(7.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row{
                    Text(
                        if(!comment?.imageUrl.isNullOrEmpty()){
                            comment.username
                        }else{
                            "User 1"
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "•",
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        if(!comment?.content.isNullOrEmpty()){
                            getTimeAgo(comment.created_at)
                        }else{
                            "NaN"
                        },
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Row {
                    Text(
                        comment.username,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        comment?.content.toString(),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Row {
                        Row{
                                Icon(
                                    imageVector = if (isFav.value == 1) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Outlined.FavoriteBorder
                                    },
                                    contentDescription = "favourite",
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                if (isFav.value == 1) {
                                                    isFav.value = musicVM.unFavComment(
                                                        comment.id,
                                                        currentUser!!.userID
                                                    )
                                                } else {
                                                    isFav.value = musicVM.favComment(
                                                        comment.id,
                                                        currentUser!!.userID
                                                    )
                                                }
                                            }
                                        }
                                        .padding(end = 1.dp),
                                    tint = if (isFav.value == 1) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.onBackground.copy(0.6f)
                                    }
                                )
                            Text(
                                text = if(isFav.value == 1){
                                    (comment.interactiveUserIDs.size + 1).toString()
                                }else{
                                    comment.interactiveUserIDs.size.toString()
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                }
            }
            IconButton(onClick = {
                showBottomSheet = true
            }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(14.dp))
            }
        }
    }
    if(showBottomSheet){
        ModalBottomSheet(onDismissRequest = {
            showBottomSheet = false
        }, sheetState = sheetState) {
            Column(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 15.dp, vertical = 10.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = stringResource(id = R.string.comment_of) + comment.username,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 14.sp)
                }
                HorizontalDivider()
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .clickable {
                                musicVM.setReplyingStatus(true, comment)
                                showBottomSheet = false
                            }){
                        Icon(
                            painter = painterResource(id = R.drawable.outline_mode_comment_24),
                            contentDescription = "reply")
                        Spacer(Modifier.width(20.dp))
                        Text(stringResource(id = R.string.reply),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp)
                    }
                    if(isCommentCreator.value == 1) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .clickable {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        musicVM.delComment(comment)
                                        musicVM.getComment(song)
                                    }
                                    showBottomSheet = false
                                }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete"
                            )
                            Spacer(Modifier.width(20.dp))
                            Text(
                                stringResource(id = R.string.del_comment),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
fun getTimeAgo(timestamp: Timestamp): String {
    // Lấy thời gian hiện tại
    val now = LocalDateTime.now()
    // Chuyển timestamp thành LocalDateTime
    val dateTime = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

    // Tính khoảng cách thời gian giữa thời điểm hiện tại và timestamp
    val duration = Duration.between(dateTime, now)

    val secondsAgo = duration.seconds.toDouble() // Số giây trước đây dưới dạng float

    val minutesAgo = secondsAgo / 60
    val hoursAgo = minutesAgo / 60
    val daysAgo = hoursAgo / 24
    val weeksAgo = daysAgo / 7
    val monthsAgo = daysAgo / 30
    val yearsAgo = daysAgo / 365

    return when {
        yearsAgo >= 1 -> String.format("%.1f years ago", yearsAgo)
        monthsAgo >= 1 -> String.format("%.1f months ago", monthsAgo)
        weeksAgo >= 1 -> String.format("%.1f weeks ago", weeksAgo)
        daysAgo >= 2 -> String.format("%.1f days ago", daysAgo)
        daysAgo >= 1 -> "Yesterday"
        hoursAgo >= 1 -> String.format("%.1f hours ago", hoursAgo)
        minutesAgo >= 1 -> String.format("%.1f minutes ago", minutesAgo)
        else -> "Just now"
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewMusic() {
   Column(
       Modifier
           .fillMaxSize()
           .background(Color.Black)
   ) {
   }
}