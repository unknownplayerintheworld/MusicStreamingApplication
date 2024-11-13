package com.hung.musicstreamingapplication.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.PermissionChecker
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hung.musicstreamingapplication.R
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
import com.hung.musicstreamingapplication.viewmodel.NavViewModel
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

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation",
        "WrongConstant"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window,false)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Quyền đã được cấp, bắt đầu foreground service
                Toast.makeText(this,"Well done!",Toast.LENGTH_SHORT).show()
            } else {
                showPermissionRationale()
                // Quyền bị từ chối, xử lý theo logic của bạn
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        checkNotificationPermission()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
//        insetsController.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
//        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val loginVM = hiltViewModel<LoginViewModel>()
            val navVM = hiltViewModel<NavViewModel>()
            val navController = rememberNavController()
            val musicVM = hiltViewModel<MusicViewModel>()
            MainScreen(homeViewModel,loginVM, navController = navController, musicVM = musicVM, navViewModel = navVM,checkNotificationPermission = {checkNotificationPermission()})
        }
    }
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PermissionChecker.PERMISSION_GRANTED) {
                Toast.makeText(this,"Application will crash after 30s playing if you dont permission",Toast.LENGTH_LONG).show()
                // Yêu cầu quyền POST_NOTIFICATIONS

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Quyền đã được cấp, bắt đầu foreground service

            }
        } else {
            // Nếu không phải Android 13 trở lên, không cần yêu cầu quyền

        }
    }
    private fun showPermissionRationale() {
        // Hiển thị thông báo hoặc dialog để giải thích lý do cần quyền
        AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("This permission is needed to show notifications for music playback.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Hướng dẫn người dùng mở cài đặt ứng dụng nếu cần
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    homeviewmodel : HomeViewModel,
    loginVM: LoginViewModel,
    musicVM: MusicViewModel,
    navController: NavHostController,
    navViewModel: NavViewModel,
    checkNotificationPermission:()->Unit
) {

    MusicStreamingApplicationTheme {
        MainNavHost(navController = navController, loginVM = loginVM, homeViewModel = homeviewmodel, musicVM = musicVM, navViewModel = navViewModel,
            checkPermission = { checkNotificationPermission() })
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
    val systemUiController = rememberSystemUiController()

    // Thiết lập màu sắc cho status bar thành trong suốt
    SideEffect {
        systemUiController.setSystemBarsColor(Color.Transparent)
    }
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
            .systemBarsPadding()
            .padding(bottom = 70.dp)
    ) {
            HeaderHome(listsong,navController,musicVM)
            RecentTrack(recentlySong, musicVM = musicVM, navController = navController, loginVM = loginVM)
            Recommend(playlist = recommendSong, navController = navController,musicVM)
            HotAlbum(hotalbum,navController, musicVM)
            Trending(trending, navController = navController, musicVM = musicVM)
        }
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicStreamingApplicationTheme {
//        Greeting()
    }
}