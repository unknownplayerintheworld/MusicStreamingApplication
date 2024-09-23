package com.hung.musicstreamingapplication.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hung.musicstreamingapplication.MusicApplication
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.data.repository.SongRepositoryImpl
import com.hung.musicstreamingapplication.service.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MusicViewModel @Inject constructor(
    application: Application,
    private val songRepository: SongRepositoryImpl
) :AndroidViewModel(application = application){

    private val _currentSong = MutableStateFlow<Song>(Song())
    val currentSong = _currentSong.asStateFlow()

    private val _currentListSong = MutableStateFlow<MutableList<Song>>(mutableListOf())
    val currentListSong = _currentListSong.asStateFlow()

    private val playbackStateFlow: StateFlow<String> by lazy {
        (application as MusicApplication).playbackStateFlow
    }

    val playbackState: StateFlow<String> = playbackStateFlow


    private val _currentPos = MutableStateFlow(0f)
    val currentPos = _currentPos.asStateFlow()

    private val _currentState = MutableStateFlow(0)
    val currentState = _currentState.asStateFlow()


    private val _isNotificationPermissionGranted = MutableStateFlow<Boolean>(false)
    val isNotificationPermissionGranted: StateFlow<Boolean> = _isNotificationPermissionGranted.asStateFlow()

    // Trạng thái yêu cầu quyền
    private val _requestPermissionEvent = MutableStateFlow(false)
    val requestPermissionEvent: StateFlow<Boolean> = _requestPermissionEvent

    private val currentPosition: StateFlow<Long> by lazy {
        (application as MusicApplication).currentPos
    }

    private fun startAutoUpdatePosition(newDuration: Long){
        _currentPos.value = newDuration.toFloat()
    }
    fun updatePermission(isGranted: Boolean){
        _isNotificationPermissionGranted.value = isGranted
    }

    fun requestPermission() {
        _requestPermissionEvent.value = true
    }


    init{
        viewModelScope.launch {
            playbackStateFlow.collect {
                if (it.contains("Start")) {
                    _currentState.value = 0
                } else if (it.contains("Play")) {
                    _currentState.value = 1
                } else if (it.contains("Pause")) {
                    _currentState.value = 2
                } else if(it.contains("Comp")){
                    _currentState.value = 3
                }
            }
        }
        viewModelScope.launch {
            currentPosition.collect{
                startAutoUpdatePosition(it)
                Log.d("CurrentPos:",it.toString())
            }
        }
    }

    // Hàm để bắt đầu dịch vụ phát nhạc
    fun startMusicService(song: Song) {
        viewModelScope.launch {
            // Lấy danh sách bài hát liên quan
            val relatedSongs = songRepository.getRelatedSong(songID = song.id).toMutableList()

            // Thêm bài hát hiện tại vào đỉnh của danh sách
            relatedSongs.add(0, song)  // Thêm bài hát hiện tại vào đầu danh sách

            // Cập nhật _currentListSong với danh sách mới
            _currentListSong.value = relatedSongs
            Log.d("CURRENT_LIST_SONG","Danh sách:${relatedSongs}")

            // Gửi danh sách sang MusicService
            val context = getApplication<Application>().applicationContext
            val intent = Intent(context, MusicService::class.java).apply {
                action = "ACTION_PLAY"
                putExtra("EXTRA_MUSIC_URI", song)
                // Bạn có thể truyền danh sách bài hát nếu cần
                putParcelableArrayListExtra("EXTRA_SONG_LIST", ArrayList(relatedSongs))
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

    fun pauseMusic(){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_PAUSE"
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun resumeMusic(){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_RESUME"
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun seekTo(position: Long){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_SEEK"
            putExtra("EXTRA_POSITION",position)
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun nextSong(){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_NEXT"
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun prevSong(){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_PREV"
        }
        ContextCompat.startForegroundService(context,intent)
    }
    fun shuffledSong(isShuffled : Boolean){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_SHUFFLED"
            putExtra("IS_SHUFFLED",isShuffled)
        }
        ContextCompat.startForegroundService(context, intent)
    }
    fun repeatSong(repeatMode: Int){
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context,MusicService::class.java).apply {
            action = "ACTION_REPEATED"
            putExtra("REPEAT_MODE",repeatMode)
        }
        ContextCompat.startForegroundService(context, intent)
    }
}