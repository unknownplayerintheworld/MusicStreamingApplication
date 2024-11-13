package com.hung.musicstreamingapplication.ui.screen

import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.ui.components.CustomButton
import com.hung.musicstreamingapplication.ui.components.itemRowAlbum
import com.hung.musicstreamingapplication.ui.components.itemRowAuthor
import com.hung.musicstreamingapplication.ui.components.itemRowLibrary
import com.hung.musicstreamingapplication.ui.components.itemRowMusic
import com.hung.musicstreamingapplication.ui.components.itemRowPlaylist
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LibraryScreen(
    musicVM:MusicViewModel,
    loginViewModel: LoginViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val isAddingSuccessful by musicVM.createdPlaylistStatus.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val userid by loginViewModel._currentUserId.collectAsState()
    val recenlyAlbumList by musicVM.albumRecentltList.collectAsState()
    val recentlyPlaylistList by musicVM.playlistRecentlyList.collectAsState()
    val favAlbums by musicVM.favAlbum.collectAsState()
    val favPlaylist by musicVM.favPlaylist.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0)
    val combineRecentlyList: List<Any>? = (recentlyPlaylistList as List<Any>) + (recenlyAlbumList as List<Any>)
    LaunchedEffect(isAddingSuccessful){
        if(isAddingSuccessful){
            Toast.makeText(context,R.string.successful_adding_playlist,Toast.LENGTH_LONG).show()
        }
        musicVM.setCreatedPlaylistStatus(false)
    }
    LaunchedEffect(key1 = Unit) {
        while (true) {
            // Gọi hàm randomSongLoading và getRecentlySong
            musicVM.getRecentlyList(userID = userid.toString(),3)
            musicVM.getFavListsInLibrary(userid.toString())

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
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
                Text(text = stringResource(id = R.string.library), color = MaterialTheme.colorScheme.onBackground, fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp))
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
        Row(
            Modifier.fillMaxWidth()
        ){
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                items(4){
                    when(it){
                        0 -> itemRowLibrary(icon = Icons.Default.FavoriteBorder, contentDescription = "Favourite", color = Color.Cyan, textRes = "Favourite", index = 1, navController = navController)
                        1 -> itemRowLibrary(iconRes = painterResource(id = R.drawable.baseline_arrow_circle_down_24),color = Color.Magenta, contentDescription = "Download", textRes = stringResource(
                            id = R.string.download_music_playlist
                        ), index = 2, navController = navController)
                        2 -> itemRowLibrary(iconRes = painterResource(id = R.drawable.baseline_cloud_upload_24), contentDescription = "Upload",textRes = stringResource(
                            id = R.string.upload
                        ), color = Color.Yellow, index = 3, navController = navController)
                        3 -> itemRowLibrary(icon = Icons.Default.AccountCircle,contentDescription = "Artist", color = Color(0xFFFFA500), textRes = stringResource(
                            id = R.string.author
                        ), index = 4, navController = navController)
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("recentlyDetail")
                }
                .padding(start = 15.dp, top = 10.dp, end = 5.dp)
        ){
            Text(text = stringResource(id = R.string.recently_play), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        LazyRow(
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier.fillMaxWidth()
        ){
            if (combineRecentlyList != null) {
                items(combineRecentlyList){
                    when(it){
                        is Album ->{
                            Card(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clickable {
                                        navController.navigate("playlist")
                                        musicVM.albumClicked(it)
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
                                            LocalContext.current).data(it.imageUrl).crossfade(true).build()),
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
                                        it.name?.let { it1 ->
                                            Text(
                                                it1,
                                                style = TextStyle(color = Color.White, fontSize = 12.sp),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
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
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                        is Playlist -> {
                            Card(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clickable {
                                        navController.navigate("playlist")
                                        musicVM.playlistClicked(it)
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
                                            LocalContext.current).data(it.imageUrl).crossfade(true).build()),
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
                                        it.name?.let { it1 ->
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
                        else ->{

                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.height(150.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            // Hành động khi bấm vào Button
                            navController.navigate("recentlyDetail")
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.DarkGray)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "More Items"
                        )
                    }
                    Text(text = stringResource(id = R.string.more), fontSize = 12.sp, fontWeight = FontWeight.Light)
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.padding(8.dp).width(200.dp)
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                              },
                    text = { Text("Playlist") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text("Album") }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                count = 2
            ) { page ->
                when (page) {
                    0 -> PlaylistSubScreen(list = favPlaylist, musicVM = musicVM,navController = navController,userid)
                    1 -> AlbumScreen(list = favAlbums,musicVM = musicVM,navController = navController)
                }
            }
        }
    }
}

@Composable
fun AlbumScreen(
    list: List<Album>?,
    musicVM: MusicViewModel,
    navController: NavHostController
) {
    Column(
        Modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp) // Thêm khoảng cách cho LazyColumn
        ) {
            if (!list.isNullOrEmpty()) {
                items(list.size) { index ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .height(90.dp)
                                .width(110.dp)
                                .padding(start = 15.dp, top = 10.dp, end = 15.dp)
                                .clickable {
                                    navController.navigate("playlist")
                                    musicVM.albumClicked(list[index])
                                },
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Transparent)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(list[index].imageUrl)
                                            .crossfade(true)
                                            .build()
                                    ),
                                    contentDescription = "Album Cover",
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
                                        .background(Color.Transparent)
                                        .padding(10.dp), contentAlignment = Alignment.BottomEnd
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.play_circle_24),
                                        contentDescription = "Play",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                        Text(
                            text = list[index].name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(start = 8.dp) // Thêm khoảng cách giữa icon và tên album
                        )
                    }
                }
            } else {
                // Giao diện khi list null hoặc empty
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_add_circle_24), // Thay đổi thành icon trống phù hợp
                                contentDescription = "No Albums",
                                modifier = Modifier.size(48.dp), // Kích thước icon
                                tint = MaterialTheme.colorScheme.onBackground.copy(0.8f) // Màu icon
                            )
                            Text(
                                text = stringResource(id = R.string.no_element),
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                                modifier = Modifier.padding(top = 8.dp) // Khoảng cách giữa icon và text
                            )
                            Text(
                                text = stringResource(id = R.string.add_text),
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                                modifier = Modifier.padding(top = 8.dp) // Khoảng cách giữa icon và text
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PlaylistSubScreen(
    list: List<Playlist>?,
    musicVM: MusicViewModel,
    navController: NavHostController,
    userID : String?
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf(TextFieldValue("")) }
    Column(
        Modifier.fillMaxSize()
    ) {
        LazyColumn{
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(90.dp)
                            .width(110.dp)
                            .padding(start = 15.dp, top = 10.dp, end = 15.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.clickable {
                                showDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Plus",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                    Text(text = stringResource(id = R.string.add_Playlist), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                }
            }
            if (!list.isNullOrEmpty()) {
                items(list.size){
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .height(90.dp)
                                .width(110.dp)
                                .padding(start = 15.dp, top = 10.dp, end = 15.dp)
                                .clickable {
                                    navController.navigate("playlist")
                                    musicVM.playlistClicked(list[it])
                                },
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Transparent)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(
                                            LocalContext.current
                                        ).data(list[it].imageUrl).crossfade(true).build()
                                    ),
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
                                        .background(Color.Transparent)
                                        .padding(10.dp), contentAlignment = Alignment.BottomEnd
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.play_circle_24),
                                        contentDescription = "Play",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                        Text(text = list[it].name, fontSize = 13.sp, fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.onBackground)
                        list[it].userName?.let {
                            Text(text = it, fontWeight = FontWeight.Light, fontSize = 12.sp)
                        }
                    }
                }
            }
//            else{
//                // Giao diện khi list null hoặc empty
//                item {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.baseline_add_circle_24), // Thay đổi thành icon trống phù hợp
//                                contentDescription = "No Albums",
//                                modifier = Modifier.size(48.dp), // Kích thước icon
//                                tint = MaterialTheme.colorScheme.onBackground.copy(0.8f) // Màu icon
//                            )
//                            Text(
//                                text = stringResource(id = R.string.no_element),
//                                fontSize = 20.sp,
//                                color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
//                                modifier = Modifier.padding(top = 8.dp) // Khoảng cách giữa icon và text
//                            )
//                            Text(
//                                text = stringResource(id = R.string.add_text),
//                                fontSize = 20.sp,
//                                color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
//                                modifier = Modifier.padding(top = 8.dp) // Khoảng cách giữa icon và text
//                            )
//                        }
//                    }
//                }
//            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter Playlist Name") },
            text = {
                TextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Playlist Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Xử lý thêm playlist
                        if (playlistName.text.isNotEmpty()) {
                            // Gọi hàm thêm playlist ở đây
                            // addNewPlaylist(Playlist(name = playlistName.text))
                            userID?.let { Playlist(name = playlistName.text, userID = it) }
                                ?.let { musicVM.createNewPlaylist(it) }
                            println("Adding playlist: ${playlistName.text}")
                        }
                        else{
                            Toast.makeText(context,R.string.error_adding_playlist_empty, Toast.LENGTH_SHORT).show()
                        }
                        showDialog = false // Đóng dialog
                        playlistName = TextFieldValue("") // Reset tên
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FixedNavbarWithTabs(
    navController: NavHostController,
    loginVM: LoginViewModel,
    musicVM: MusicViewModel
) {
    val tabItems = listOf("Playlist", "Album","Bài hát","Nghệ Sĩ")
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    // Bọc giao diện trong Scaffold
    Scaffold(
        topBar = {
            Column {
                // Navbar với các icon
                TopAppBar(
                    title = { Text(stringResource(id = R.string.recently_play),color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",tint = MaterialTheme.colorScheme.onBackground)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle action */ }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                )

                // TabRow cho TabLayout
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                    contentColor = Color.White
                ) {
                    tabItems.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Nội dung cuộn được
        HorizontalPager(
            count = tabItems.size,
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> PlaylistContentLib(navController, musicVM, loginVM = loginVM) // Trang Playlist
                1 -> AlbumContentLib(navController, musicVM, loginVM = loginVM) // Trang Album
                2 -> SongContentLib(navController,musicVM, loginVM = loginVM)
                3 -> AuthorContentLib(navController,musicVM, loginVM = loginVM)
            }
        }
    }
}

@Composable
fun AuthorContentLib(navController: NavHostController, musicVM: MusicViewModel,loginVM: LoginViewModel) {
    val recentlyAuthor by musicVM.authorRecently.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    LaunchedEffect(key1 = Unit) {
        while (true) {
            // Gọi hàm randomSongLoading và getRecentlySong
            musicVM.getRecentlyList(userID = userid.toString(),3)
            musicVM.getFavListsInLibrary(userid.toString())

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ){
            recentlyAuthor?.let {
                items(it.size){
                    itemRowAuthor(navController = navController, author = recentlyAuthor!![it], musicVM = musicVM)
                }
            }
        }
    }
}
@Composable
fun AuthorFav(navController: NavHostController, musicVM: MusicViewModel,loginVM: LoginViewModel) {
    val authorFavs by musicVM.authorFavList.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    LaunchedEffect(key1 = Unit) {
        while (true) {
            musicVM.getAuthorFavourite(userid.toString())

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ){
            authorFavs.let {
                items(it.size){
                    itemRowAuthor(navController = navController, author = authorFavs[it], musicVM = musicVM)
                }
            }
        }
    }
}
@Composable
fun SongContentLib(navController: NavHostController, musicVM: MusicViewModel,loginVM: LoginViewModel) {
    val recentlySong by musicVM.songlistRecently.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    LaunchedEffect(key1 = Unit) {
        while (true) {
            // Gọi hàm randomSongLoading và getRecentlySong
            musicVM.getFavListsInLibrary(userid.toString())

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ){
            recentlySong?.let {
                items(it.size){
                    itemRowMusic(musicVM = musicVM, navController = navController, song = recentlySong!![it], loginVM = loginVM)
                }
            }
        }
    }
}
@Composable
fun SongFav(navController: NavHostController, musicVM: MusicViewModel,loginVM: LoginViewModel) {
    val favSongs by musicVM.favSongList.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    LaunchedEffect(key1 = Unit) {
        while (true) {
            // Gọi hàm randomSongLoading và getRecentlySong
            musicVM.getFavSongList(userid.toString())

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ){
            favSongs.let {
                items(it.size){
                    itemRowMusic(musicVM = musicVM, navController = navController, song = favSongs[it], loginVM = loginVM)
                }
            }
        }
    }
}
@Composable
fun PlaylistContentLib(navController: NavHostController, musicVM: MusicViewModel,loginVM:LoginViewModel) {
    val recentlyPlaylist by musicVM.playlistRecentlyList.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    LaunchedEffect(key1 = Unit) {
        while (true) {
            // Gọi hàm randomSongLoading và getRecentlySong
            musicVM.getRecentlyList(userID = userid.toString(),3)
            musicVM.getFavListsInLibrary(userid.toString())

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ){
            recentlyPlaylist?.let {
                items(it.size){
                    itemRowPlaylist(navController = navController, playlist = recentlyPlaylist!![it], musicVM = musicVM)
                }
            }
        }
    }
}
@Composable
fun PlaylistFav(navController: NavHostController, musicVM: MusicViewModel,loginVM:LoginViewModel) {
    val favPlaylist by musicVM.favPlaylist.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    LaunchedEffect(key1 = Unit) {
        while (true) {
            // Gọi hàm randomSongLoading và getRecentlySong
            musicVM.getFavListsInLibrary(userid.toString())

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ){
            favPlaylist?.let {
                items(it.size){
                    itemRowPlaylist(navController = navController, playlist = favPlaylist!![it], musicVM = musicVM)
                }
            }
        }
    }
}

@Composable
fun AlbumContentLib(navController: NavHostController, musicVM: MusicViewModel,loginVM: LoginViewModel) {
    val recentlyAlbum by musicVM.albumRecentltList.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    LaunchedEffect(key1 = Unit) {
        while (true) {
            // Gọi hàm randomSongLoading và getRecentlySong
            musicVM.getRecentlyList(userID = userid.toString(),3)
            musicVM.getFavListsInLibrary(userid.toString())

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ){
            recentlyAlbum?.let {
                items(it.size){
                    itemRowAlbum(navController = navController, album = recentlyAlbum!![it], musicVM = musicVM)
                }
            }
        }
    }
}
@Composable
fun AlbumFav(navController: NavHostController, musicVM: MusicViewModel,loginVM: LoginViewModel) {
    val favAlbum by musicVM.favAlbum.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    LaunchedEffect(key1 = Unit) {
        while (true) {
            // Gọi hàm randomSongLoading và getRecentlySong
            musicVM.getFavListsInLibrary(userid.toString())

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.fillMaxSize()
        ){
            favAlbum?.let {
                items(it.size){
                    itemRowAlbum(navController = navController, album = favAlbum!![it], musicVM = musicVM)
                }
            }
        }
    }
}
@Composable
fun DownloadedScreen(
    musicVM: MusicViewModel,
    loginVM: LoginViewModel,
    navController: NavHostController
) {
    val downloadedSong by musicVM.downloadedSong.collectAsState()
    val isAsc by musicVM.isAsc.collectAsState()
    LaunchedEffect(downloadedSong) {
        musicVM.fetchDownloadedSongs()
        delay(1000*60*5)
    }
    Box(
        Modifier.fillMaxSize()
            .zIndex(2f)
            .statusBarsPadding()
            .padding(horizontal = 15.dp)
    ){
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            IconButton(onClick = {
                navController.popBackStack()
            }){
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack,contentDescription = null)
            }
            Row{
                IconButton(
                    onClick = {
                        navController.navigate("dSearch")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,contentDescription = null
                    )
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,contentDescription = null
                    )
                }
            }
        }
    }
    LazyColumn(
        Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(top = 60.dp,start = 15.dp,end = 15.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        item{
            Column(
                Modifier.fillMaxWidth()
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.downloaded),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    downloadedSong.size.toString()+" "+stringResource(R.string.song),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                CustomButton(onClick = {}, stringResource(R.string.random_playing_lib), textColor = Color.White , color = MaterialTheme.colorScheme.primary, enabled = true, fontWeight = FontWeight.Bold)
            }
            Column(
                Modifier.fillMaxWidth()
                    .padding(top = 15.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween
                ) {
                    Row(

                    ){
                        Icon(
                            painter = painterResource(R.drawable.baseline_filter_list_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                            )
                        Text(
                            stringResource(R.string.filter),
                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        Modifier.
                        clip(RoundedCornerShape(10.dp))
                        .clickable {
                            musicVM.setAscendingOrDescending()
                            musicVM.fetchDownloadedSongs()
                        }
                    ){
                        Text(
                            if(isAsc){
                                " A-Z"
                            }else{
                                " Z-A"
                            },
                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp
                        )
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                if (downloadedSong.isEmpty()) {
                    // Hiển thị thông báo khi không có bài hát nào được tải về
                    Text(text = "Chưa có lịch sử download", modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    downloadedSong.forEach { innerSongItem ->
                        Spacer(modifier = Modifier.height(2.dp))
                        itemRowMusic(musicVM, navController, loginVM,innerSongItem)
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchDownloadedSong(
    musicVM: MusicViewModel,
    loginVM: LoginViewModel,
    navController: NavHostController
){
    var searchText by remember {
        mutableStateOf("")
    }
    val searchSong by musicVM.searchDSongs.collectAsState()
    Box(Modifier.fillMaxSize()
        .statusBarsPadding()
        .zIndex(2f)){
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
                onValueChange = {
                    searchText = it
                    musicVM.getSongByNames(searchText)
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
                                musicVM.getSongByNames("")
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
    }
    LazyColumn(
        Modifier.fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = 60.dp, horizontal = 15.dp)
    ) {
        items(searchSong.size){
            Spacer(Modifier.height(2.dp))
            itemRowMusic(musicVM,navController,loginVM,searchSong[it])
        }
    }
}
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    musicVM:MusicViewModel,
    navController: NavHostController,
    loginVM: LoginViewModel
){
    val context = LocalContext.current
    val addState by musicVM.addFavSong.collectAsState()
    val delState by musicVM.delFavSong.collectAsState()
    LaunchedEffect(addState) {
        if(addState == 1){
            Toast.makeText(context,R.string.add_to_fav_success,Toast.LENGTH_SHORT).show()
        }else if(addState == 0){
            Toast.makeText(context,R.string.add_to_fav_failure,Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(delState) {
        if(delState == 1){
            Toast.makeText(context,R.string.del_from_fav_success,Toast.LENGTH_SHORT).show()
        }else if(delState == 0){
            Toast.makeText(context,R.string.del_from_fav_failure,Toast.LENGTH_SHORT).show()
        }
    }

        val tabItems = listOf("Playlist", "Album","Bài hát","Nghệ Sĩ")
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState()

        // Bọc giao diện trong Scaffold
        Scaffold(
            topBar = {
                Column {
                    // Navbar với các icon
                    TopAppBar(
                        title = { Text(stringResource(id = R.string.favourite),color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",tint = MaterialTheme.colorScheme.onBackground)
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* Handle action */ }) {
                                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onBackground)
                            }
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    // TabRow cho TabLayout
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                        contentColor = Color.White
                    ) {
                        tabItems.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = { Text(title) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            // Nội dung cuộn được
            HorizontalPager(
                count = tabItems.size,
                state = pagerState,
                modifier = Modifier.padding(innerPadding)
            ) { page ->
                when (page) {
                    0 -> PlaylistFav(navController, musicVM, loginVM = loginVM) // Trang Playlist
                    1 -> AlbumFav(navController, musicVM, loginVM = loginVM) // Trang Album
                    2 -> SongFav(navController,musicVM, loginVM = loginVM)
                    3 -> AuthorFav(navController,musicVM, loginVM = loginVM)
                }
            }
        }

}