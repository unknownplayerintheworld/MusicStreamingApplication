package com.hung.musicstreamingapplication.ui.components

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.activity.BottomNavigationItem
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel
import com.hung.musicstreamingapplication.viewmodel.NavViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun customScaffold(
    navController: NavHostController,
    musicVM: MusicViewModel,
    loginVM: LoginViewModel,
    navVM: NavViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val items = listOf(
        BottomNavigationItem(
            title = "library",
            selectedIcon = Icons.AutoMirrored.Filled.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List,
            hasNews = false,
            navController = navController
        ),
        BottomNavigationItem(
            title = "home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            navController = navController
        ),
        BottomNavigationItem(
            title = "profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            hasNews = false,
            navController = navController
        ),
    )
    val selectedItem by navVM.itemSelected.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    var isPlaying by remember { mutableStateOf(false) }
    var currentSongBR by remember { mutableStateOf<Song?>(null) }
    val currentSong by musicVM.currentSong.collectAsState()
    val songReceiver = rememberUpdatedState(newValue = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val song = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra("CURRENT_SONG", Song::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableExtra<Song>("CURRENT_SONG")
            }
            currentSongBR = song
            if(currentSongBR!=null){
                musicVM.setCurrentSong(currentSongBR!!)
            }
        }
    })
    val currentState by musicVM.currentState.collectAsState()
    LaunchedEffect(currentState) {
        isPlaying = currentState == 0 || currentState == 1
        if(currentState == -1){
            userid?.let { musicVM.getMostRecentlySong(it) }
        }
    }
    DisposableEffect(context){
        LocalBroadcastManager.getInstance(context).registerReceiver(
            songReceiver.value,
            IntentFilter("ACTION_UPDATE_CURRENTSONG")
        )
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(songReceiver.value)
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                val shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                Column(
                    Modifier
                        .fillMaxWidth()
//                        .padding(
//                            horizontal = 16.dp,
//                            vertical = 8.dp
//                        ) // Adjust padding for "floating" effect
//                        .offset(y = (-12).dp) // Push it slightly up
                        .clip(shape)
                ) {
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        FloatingActionButton(
                            onClick = {

                            },
                            modifier = Modifier
                                .size(70.dp)
                                .zIndex(10f)// Size of the FAB
                                .background(Color.Transparent), // Transparent background for image
                            containerColor = Color.Transparent,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                // Album cover background image
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(
                                            LocalContext.current
                                        ).data(currentSong?.imageUrl)
                                            .crossfade(true)
                                            .build()
                                    ), // Use a URL or image source
                                    contentDescription = "Album Cover",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Log.d("CURRENT_SONG_BOTTOMNAV", currentSong.toString())

                            }
                        }
                        Row(
                            modifier = Modifier
                                .background(
                                    color = Color.Gray.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .zIndex(1f)
                                .clickable {
                                    onClickMiniBar(navController, musicVM, currentSong)
                                }
                                .fillMaxWidth()
                                .height(63.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.padding(75.dp,8.dp,0.dp,5.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(text = currentSong.name,color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(5.dp))
                                currentSong.authorName?.let { Text(text = it,color = Color.White, fontSize = 10.sp) }
                            }
                            Row {
                                IconButton(onClick = {
                                    if(currentState != -1){
                                        musicVM.prevSong()
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                                        contentDescription = "prev",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp) // Size of the play/pause icon
                                    )
                                }
                                IconButton(onClick = {
                                    isPlaying = !isPlaying
                                    when (currentState) {
                                        0, 1 -> {
                                            musicVM.pauseMusic()
                                        }
                                        2 -> {
                                            musicVM.resumeMusic()
                                        }
                                        -1 -> {
                                            musicVM.startMusicService(currentSong)
                                        }
                                    }
                                }) {
                                    Icon(
                                        painter = if (isPlaying)
                                            painterResource(id = R.drawable.baseline_pause_24)
                                        else painterResource(
                                            id = R.drawable.baseline_play_arrow_24
                                        ),
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp) // Size of the play/pause icon
                                    )
                                }
                                IconButton(onClick = {
                                    if(currentState!=-1){
                                        musicVM.nextSong()
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_skip_next_24),
                                        contentDescription = "next",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp) // Size of the play/pause icon
                                    )
                                }
                            }
                        }
                    }
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(85.dp) // Customize height
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant, // Set background color
                                shape = shape // Rounded shape to make it look more circular
                            )
                            .padding(bottom = 0.dp),
                    ) {
                        items.forEachIndexed { index, item ->
                            Log.d("NavBar1",selectedItem)
                            NavigationBarItem(
                                selected =
//                                    navController.currentBackStackEntry?.destination?.route == item.title,
                                selectedItem == item.title,
                                onClick = {
                                    navController.navigate(item.title) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                        navVM.setItemNavBar(item.title)
                                    }

                                },
                                label = {
                                    Text(text = item.title, color = if(selectedItem.equals(item.title)){
                                        Color(0xFF147354)
                                    }else{
                                        MaterialTheme.colorScheme.onBackground
                                    })
                                },
                                icon = {
                                    BadgedBox(badge = {
                                        if (item.badgeCount != null) {
                                            Badge {
                                                Text(text = item.badgeCount.toString())
                                            }
                                        } else if (item.hasNews) {
                                            Badge()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (selectedItem.equals(item.title))
                                                item.selectedIcon // sử dụng icon bôi đậm
                                            else item.unselectedIcon, // icon bình thường khi không được chọn
                                            contentDescription = item.title,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ){
             paddingValues ->
            content(paddingValues)
        }
    }
}

fun onClickMiniBar(navController: NavHostController,musicVM: MusicViewModel,song: Song) {
    musicVM.startMusicService(song)
    navController.navigate("playing")
}

@Preview(showBackground = true)
@Composable
fun preview() {

}