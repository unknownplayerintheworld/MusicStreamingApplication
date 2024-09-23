package com.hung.musicstreamingapplication

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltAndroidApp
open class MusicApplication: Application() {
    private lateinit var broadcastrcv: BroadcastReceiver
    private val _playbackStateFlow = MutableStateFlow<String>("")
    val playbackStateFlow: StateFlow<String> = _playbackStateFlow.asStateFlow()
    val currentPosition = MutableStateFlow(0L)
    val currentPos: StateFlow<Long> = currentPosition.asStateFlow()


    override fun onCreate() {
        super.onCreate()

        broadcastrcv = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    "ACTION_PLAYBACK_STARTED" -> _playbackStateFlow.value = "Started"
                    "ACTION_PLAYBACK_PLAYING" -> _playbackStateFlow.value = "Playing"
                    "ACTION_PLAYBACK_PAUSE" -> _playbackStateFlow.value = "Paused"
                    "ACTION_PLAYBACK_COMPLETED" -> _playbackStateFlow.value = "Completed"
                }
            }
        }

        val intentFilter = IntentFilter().apply {
            addAction("ACTION_PLAYBACK_PLAYING")
            addAction("ACTION_PLAYBACK_STARTED")
            addAction("ACTION_PLAYBACK_PAUSE")
            addAction("ACTION_PLAYBACK_COMPLETED")
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastrcv, intentFilter)
    }

    override fun onTerminate() {
        super.onTerminate()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastrcv)
    }
}