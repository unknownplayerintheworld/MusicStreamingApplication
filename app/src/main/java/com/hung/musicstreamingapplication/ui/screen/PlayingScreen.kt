package com.hung.musicstreamingapplication.ui.screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun PlayingScreen(
    song: Song,
    musicVM: MusicViewModel,
    navController: NavController,
    checkPermission: () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    // Thiết lập màu sắc cho thanh status bar
    SideEffect {
        systemUiController.setSystemBarsColor(Color.Transparent)
    }
    val context = LocalContext.current
    val durationState = remember{
        mutableStateOf(0L)
    }
    var currentSong by remember { mutableStateOf<Song?>(null) }
    val songReceiver = rememberUpdatedState(newValue = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val song = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra("CURRENT_SONG", Song::class.java)
            } else {
                intent?.getParcelableExtra<Song>("CURRENT_SONG")
            }
            currentSong = song
        }
    })
    val durationReceiver = rememberUpdatedState(newValue = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val duration = intent?.getLongExtra("DURATION_EXTRA",0L) ?: 0L
            durationState.value = duration
        }
    })
    val currentState by musicVM.currentState.collectAsState()
    val currentPosition by musicVM.currentPos.collectAsState()
    var isPlaying by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState()
    var isShuffled by remember {
        mutableStateOf(false)
    }
    var repeatMode by remember {
        mutableStateOf(0)
    }
    val permissionGranted by musicVM.isNotificationPermissionGranted.collectAsState()

    // Lưu trữ góc quay hiện tại
    var currentRotation by remember { mutableStateOf(0f) }

    val rotation = remember { Animatable(currentRotation) } // Tạo Animatable để điều khiển việc quay
    DisposableEffect(context){
        LocalBroadcastManager.getInstance(context).registerReceiver(
            songReceiver.value,
            IntentFilter("ACTION_UPDATE_CURRENTSONG")
        )
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(songReceiver.value)
        }
    }
    DisposableEffect(context){
        LocalBroadcastManager.getInstance(context).registerReceiver(
            durationReceiver.value,
            IntentFilter("ACTION_UPDATE_DURATION")
        )
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(durationReceiver.value)
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            // Quay liên tục khi isPlaying = true
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(6000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            // Lưu lại góc hiện tại và dừng quay khi isPlaying = false
            currentRotation = rotation.value
            rotation.stop() // Dừng quay
        }
    }
    LaunchedEffect(currentState){
        isPlaying = currentState==0 || currentState == 1
        Log.d("PlayingScreen:",currentState.toString())
//        if(currentState == 0){
//            musicVM.resetPosition()
//            musicVM.startAutoUpdatePosition()
//        }
    }
    var isVisible by remember { mutableStateOf(true) }

    // Hiệu ứng xuất hiện trượt lên và ẩn đi trượt xuống
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 4000)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 4000)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image with blur
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(
                        LocalContext.current
                    ).data(currentSong?.imageUrl).crossfade(true).build()
                ), // Thay thế bằng hình nền của bạn
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop, // Để hình ảnh phủ đầy toàn màn hình
                modifier = Modifier
                    .fillMaxSize()
                    .blur(40.dp) // Làm mờ hình ảnh nền
                    .zIndex(0f)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000))
            ) {


                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Now Playing",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick =
                        {
                            isVisible = false
                            navController.popBackStack()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent // Làm cho TopAppBar trong suốt
                    ),
                    actions = {
                        Spacer(modifier = Modifier.width(56.dp)) // Adjust to push the title to the center
                    },
                    modifier = Modifier.zIndex(10f)
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(0.7f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        HorizontalPager(
                            count = 3,
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            when (page) {
                                0 -> {
                                    // Hiển thị thông tin bài hát
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Card(
                                            modifier = Modifier
                                                .width(300.dp)
                                                .height(280.dp)
                                                .padding(10.dp, 0.dp, 10.dp, 0.dp),
                                            shape = RoundedCornerShape(150.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                                            content = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .graphicsLayer(rotationZ = rotation.value)
                                                ) {
                                                    Image(
                                                        painter = rememberAsyncImagePainter(
                                                            model = ImageRequest.Builder(
                                                                LocalContext.current
                                                            ).data(currentSong?.imageUrl)
                                                                .crossfade(true)
                                                                .build()
                                                        ),
                                                        contentDescription = "Song Image",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                }
                                            }
                                        )
                                        Spacer(Modifier.height(65.dp))
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            Arrangement.SpaceBetween
                                        ) {
                                            IconButton(onClick = {

                                            }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.baseline_share_24),
                                                    contentDescription = "share",
                                                    tint = Color.White
                                                )
                                            }
                                            Column(
                                                Modifier.width(200.dp),
                                                Arrangement.Center,
                                                Alignment.CenterHorizontally
                                            ) {
                                                currentSong?.name?.let {
                                                    Text(
                                                        text = it,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 1,
                                                        fontSize = 16.sp,
                                                        color = Color.White
                                                    )
                                                }
                                                Log.d("Song", currentSong.toString())
                                                currentSong?.authorName?.let {
                                                    Text(
                                                        it,
                                                        color = Color.White,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }
                                            IconButton(onClick = { /*TODO*/ }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.FavoriteBorder,
                                                    contentDescription = "Favourite",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    }
                                }

                                1 -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "lyrics",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 32.sp,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Tiếp tục với phần còn lại của giao diện người dùng (Slider, nút phát, v.v.)
                    Column(
                        Modifier
                            .width(500.dp)
                            .height(300.dp)
                    ) {
                        var seekPos by remember {
                            mutableStateOf(currentPosition)
                        }
                        LaunchedEffect(currentPosition){
                            seekPos = currentPosition
                        }
                        // Thanh tiến trình
                        Slider(
                            value = seekPos,
                            onValueChange = {
                                            seekPos = it
                            },
                            onValueChangeFinished = { musicVM.seekTo(seekPos.toLong()) },
                            valueRange = 0f..durationState.value.toFloat(),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(formatDuration(seekPos.toLong()), color = Color.White)
                            Text(formatDuration(durationState.value), color = Color.White)
                        }

                        // Điều khiển phát nhạc
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = {
                                isShuffled=!isShuffled
                                musicVM.shuffledSong(isShuffled = isShuffled)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_shuffle_24),
                                    contentDescription = if(isShuffled) "Shuffled" else "unShuffled",
                                    tint = if(isShuffled) MaterialTheme.colorScheme.primary else Color.White
                                )
                            }
                            IconButton(onClick = { musicVM.prevSong() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                                    contentDescription = "Previous",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = {
                                checkPermission()
                                if(!permissionGranted){
                                    musicVM.requestPermission()
                                }
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
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = { musicVM.nextSong() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_skip_next_24),
                                    contentDescription = "Next",
                                    tint = Color.White
                                )
                            }
                            BadgedBox(
                                badge = {
                                    if (repeatMode == 1) {
                                        Badge {
                                            Text(text = "1")
                                        }
                                    }
                                }
                            ) {
                                IconButton(onClick = {
                                    if (repeatMode in 0..1) {
                                        repeatMode++
                                        musicVM.repeatSong(repeatMode)
                                    } else {
                                        repeatMode = 0
                                        musicVM.repeatSong(repeatMode)
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_repeat_24),
                                        contentDescription = "Repeat",
                                        tint = if(repeatMode!=0) MaterialTheme.colorScheme.primary else Color.White
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { /* Shuffle */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_comment_24),
                                    contentDescription = "Shuffle",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = { /* Previous */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_library_add_24),
                                    contentDescription = "Previous",
                                    tint = Color.White
                                )
                            }
                            Column(
                                Modifier
                                    .fillMaxHeight()
                                    .padding(0.dp, 10.dp, 0.dp, 0.dp),
                                Arrangement.Top
                            ) {
                                Text("128kbps", color = Color.White)
                            }
                            IconButton(onClick = { /* Next */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_arrow_circle_down_24),
                                    contentDescription = "Next",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = { /* Repeat */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.library_music_24),
                                    contentDescription = "Repeat",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}
fun formatDuration(durationMillis: Long): String {
    val minutes = (durationMillis / 1000) / 60
    val seconds = (durationMillis / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
@Preview(showBackground = true)
@Composable
fun PreviewDisplay() {
//    PlayingScreen(song = Song(), musicVM = MusicViewModel(application = MusicApplication()))
}