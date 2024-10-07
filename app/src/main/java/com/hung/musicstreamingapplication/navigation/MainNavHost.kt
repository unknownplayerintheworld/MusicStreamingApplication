package com.hung.musicstreamingapplication.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hung.musicstreamingapplication.activity.HomeScreen
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.ui.components.customScaffold
import com.hung.musicstreamingapplication.ui.screen.AuthorScreen
import com.hung.musicstreamingapplication.ui.screen.FixedNavbarWithTabs
import com.hung.musicstreamingapplication.ui.screen.LibraryScreen
import com.hung.musicstreamingapplication.ui.screen.PlayingScreen
import com.hung.musicstreamingapplication.ui.screen.PlaylistScreen
import com.hung.musicstreamingapplication.ui.screen.ProfileScreen
import com.hung.musicstreamingapplication.ui.screen.SearchScreen
import com.hung.musicstreamingapplication.ui.screen.addMusicToPlaylist
import com.hung.musicstreamingapplication.viewmodel.HomeViewModel
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel
import com.hung.musicstreamingapplication.viewmodel.NavViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavHost(
    navController: NavHostController,
    loginVM: LoginViewModel,
    homeViewModel : HomeViewModel,
    navViewModel: NavViewModel,
    musicVM: MusicViewModel,
    checkPermission:() -> Unit
) {
    val playlist by musicVM.playlistClicked.collectAsState()
    val album by musicVM.albumClicked.collectAsState()
    val author by musicVM.author.collectAsState()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route

    // Kiểm tra xem màn hình hiện tại có phải là "playing" không
    val isPlayingScreen = currentDestination == "playing"
    if (!isPlayingScreen) {
        customScaffold(navController = navController, musicVM = musicVM, loginVM = loginVM, navVM = navViewModel) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(loginVM = loginVM, viewmodel = homeViewModel, navController = navController, musicVM = musicVM)
                }
                composable("library") {
                    LibraryScreen(musicVM = musicVM, navController = navController, loginViewModel = loginVM)
                }
                composable("profile") {
                    ProfileScreen(musicVM = musicVM, navController = navController, loginVM = loginVM)
                }
                composable("search") {
                    SearchScreen(musicVM = musicVM, navController = navController)
                }
                composable("playlist") {
                    PlaylistScreen(playlist = playlist, navController = navController, musicVM = musicVM, loginVM = loginVM, album = album)
                }
                composable("author") {
                    AuthorScreen(musicVM, navController, author)
                }
                composable("recentlyDetail") {
                    FixedNavbarWithTabs(navController = navController, musicVM = musicVM, loginVM = loginVM)
                }
                composable("addSongPlaylist") {
                    addMusicToPlaylist(loginVM = loginVM, musicVM = musicVM, navController = navController, playlist)
                }
                // Màn hình "playing" được tách biệt và hiển thị không dùng Scaffold
                composable("playing") {
                    PlayingScreen(Song(), musicVM = musicVM, navController = navController, loginVM = loginVM) {
                        checkPermission()
                    }
                }
            }
        }
    } else {
        // Khi ở màn hình "playing", chỉ hiển thị PlayingScreen mà không có Scaffold
        PlayingScreen(Song(), musicVM = musicVM, navController = navController, loginVM = loginVM) {
            checkPermission()
        }
    }
}