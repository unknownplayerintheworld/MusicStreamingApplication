package com.hung.musicstreamingapplication.domain.repository

import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.data.model.Song

interface SongRepository {
    suspend fun getRandomSong() : List<Song>
    suspend fun getRecentlySong(userID: String) : List<Song>
    suspend fun recommendBestPlaylist(userID: String): Playlist?
    suspend fun getHotAlbum(): List<Album>
    suspend fun getTrending(): List<Song>
    suspend fun getRelatedSong(userID: String): List<Song>
}