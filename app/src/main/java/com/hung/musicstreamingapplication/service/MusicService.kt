package com.hung.musicstreamingapplication.service

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.PermissionChecker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager.ACTION_NEXT
import androidx.media3.ui.PlayerNotificationManager.ACTION_PAUSE
import androidx.media3.ui.PlayerNotificationManager.ACTION_PLAY
import androidx.media3.ui.PlayerNotificationManager.ACTION_PREVIOUS
import com.hung.musicstreamingapplication.MusicApplication
import com.hung.musicstreamingapplication.R
import com.hung.musicstreamingapplication.data.model.Song
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable
import javax.inject.Inject
@AndroidEntryPoint
class MusicService : Service() {

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var mediaSession: MediaSessionCompat
    var song:Song? = null
    private lateinit var currentList: ArrayList<Song>

    @Inject
    lateinit var exoPlayer: ExoPlayer
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val CHANNEL_ID = "music_channel"
        private const val NOTIFICATION_ID = 100
    }

    var hasStartedPlayback = false
    private val app: MusicApplication?
        get() = application as? MusicApplication
    private val updateRunnable = object : Runnable{
        override fun run() {
            updatePositionApp()
            handler.postDelayed(this,1000)
        }
    }
    private val updatePositionRunnable = object : Runnable {
        override fun run() {
            // Giả sử `player` là đối tượng đang phát nhạc của bạn
            val currentPosition = exoPlayer.currentPosition
            val duration = exoPlayer.duration

            updatePlaybackState(exoPlayer.isPlaying, currentPosition, duration)
            updateNotification(song)

            // Tiếp tục cập nhật mỗi giây
            handler.postDelayed(this, 1000)
        }
    }

    private fun startUpdatingPosition() {
        handler.post(updatePositionRunnable)
    }
    private fun updatePositionApp() {
        app?.let{
            it.currentPosition.value = exoPlayer.currentPosition
        }
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
//        val notification = createNotification()
//        startForeground(1,notification)
//        createNotificationChannel()
        notificationManager = NotificationManagerCompat.from(this)


        handler.post(updateRunnable)

        exoPlayer.addListener(object : Player.Listener{
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if(exoPlayer.playbackState == Player.STATE_READY){
                    val duration = exoPlayer.duration
                    sendDurationBroadcast(duration)
                    val currentMediaItem = exoPlayer.currentMediaItem

                    // Nếu MediaItem đang phát không null, so sánh với currentList
                    if (currentMediaItem != null) {
                        val currentMediaId = currentMediaItem.mediaId

                        // Sử dụng map hoặc for-loop để so sánh với currentList
                        currentList.forEach { song ->
                            if (song.id == currentMediaId) {
                                // Đây là bài hát hiện đang phát
                                sendCurrentSong(song)
                            }
                        }
                    }
                    sendPlayBackStateBroadcast("ACTION_PLAYBACK_STARTED")
                    hasStartedPlayback = false
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if(playbackState == Player.STATE_READY){
                    if(exoPlayer.isPlaying) {
                        if(!hasStartedPlayback){
                            sendPlayBackStateBroadcast("ACTION_PLAYBACK_STARTED")
                            hasStartedPlayback = true
                        }
                        val currentMediaItem = exoPlayer.currentMediaItem

                        // Nếu MediaItem đang phát không null, so sánh với currentList
                        if (currentMediaItem != null) {
                            val currentMediaId = currentMediaItem.mediaId

                            // Sử dụng map hoặc for-loop để so sánh với currentList
                            currentList.forEach { song ->
                                if (song.id == currentMediaId) {
                                    // Đây là bài hát hiện đang phát
                                    sendCurrentSong(song)
                                }
                            }
                        }
                        val duration = exoPlayer.duration
                        sendDurationBroadcast(duration = duration)
//                        sendPlayBackStateBroadcast("ACTION_PLAYBACK_PLAYING")
                    }
                    else{
                        sendPlayBackStateBroadcast("ACTION_PLAYBACK_PAUSE")
                    }
                } else if(playbackState == Player.STATE_ENDED){
                    sendPlayBackStateBroadcast("ACTION_PLAYBACK_COMPLETED")
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if(isPlaying){
                    sendPlayBackStateBroadcast("ACTION_PLAYBACK_PLAYING")
                }
                else{
                    sendPlayBackStateBroadcast("ACTION_PLAYBACK_PAUSE")
                }
            }

            override fun onEvents(player: Player, events: Player.Events) {
//                if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)) {
//                    val reason = player.currentPositionDiscontinuityReason
//                    when (reason) {
//                        Player.DISCONTINUITY_REASON_SEEK -> {
//                            Log.d("ExoPlayer", "Position changed due to seek")
//                            // Thông báo khi người dùng tua
//                        }
//                        Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> {
//                            Log.d("ExoPlayer", "Auto transition to next song")
//                            // Thông báo khi ExoPlayer tự động chuyển bài
//                        }
//                        // Bạn có thể bắt thêm các lý do khác nếu cần
//                    }
//                }
            }
        })
    }

    @OptIn(UnstableApi::class)
    private fun updateNotification(song: Song?) {
        val playPauseAction = if (exoPlayer.isPlaying) {
            NotificationCompat.Action(
                R.drawable.baseline_pause_24, "Pause",
                getPendingIntentForAction(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.play_circle_24, "Play",
                getPendingIntentForAction(ACTION_PLAY)
            )
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song?.name)
            .setContentText(song?.authorName)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(NotificationCompat.Action(R.drawable.baseline_skip_previous_24, "Previous", getPendingIntentForAction(ACTION_PREVIOUS)))
            .addAction(playPauseAction)
            .addAction(NotificationCompat.Action(R.drawable.baseline_skip_next_24, "Next", getPendingIntentForAction(ACTION_NEXT)))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(1)
            )
            .build()

        // Update the existing notification
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }
    private fun sendDurationBroadcast(duration: Long){
        val intent = Intent("ACTION_UPDATE_DURATION").apply {
            putExtra("DURATION_EXTRA", duration)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    private fun sendCurrentSong(song: Song){
        val intent = Intent("ACTION_UPDATE_CURRENTSONG").apply {
            putExtra("CURRENT_SONG",song)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    private fun resumePlayback() {
        // Kiểm tra nếu ExoPlayer đã có dữ liệu và không phải đang chơi
        if (!exoPlayer.isPlaying && exoPlayer.playbackState == Player.STATE_READY) {
            exoPlayer.play() // Tiếp tục phát từ vị trí hiện tại
            sendPlayBackStateBroadcast("ACTION_PLAYBACK_RESUME")
        }
    }

    fun seekTo(position: Long){
        exoPlayer.seekTo(position)
        sendPlayBackStateBroadcast("ACTION_PLAYBACK_POSITION")
    }

    private fun sendPlayBackStateBroadcast(action: String){
        val intent = Intent(action)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }



    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Nhận danh sách bài hát từ Intent
        val songList: ArrayList<Song>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33 trở lên sử dụng phiên bản mới của getParcelableArrayListExtra
            intent?.getParcelableArrayListExtra("EXTRA_SONG_LIST", Song::class.java)
        } else {
            // API thấp hơn sử dụng phương thức cũ
            intent?.getParcelableArrayListExtra("EXTRA_SONG_LIST")
        }
        if (songList != null) {
            currentList = songList
        }
        if (!songList.isNullOrEmpty()) {
            // Tạo danh sách MediaItem từ danh sách Song và gán mediaId là id của bài hát
            val mediaItems = songList.map { song ->
                MediaItem.Builder()
                    .setUri(song.link)   // Đặt URI cho bài hát
                    .setMediaId(song.id)  // Đặt mediaId là id của bài hát
                    .build()
            }

            // Thêm danh sách MediaItem vào ExoPlayer
            exoPlayer.setMediaItems(mediaItems)
            exoPlayer.prepare()
            val currentMediaItem = exoPlayer.currentMediaItem

            // Nếu MediaItem đang phát không null, so sánh với currentList
            if (currentMediaItem != null) {
                val currentMediaId = currentMediaItem.mediaId

                // Sử dụng map hoặc for-loop để so sánh với currentList
                currentList.forEach { song ->
                    if (song.id == currentMediaId) {
                        // Đây là bài hát hiện đang phát
                        startForegroundService(song)
                        sendCurrentSong(song)
                    }
                }
            }
        }

//        val musicUri = intent?.getParcelableExtra("EXTRA_MUSIC_URI",Song::class.java)
//
//        song = musicUri
//        startForegroundService(song)
//        if (musicUri != null) {
//            // Tạo MediaItem với URI của bài hát
//            val mediaItem = MediaItem.fromUri(musicUri.link)
//            exoPlayer.setMediaItem(mediaItem)
//            exoPlayer.prepare()
//        }
        // Sử dụng Handler để đảm bảo chạy trên main thread
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            when (intent?.action) {
                "ACTION_PLAY", ACTION_PLAY -> {
                    exoPlayer.play()
                    updateNotification(song)
                    startUpdatingPosition()
                }
                "ACTION_PAUSE", ACTION_PAUSE -> {
                    exoPlayer.pause()
                    updateNotification(song)
                }
                "ACTION_SEEK" -> {
                    val position = intent.getLongExtra("EXTRA_POSITION", 0L)
                    exoPlayer.seekTo(position)
                }
                "ACTION_RESUME" -> resumePlayback()
                "ACTION_NEXT", ACTION_NEXT ->{
                    nextSong()
                    updateNotification(song)
                }
                "ACTION_PREV", ACTION_PREVIOUS ->{
                    prevSong()
                    updateNotification(song)
                }
                "ACTION_SHUFFLED" ->{
                    val isShuffled = intent.getBooleanExtra("IS_SHUFFLED",false)
                    shuffledSong(isShuffled)
                }
                "ACTION_REPEATED" ->{
                    val repeatMode = intent.getIntExtra("REPEAT_MODE",0)
                    repeatSong(repeatMode)
                }
            }
        }

        return START_NOT_STICKY
    }
    private fun updatePlaybackState(isPlaying: Boolean, currentPosition: Long, duration: Long) {
        val state = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(state, currentPosition, 1.0f) // currentPosition là vị trí hiện tại của bài hát
            .setBufferedPosition(duration) // duration là tổng thời lượng bài hát
            .build()

        mediaSession.setPlaybackState(playbackState)
    }

    override fun onDestroy() {
        exoPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    @OptIn(UnstableApi::class)
    private fun startForegroundService(song:Song?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = PermissionChecker.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (permission != PermissionChecker.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "Dont have")
                return
            }
        }

        // Create MediaSessionCompat
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            isActive = true
        }

        // Create notification actions (Previous, Play/Pause, Next)
        val playPauseAction = if (exoPlayer.isPlaying) {
            NotificationCompat.Action(
                R.drawable.baseline_pause_24, "Pause",
                getPendingIntentForAction(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.play_circle_24, "Play",
                getPendingIntentForAction(ACTION_PLAY)
            )
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song?.name)
            .setContentText(song?.authorName)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // Keep the notification ongoing
            .addAction(NotificationCompat.Action(R.drawable.baseline_skip_previous_24, "Previous", getPendingIntentForAction(ACTION_PREVIOUS)))
            .addAction(playPauseAction)
            .addAction(NotificationCompat.Action(R.drawable.baseline_skip_next_24, "Next", getPendingIntentForAction(ACTION_NEXT)))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(1) // Only show play/pause in compact view
            )
            .build()

        // Start the foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }
    // Helper to create PendingIntent for notification actions
    private fun getPendingIntentForAction(action: String): PendingIntent {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
    fun nextSong(){
        when (exoPlayer.repeatMode) {
            Player.REPEAT_MODE_OFF -> {
                // Không lặp lại, nếu đến cuối danh sách thì không chuyển bài
                if (exoPlayer.currentMediaItemIndex < exoPlayer.mediaItemCount - 1) {
                    exoPlayer.seekToNextMediaItem()
                }
            }
            Player.REPEAT_MODE_ONE -> {
                // Lặp lại bài hát hiện tại
                exoPlayer.seekTo(exoPlayer.currentMediaItemIndex, 0)
            }
            Player.REPEAT_MODE_ALL -> {
                // Lặp lại danh sách bài hát, chuyển sang bài tiếp theo
                exoPlayer.seekToNextMediaItem()
                // Nếu đã đến cuối danh sách, quay về bài đầu tiên
                if (exoPlayer.currentMediaItemIndex == 0) {
                    exoPlayer.seekTo(0)
                }
            }
        }
    }
    fun prevSong(){
        when (exoPlayer.repeatMode) {
            Player.REPEAT_MODE_OFF -> {
                // Không lặp lại, nếu đã là bài đầu tiên thì không chuyển bài
                if (exoPlayer.currentMediaItemIndex > 0) {
                    exoPlayer.seekToPreviousMediaItem()
                }
            }
            Player.REPEAT_MODE_ONE -> {
                // Lặp lại bài hát hiện tại
                exoPlayer.seekTo(exoPlayer.currentMediaItemIndex, 0)
            }
            Player.REPEAT_MODE_ALL -> {
                // Lặp lại danh sách bài hát, chuyển về bài trước đó
                if (exoPlayer.currentMediaItemIndex > 0) {
                    exoPlayer.seekToPreviousMediaItem()
                } else {
                    // Nếu đã ở bài đầu tiên, quay về bài cuối cùng
                    exoPlayer.seekTo((exoPlayer.mediaItemCount - 1),0)
                }
            }
        }
    }
    fun shuffledSong(boolean: Boolean){
        exoPlayer.shuffleModeEnabled = boolean
    }
    fun repeatSong(repeatMode: Int) {
        when (repeatMode) {
            0 -> {
                // Không lặp
                exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
            }
            1 -> {
                // Lặp lại bài hát hiện tại
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
            }
            2 -> {
                // Lặp lại danh sách bài hát
                exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
            }
        }
    }
}
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACTION_PLAY" -> {
                // Gọi hàm phát nhạc từ service
                val playIntent = Intent(context, MusicService::class.java).setAction("ACTION_PLAY")
                context.startService(playIntent)
            }
            "ACTION_PAUSE" -> {
                val pauseIntent = Intent(context, MusicService::class.java).setAction("ACTION_PAUSE")
                context.startService(pauseIntent)
            }
            "ACTION_NEXT" -> {
                val nextIntent = Intent(context, MusicService::class.java).setAction("ACTION_NEXT")
                context.startService(nextIntent)
            }
            "ACTION_PREV" -> {
                val prevIntent = Intent(context, MusicService::class.java).setAction("ACTION_PREV")
                context.startService(prevIntent)
            }
        }
    }
}