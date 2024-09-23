package com.hung.musicstreamingapplication.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.PermissionChecker
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hung.musicstreamingapplication.navigation.MainNavHost
import com.hung.musicstreamingapplication.ui.screen.HeaderHome
import com.hung.musicstreamingapplication.ui.screen.HotAlbum
import com.hung.musicstreamingapplication.ui.screen.RecentTrack
import com.hung.musicstreamingapplication.ui.screen.Recommend
import com.hung.musicstreamingapplication.ui.screen.Trending
import com.hung.musicstreamingapplication.ui.theme.MusicStreamingApplicationTheme
import com.hung.musicstreamingapplication.viewmodel.HomeViewModel
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val navController: NavHostController
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Quyền đã được cấp, bắt đầu foreground service
                Toast.makeText(this,"Well done!",Toast.LENGTH_SHORT).show()
            } else {
                // Quyền bị từ chối, xử lý theo logic của bạn
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        checkNotificationPermission()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val loginVM = hiltViewModel<LoginViewModel>()
            val navController = rememberNavController()
            val musicVM = hiltViewModel<MusicViewModel>()
            MainScreen(homeViewModel,loginVM, navController = navController, musicVM = musicVM,checkNotificationPermission = {checkNotificationPermission()})
        }
    }
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PermissionChecker.PERMISSION_GRANTED) {
                // Yêu cầu quyền POST_NOTIFICATIONS
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Quyền đã được cấp, bắt đầu foreground service

            }
        } else {
            // Nếu không phải Android 13 trở lên, không cần yêu cầu quyền

        }
    }

}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    homeviewmodel : HomeViewModel,
    loginVM: LoginViewModel,
    musicVM: MusicViewModel,
    navController: NavHostController,
    checkNotificationPermission:()->Unit
) {
    MusicStreamingApplicationTheme {
        MainNavHost(navController = navController, loginVM = loginVM, homeViewModel = homeviewmodel, musicVM = musicVM,
            { checkNotificationPermission() })
    }
}

@Composable
fun HomeScreen(
    loginVM : LoginViewModel,
    viewmodel : HomeViewModel,
    navController: NavHostController,
    musicVM: MusicViewModel
) {
    val scrollState = rememberScrollState()
    val listsong by viewmodel.song.collectAsState()
    val userid by loginVM._currentUserId.collectAsState()
    val recentlySong by viewmodel.recentlySong.collectAsState()
    val recommendSong by viewmodel.recommendSong.collectAsState()
    val trending by viewmodel.trending.collectAsState()
    val hotalbum by viewmodel.hotalbum.collectAsState()
    LaunchedEffect(key1 = Unit) {
        while (true) {

            // Gọi hàm randomSongLoading và getRecentlySong
            viewmodel.randomSongLoading()
            viewmodel.getRecentlySong(userid.toString())
            viewmodel.getRecommendSongs(userid.toString())
            viewmodel.getTrending()
            viewmodel.getHotAlbum()

            // Đợi 5 phút (5 * 60 * 1000 milliseconds)
            delay(5 * 60 * 1000L) // 5 phút
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
//            .padding(paddingValues)
    ) {
            HeaderHome(listsong,navController,musicVM)
            RecentTrack(recentlySong)
            Recommend(playlist = recommendSong)
            HotAlbum(hotalbum)
            Trending(trending)
        }
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicStreamingApplicationTheme {
//        Greeting()
    }
}