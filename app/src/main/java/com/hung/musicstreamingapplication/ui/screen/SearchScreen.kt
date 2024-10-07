package com.hung.musicstreamingapplication.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.ui.components.itemRowAlbum
import com.hung.musicstreamingapplication.ui.components.itemRowAuthor
import com.hung.musicstreamingapplication.ui.components.itemRowMusic
import com.hung.musicstreamingapplication.ui.components.itemRowPlaylist
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    musicVM : MusicViewModel,
    navController: NavHostController
) {
    var searchText by remember {
        mutableStateOf("")
    }
    val keyword by musicVM.keyword.collectAsState()
    var keywordSearch by remember {
        mutableStateOf(mutableListOf<String>())
    }
    musicVM.getKeyword()
    LaunchedEffect(keyword){
        keywordSearch = keyword
        Log.d("SEARCH_SCREEN","search $keyword")
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
                    onValueChange = {
                                    searchText = it
                        musicVM.getAlbumSearchResults(searchText)
                        musicVM.getAuthorSearchResults(searchText)
                        musicVM.getSongSearchResults(searchText)
                        musicVM.getPlaylistSearchResults(searchText)
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
            if (searchText != "") {
                var selectedTabIndex by remember { mutableStateOf(0) }

                // Danh sách các tab
                val tabs = listOf("Bài hát", "Playlist", "Album", "Nghệ sĩ")

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
                        0 -> SongsContent(musicVM = musicVM, navController = navController)    // Nội dung cho tab "Bài hát"
                        1 -> PlaylistContent(musicVM = musicVM, navController = navController) // Nội dung cho tab "Playlist"
                        2 -> AlbumContent(musicVM = musicVM, navController = navController)    // Nội dung cho tab "Album"
                        3 -> ArtistContent(musicVM = musicVM, navController = navController)   // Nội dung cho tab "Nghệ sĩ"
                    }
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.recommend_for_you),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        // Hiển thị danh sách từ khóa
                        keyword.forEach { keyword ->
                            Surface(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable {
                                        searchText = keyword.trim()
                                        musicVM.getAlbumSearchResults(searchText)
                                        musicVM.getAuthorSearchResults(searchText)
                                        musicVM.getSongSearchResults(searchText)
                                        musicVM.getPlaylistSearchResults(searchText)
                                    },
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = keyword,
                                    modifier = Modifier.padding(8.dp),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistContent(
    musicVM: MusicViewModel,
    navController: NavHostController
) {
    val artists by musicVM.searchAuthor.collectAsState()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Text(text = stringResource(id = R.string.search_results),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)
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
            items(artists.size){
                itemRowAuthor(navController = navController, author = artists[it], musicVM = musicVM)
            }
        }
        if(artists.isNullOrEmpty()){
            Spacer(Modifier.height(100.dp))
            Column(Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.no_result_searching),color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AlbumContent(
    musicVM: MusicViewModel,
    navController: NavHostController
) {
    val albums by musicVM.searchAlbum.collectAsState()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Text(text = stringResource(id = R.string.search_results),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)
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
            items(albums.size){
                itemRowAlbum(navController = navController, album = albums[it], musicVM = musicVM)
            }
        }
        if(albums.isNullOrEmpty()){
            Spacer(Modifier.height(100.dp))
            Column(Modifier.fillMaxSize()) {
                Text(text = stringResource(id = R.string.no_result_searching),color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PlaylistContent(
    musicVM: MusicViewModel,
    navController: NavHostController
) {
    val playlists by musicVM.searchPlaylist.collectAsState()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Text(text = stringResource(id = R.string.search_results),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)
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
            items(playlists.size){
                itemRowPlaylist(navController = navController, playlist = playlists[it], musicVM = musicVM)
            }
        }
        if(playlists.isNullOrEmpty()){
            Spacer(Modifier.height(100.dp))
            Column(Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.no_result_searching),color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SongsContent(
    musicVM: MusicViewModel,
    navController: NavHostController
) {
    val songs by musicVM.searchSong.collectAsState()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Text(text = stringResource(id = R.string.search_results),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)
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
            items(songs.size){
                itemRowMusic(musicVM = musicVM, navController = navController, song = songs[it])
            }
        }
        if(songs.isNullOrEmpty()){
            Spacer(Modifier.height(100.dp))
            Column(Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.no_result_searching),color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SearchPrev() {
//    SearchScreen()
}