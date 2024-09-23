package com.hung.musicstreamingapplication.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.activity.BottomNavigationItem
import com.hung.musicstreamingapplication.navigation.MainNavHost
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun customScaffold(
    navController: NavHostController,
    musicVM: MusicViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val items = listOf(
        BottomNavigationItem(
            title = "library",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List,
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

    var selectedItem by rememberSaveable {
        mutableStateOf("home")
    }
    var isPlaying by remember { mutableStateOf(false) }
    val currentSong by musicVM.currentSong.collectAsState()
    val currentState by musicVM.currentState.collectAsState()
    LaunchedEffect(currentState) {
        isPlaying = currentState == 0 || currentState == 1
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ) // Adjust padding for "floating" effect
                        .offset(y = (-12).dp) // Push it slightly up
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp) // Customize height
                            .background(
                                color = Color.White, // Set background color
                                shape = RoundedCornerShape(24.dp) // Rounded shape to make it look more circular
                            )
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected =
//                                    navController.currentBackStackEntry?.destination?.route == item.title,
                                selectedItem.equals(item.title)
                                ,
                                onClick = {
                                    navController.navigate(item.title) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                        selectedItem = (item.title)
                                    }

                                },
                                label = {

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
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    isPlaying = !isPlaying
                },
                    modifier = Modifier
                        .size(80.dp) // Size of the FAB
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
                            painter = rememberAsyncImagePainter(currentSong.imageUrl), // Use a URL or image source
                            contentDescription = "Album Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Play/Pause button
                        IconButton(onClick = {
                            if (currentState == 0 || currentState == 1) {
                                musicVM.pauseMusic()
                            } else if (currentState == 2) {
                                musicVM.resumeMusic()
                            }
                        }) {
                            Icon(
                                painter = if (isPlaying) painterResource(id = R.drawable.baseline_pause_24) else painterResource(
                                    id = R.drawable.baseline_play_arrow_24
                                ),
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp) // Size of the play/pause icon
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