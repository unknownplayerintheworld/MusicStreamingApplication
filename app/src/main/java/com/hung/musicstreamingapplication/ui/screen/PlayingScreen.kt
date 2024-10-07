package com.hung.musicstreamingapplication.ui.screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.hung.musicstreamingapplication.data.model.Comment
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.ui.components.IconWithBadge
import com.hung.musicstreamingapplication.ui.components.itemRowComment
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.MusicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun PlayingScreen(
    song: Song,
    musicVM: MusicViewModel,
    loginVM: LoginViewModel,
    navController: NavController,
    checkPermission: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val isCommentSuccess by musicVM.isSuccessfulComment.collectAsState()
    val commentList by musicVM.comment.collectAsState()
    val userID by loginVM._currentUserId.collectAsState()
    val isSave by musicVM.isSave.collectAsState()
    val isReplyingStatus by musicVM.isReplying.collectAsState()
    // Thiết lập màu sắc cho thanh status bar
    SideEffect {
        systemUiController.setSystemBarsColor(Color.Transparent)
    }
    LaunchedEffect(isReplyingStatus){
        Log.d("IS_REPLYING_CMT",isReplyingStatus.toString())
    }
    val context = LocalContext.current
    val durationState = remember{
        mutableStateOf(0L)
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isHistoryUpdated = remember { mutableStateOf(false) }
    LaunchedEffect(isCommentSuccess){
        if(isCommentSuccess == 1){
            Toast.makeText(context, R.string.successful_comment,Toast.LENGTH_SHORT).show()
            currentSong?.let { musicVM.getComment(it) }
        }
        else if(isCommentSuccess == 0){
            Toast.makeText(context, R.string.failure_comment,Toast.LENGTH_SHORT).show()
        }
    }
    val songReceiver = rememberUpdatedState(newValue = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val song = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra("CURRENT_SONG", Song::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableExtra<Song>("CURRENT_SONG")
            }
            currentSong = song
            userID?.let{
                currentSong?.let {
                        it1 -> musicVM.savePlayCount(it, it1.id)
                    if (!isHistoryUpdated.value) {
                            userID?.let { currentSong?.let { it1 -> musicVM.upsertHistory(it, it1.id, "song") } }
                            isHistoryUpdated.value = true // Đánh dấu rằng lịch sử đã được cập nhật
                        }

                }
            }
            currentSong?.let { musicVM.getComment(it) }
            Log.d("SONG_CURRENT",song.toString())
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
    LaunchedEffect(isSave){
        if(isSave){
            Log.d("SAVE_PC","Successfully updating playcount")
        }
        else{
            Log.d("SAVE_PC","failure updating playcount")
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
                                .statusBarsPadding(),
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
                                tint = Color.White,
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
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(Modifier.height(100.dp))
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
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        // Danh sách lời bài hát và thời gian
                                        val lyricsList = currentSong?.let { parseLrc(it.lyrics) } ?: emptyList()

                                        // Hiển thị từng câu lời bài hát
                                        lyricsList.forEachIndexed { index, lyricPair ->
                                            val lyricTime = lyricPair.first
                                            val lyricText = lyricPair.second

                                            // Kiểm tra nếu thời gian hiện tại đã vượt qua thời gian của câu này
                                            val isLyricPassed = currentPosition.toLong() >= lyricTime

                                            // Chọn màu sắc cho câu dựa trên thời gian hiện tại
                                            val lyricColor = if (isLyricPassed) {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) // Đã phát
                                            } else {
                                                MaterialTheme.colorScheme.onBackground // Chưa phát
                                            }

                                            // Hiển thị từng câu với màu phù hợp
                                            Text(
                                                text = lyricText,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                lineHeight = 28.sp,
                                                color = lyricColor,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
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
                                if(!commentList.isNullOrEmpty()){
                                    IconButton(onClick = { showBottomSheet = true }) {


                                        IconWithBadge(
                                            contentDescription = "Comment",
                                            badgeCount = commentList!!.size,
                                            painter = painterResource(
                                                id = R.drawable.baseline_comment_24
                                            )
                                        )
                                    }
                                }
                                else{
                                    IconButton(onClick = {
                                        showBottomSheet = true
                                    }) {


                                        IconWithBadge(
                                            contentDescription = "Comment",
                                            badgeCount = 0,
                                            painter = painterResource(
                                                id = R.drawable.baseline_comment_24
                                            )
                                        )
                                    }
                                }
//                            IconWithBadge(contentDescription = "Comment", badgeCount = commentList.size)
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
        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = {
                showBottomSheet = false
            }, sheetState = sheetState) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(bottom = 70.dp) // Để lại khoảng trống cho TextField ở dưới
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(top = 15.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(end = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = if (commentList?.size!! > 1) {
                                        "${commentList!!.size} ${stringResource(id = R.string.comment)}s"
                                    } else {
                                        "${commentList!!.size} ${stringResource(id = R.string.comment)}"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Icon(imageVector = Icons.Outlined.Close, contentDescription = "close")
                            }
                        }
                        HorizontalDivider()
                        Spacer(Modifier.height(5.dp))

                        // Comment list
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f) // Chiếm hết phần còn lại của màn hình
                                .padding(start = 15.dp, top = 0.dp, end = 15.dp, bottom = 10.dp)
                        ) {
                            commentList?.size?.let {
                                items(it) { index ->
                                    currentSong?.let { it1 -> itemRowComment(it1,
                                        commentList!![index], musicVM,loginVM) }
                                }
                            }
                        }
                    }

                    // TextField at the bottom
                    var commentText by remember { mutableStateOf("") }

                    Box(
                        Modifier
                            .align(Alignment.BottomCenter) // Cố định TextField ở dưới cùng
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(24.dp)
                            )
                    ) {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if(!isReplyingStatus.username.isNullOrEmpty()) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.reply_to) + isReplyingStatus.username,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Light,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        modifier = Modifier.clickable {
                                            musicVM.setReplyingStatus(false, Comment())
                                        })
                                }
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = commentText,
                                    onValueChange = {
                                        commentText = it
                                    },
                                    placeholder = {
                                        Text(
                                            fontSize = 14.sp,
                                            text = stringResource(id = R.string.write_a_comment),
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .padding(horizontal = 8.dp),
                                    maxLines = 1,
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent, // Ẩn đường viền khi được focus
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp, // Kích thước phông chữ
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                                if (!commentText.isNullOrBlank()) {
                                    IconButton(
                                        onClick = {
                                            // Action to send comment
                                            if (commentText.isNotEmpty()) {
                                                // Gọi hàm gửi bình luận
                                                // musicVM.postComment(commentText)
                                                if(!isReplyingStatus.username.isNullOrEmpty()){
                                                    userID?.let { currentSong?.let { it1 ->
                                                        musicVM.writeComment(it,commentText,
                                                            it1.id,isReplyingStatus.id)
                                                    } }
                                                }else{
                                                    currentSong?.id?.let {
                                                        userID?.let { it1 ->
                                                            musicVM.writeComment(
                                                                it1, commentText,
                                                                it, ""
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            CoroutineScope(Dispatchers.Main).launch {
                                                if(!isReplyingStatus.username.isNullOrEmpty()){
                                                    musicVM.getChildComments(isReplyingStatus.id)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Send Comment",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
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
fun parseLrc(lrcContent: String): List<Pair<Long, String>> {
    val lyrics = mutableListOf<Pair<Long, String>>()
    val lines = lrcContent.split("\n")

    for (line in lines) {
        val timeRegex = """\[(\d{2}):(\d{2}).(\d{2})\]""".toRegex()
        val match = timeRegex.find(line)

        if (match != null) {
            val minutes = match.groups[1]?.value?.toLong() ?: 0
            val seconds = match.groups[2]?.value?.toLong() ?: 0
            val milliseconds = match.groups[3]?.value?.toLong() ?: 0

            val totalMilliseconds = (minutes * 60 + seconds) * 1000 + milliseconds
            val lyricText = line.substringAfter(match.value).trim()

            lyrics.add(Pair(totalMilliseconds, lyricText))
        }
    }
    return lyrics
}
@Preview(showBackground = true)
@Composable
fun PreviewDisplay() {
//    PlayingScreen(song = Song(), musicVM = MusicViewModel(application = MusicApplication()))
}