package com.hung.musicstreamingapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hung.musicstreamingapplication.activity.HomeScreen
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.ui.components.customScaffold
import com.hung.musicstreamingapplication.ui.screen.PlayingScreen
import com.hung.musicstreamingapplication.viewmodel.HomeViewModel
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    loginVM: LoginViewModel,
    homeViewModel : HomeViewModel,
    musicVM: MusicViewModel,
    checkPermission:() -> Unit
) {
    NavHost(navController = navController, startDestination = "home"){
        composable("home"){
            customScaffold(navController = navController,musicVM) {
                paddingValues ->
                HomeScreen(loginVM = loginVM, viewmodel = homeViewModel, navController = navController, musicVM = musicVM)
            }
        }
        composable("library"){
//            LibraryScreem()
        }
        composable("profile"){
//            ProfileScreen()
        }
        composable("playing"){
            PlayingScreen(Song(), musicVM = musicVM,navController, { checkPermission() })
        }
    }
}