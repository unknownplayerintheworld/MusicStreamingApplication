package com.hung.musicstreamingapplication.ui.screen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.ui.components.CustomButton
import com.hung.musicstreamingapplication.ui.components.itemRowMusicPlaylist
import com.hung.musicstreamingapplication.ui.components.itemRowMusicSearchPlaylist
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlaylistScreen(
    playlist: Playlist,
    album: Album,
    navController: NavHostController,
    musicVM: MusicViewModel,
    loginVM: LoginViewModel
) {
    val context = LocalContext.current
    val songs by musicVM.songFromPlaylist.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val isRemoveSongFromPlaylist by musicVM.isRemoveSongFromPlaylist.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val isFavPlaylist by musicVM.isFavPlaylist.collectAsState()
    val isAlbumOrPlaylist by musicVM.isClickedAlbumOrPlaylist.collectAsState()
    val userID by loginVM._currentUserId.collectAsState()
    val isCreator by musicVM.isUserCreatedPlaylist.collectAsState()
    if(isAlbumOrPlaylist==2){
        userID?.let{
            musicVM.getFavouriteAlbumStatus(it, albumID = album.id)
            musicVM.getSongFromAlbum(albumID = album.id)
        }
    }
    else {
        userID?.let { musicVM.getFavouriteStatus(it, playlistID = playlist.id) }
        musicVM.getSongFromPlaylist(playlistID = playlist.id)
    }
    LaunchedEffect(Unit){
        userID?.let { musicVM.checkUserCreatedPlaylist(it,playlist) }
        delay(1000*60*10)
    }
    LaunchedEffect(isRemoveSongFromPlaylist){
        if(isRemoveSongFromPlaylist==1){
            Toast.makeText(context,R.string.remove_song_playlist_toast,Toast.LENGTH_SHORT).show()
            musicVM.getSongFromPlaylist(playlist.id)
        }
        else if(isRemoveSongFromPlaylist==0){
            Toast.makeText(context,R.string.remove_song_playlist_error,Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Image(
                painter = rememberAsyncImagePainter(model = ImageRequest.Builder(
                    LocalContext.current).data(
                    if(isAlbumOrPlaylist==2) {album.imageUrl}
                    else if(isAlbumOrPlaylist == 1 && !playlist.imageUrl.isNullOrBlank()) {playlist.imageUrl}
                    else if(!songs.isNullOrEmpty()){songs[0].imageUrl}
                    else { R.drawable.d3650077420d928b587a1feb77fa94cb }
                    ).crossfade(true).build()),
                contentDescription = "journey",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(top = 30.dp, bottom = 15.dp)
            )
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options",tint = MaterialTheme.colorScheme.onBackground)
            }
        }
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(if(isAlbumOrPlaylist==2){album.name}else{playlist.name},
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 5.dp))
            Text(text = "MUSA",
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 5.dp))
            val year = if(isAlbumOrPlaylist==2){album.created_at}else{playlist.created_at}?.let {
                Instant.ofEpochMilli(it.toDate().time)
                    .atZone(ZoneId.systemDefault())
                    .year
            }

            // Chuyển đổi năm thành chuỗi
            val yearString = year?.toString() ?: "Unknown Year"

            // Hiển thị tên playlist và năm tạo
            Text(
                text = if(isAlbumOrPlaylist==2){"Album • $yearString"}else{"Playlist • $yearString"},
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 5.dp)
            )
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Column(
                    Modifier.padding(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { /*TODO*/ },Modifier.padding(0.dp)) {
                        Icon(painter = painterResource(id = R.drawable.baseline_arrow_circle_down_24), contentDescription = "Download",tint = MaterialTheme.colorScheme.onBackground)
                    }
                    Text(text = stringResource(id = R.string.download_music_playlist),color = MaterialTheme.colorScheme.onBackground, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                CustomButton(onClick = {
                    navController.navigate("playing")
                    musicVM.startMusicService(songs[0], songs) }, text = "PHÁT NHẠC", textColor = Color.White , color = MaterialTheme.colorScheme.primary, enabled = true)
                Spacer(modifier = Modifier.width(10.dp))
                if(isCreator){
                    Column(
                        Modifier.padding(0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            navController.navigate("addSongPlaylist")
                        }, Modifier.padding(0.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_add_circle_24),
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.add_song_to_playlist),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 10.sp
                        )
                    }
                }
                else {
                    Column(
                        Modifier.padding(0.dp)
                    ) {
                        IconButton(onClick = {
                            userID?.let {
                                if (isAlbumOrPlaylist == 2) {
                                    musicVM.setFavouriteStatusAlbum(!isFavPlaylist, it, album.id)
                                } else {
                                    musicVM.setFavouriteStatus(
                                        !isFavPlaylist,
                                        it, playlist.id
                                    )
                                }
                            }
                        }, Modifier.padding(0.dp)) {
                            Icon(
                                imageVector = if (isFavPlaylist) {
                                    Icons.Default.Favorite
                                } else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favourite",
                                tint = if (isFavPlaylist) {
                                    MaterialTheme.colorScheme.primary
                                } else MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.favourite),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
        ) {
            LazyColumn{
                items(songs.size){
                    itemRowMusicPlaylist(it,navController = navController, song = songs[it], list = songs, musicVM = musicVM, playlist = playlist)
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
                                    if (isAlbumOrPlaylist == 2) {
                                        album.imageUrl
                                    } else {
                                        playlist.imageUrl
                                    }
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
                            text = if (isAlbumOrPlaylist == 2) {
                                album.name
                            } else {
                                playlist.name
                            },
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
                                userID?.let {
                                    if (isAlbumOrPlaylist == 2) {
                                        musicVM.setFavouriteStatusAlbum(
                                            !isFavPlaylist,
                                            it,
                                            album.id
                                        )
                                    } else {
                                        musicVM.setFavouriteStatus(
                                            !isFavPlaylist,
                                            it, playlist.id
                                        )
                                    }
                                }
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
fun addMusicToPlaylist(
    loginVM: LoginViewModel,
    musicVM : MusicViewModel,
    navController: NavHostController,
    playlist: Playlist
) {
    val isAddingSuccessful by musicVM.isAddingSuccessful.collectAsState()
    val userID by loginVM._currentUserId.collectAsState()
    val context = LocalContext.current
    var searchText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(isAddingSuccessful){
        if(isAddingSuccessful == 1){
            Toast.makeText(context,"Thêm thành công",Toast.LENGTH_SHORT).show()
        }
        else if(isAddingSuccessful == 0){
            Toast.makeText(context,"Bài hát đã có rồi",Toast.LENGTH_SHORT).show()
        }
        musicVM.resetStatusAdding()
    }
    LaunchedEffect(Unit){
        if(!searchText.isNullOrBlank()){
            musicVM.getOnlineSongByKW(searchText)
            userID?.let { musicVM.getRecentlySongByKW(it,30,searchText) }
        }
        else {
            musicVM.getOnlineSong()
            userID?.let { musicVM.getRecentSong(it, 30) }
        }
    }
    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            // search bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                BasicTextField(
                    value = searchText,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground, // Thay đổi màu chữ của văn bản
                        fontSize = 14.sp // Kích thước chữ (tuỳ chọn)
                    ),
                    onValueChange = { it ->
                        searchText = it
                        if(!searchText.isNullOrBlank()){
                            musicVM.getOnlineSongByKW(searchText)
                            userID?.let { musicVM.getRecentlySongByKW(it,30,searchText) }
                        }
                        else {
                            musicVM.getOnlineSong()
                            userID?.let { musicVM.getRecentSong(it, 30) }
                        }
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(2.dp) // Điều chỉnh padding
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ), // Padding xung quanh text
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchText.isEmpty()) { // Giả sử input rỗng thì hiện placeholder
                                    Text(
                                        text = "Tìm kiếm bài hát, nghệ sĩ,...",
                                        color = Color.Gray
                                    )
                                }
                                innerTextField() // Phần nhập liệu
                            }
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchText = ""
                                })
                                {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        }
                    }
                )
            }
                var selectedTabIndex by remember { mutableStateOf(0) }

                // Danh sách các tab
                val tabs = listOf("Online","Gần đây")

                Column {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        contentColor = MaterialTheme.colorScheme.onBackground,    // Màu của văn bản và indicator
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = tab) }
                            )
                        }
                    }

                    // Nội dung tương ứng với mỗi tab
                    when (selectedTabIndex) {
                        0 -> SongOnlineContent(musicVM = musicVM, navController = navController,playlist = playlist)    // Nội dung cho tab "Bài hát"
                        1 -> SongRecentlyContent(musicVM = musicVM, navController = navController,playlist = playlist)
                    }
                }

        }
    }
}

@Composable
fun SongOnlineContent(
    navController: NavHostController,
    musicVM: MusicViewModel,
    playlist: Playlist
) {
    val songs by musicVM.searchOnlineSong.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Text(
            text = stringResource(id = R.string.search_results),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (songs.isNullOrEmpty()) {
                // Hiển thị thông báo khi không có kết quả
                item {
                    Spacer(Modifier.height(100.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_result_searching),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                items(songs!!.size) {
                    itemRowMusicSearchPlaylist(musicVM = musicVM, navController = navController, song = songs!![it], playlist = playlist)
                }
            }
        }
    }
}
@Composable
fun SongRecentlyContent(
    navController: NavHostController,
    musicVM: MusicViewModel,
    playlist: Playlist
) {
    val songs by musicVM.searchRecentlySong.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Text(
            text = stringResource(id = R.string.search_results),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (songs.isNullOrEmpty()) {
                // Hiển thị thông báo khi không có kết quả
                item {
                    Spacer(Modifier.height(100.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_result_searching),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                items(songs!!.size) {
                    itemRowMusicSearchPlaylist(musicVM = musicVM, navController = navController, song = songs!![it],playlist)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewPlaylist() {
//    PlaylistScreen()
}