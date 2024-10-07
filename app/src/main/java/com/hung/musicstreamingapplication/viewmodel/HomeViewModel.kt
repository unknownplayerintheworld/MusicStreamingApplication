package com.hung.musicstreamingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.data.repository.SongRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songRepository : SongRepositoryImpl
) :ViewModel(){
    private val _song = MutableStateFlow<List<Song>>(emptyList())
    val song = _song.asStateFlow()

    private val _recentlySong = MutableStateFlow<List<Song>>(emptyList())
    val recentlySong = _recentlySong.asStateFlow()

    private val _recommendSong = MutableStateFlow(Playlist())
    val recommendSong = _recommendSong.asStateFlow()

    private val _trending = MutableStateFlow<List<Song>>(emptyList())
    val trending = _trending.asStateFlow()

    private val _hotalbum = MutableStateFlow<List<Album>>(emptyList())
    val hotalbum = _hotalbum.asStateFlow()

    fun randomSongLoading(){
        viewModelScope.launch {
            val fetchedSongs = songRepository.getRandomSong()
            _song.value = fetchedSongs
        }
    }
    fun getRecentlySong(userID : String){
        viewModelScope.launch {
            val songs = songRepository.getRecentlySong(userID)
            _recentlySong.value = songs
        }
    }
    fun getRecommendSongs(userID : String){
        viewModelScope.launch {
            val songs = songRepository.recommendBestPlaylist(userID)
            if (songs != null) {
                _recommendSong.value = songs
            }
        }
    }
    fun getTrending(){
        viewModelScope.launch {
            val song = songRepository.getTrending()
            _trending.value = song
        }
    }
    fun getHotAlbum(){
        viewModelScope.launch {
            val song = songRepository.getHotAlbum()
            _hotalbum.value = song
        }
    }
}