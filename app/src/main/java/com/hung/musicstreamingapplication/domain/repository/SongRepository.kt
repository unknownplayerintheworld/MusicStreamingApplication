package com.hung.musicstreamingapplication.domain.repository

import android.net.Uri
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Author
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.data.model.Song
import java.io.File

interface SongRepository {
    suspend fun getRandomSong() : List<Song>
    suspend fun getRecentlySong(userID: String) : List<Song>
    suspend fun recommendBestPlaylists(userID: String): List<Playlist>
    suspend fun getHotAlbum(): List<Album>
    suspend fun getTrending(): List<Song>
    suspend fun getRelatedSong(userID: String): List<Song>
    suspend fun getMostRecentlySong(userID: String) : Song?
    suspend fun getSongRecordFromSearching(keyword: String): List<Song>?
    suspend fun getPlaylistRecordFromSearching(keyword: String): List<Playlist>?
    suspend fun getAlbumRecordFromSearching(keyword: String): List<Album>?
    suspend fun getAuthorRecordFromSearching(keyword: String): List<Author>?
    suspend fun getSongFromPlaylist(playlistID: String): List<Song>?
    suspend fun getSongFromAlbum(albumID: String): List<Song>?
    suspend fun getHotAuthorSongs(authorID: String): List<Song>?
    suspend fun getPlaylistRecently(userID: String,count: Int): List<Playlist>?
    suspend fun getAlbumRecently(userID: String,count: Int) : List<Album>?
    suspend fun getSongRecently(userID:String,count: Int): List<Song>?
    suspend fun getAuthorRecently(userID: String,count: Int): List<Author>?
    suspend fun getRecentlySongByKw(kw: String,count: Int,userID : String): List<Song>?
    suspend fun getOnlineSong() : List<Song>?
    suspend fun getOnlineSongByKW(kw: String): List<Song>?
    suspend fun addSongToPlaylist(playlistID: String,songID: String): Int
    suspend fun removeSongFromPlaylist(playlist: Playlist,song: Song): Int
    suspend fun savePlayCount(userID: String,songID : String) : Boolean
    suspend fun getLastPlayedTime(userID: String,songID: String): Long
    suspend fun upsertHistory(userID: String,itemID: String,type: String):Boolean
    suspend fun downloadSong(songID: String): Boolean
    suspend fun saveToMediaStore(file: File,title: String): Uri?
    suspend fun getAllDownloadSongs(isAsc : Boolean): List<Song>?
    suspend fun searchSongByName(name: String): List<Song>?
    suspend fun addSongToFavourite(userID: String,songID: String): Boolean
    suspend fun delSongFromFavourite(userID: String,songID: String): Boolean
    suspend fun getFavouriteSongs(userID: String): List<Song>?
}