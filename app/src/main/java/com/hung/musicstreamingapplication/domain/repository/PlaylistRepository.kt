package com.hung.musicstreamingapplication.domain.repository

import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Playlist

interface PlaylistRepository {
    suspend fun addPlaylistToFavourite(userID: String,playlistID: String): Boolean
    suspend fun delPlaylistFromFavourite(userID: String,playlistID : String) : Boolean
    suspend fun getFavouriteStatus(userID: String,playlistID: String): Boolean
    suspend fun addAlbumToFavourite(userID: String,albumID: String): Boolean
    suspend fun delAlbumFromFavourite(userID: String,albumID: String): Boolean
    suspend fun getFavouriteStatusAlbum(userID : String,albumID: String) : Boolean
    suspend fun getFavouritePlaylists(userID: String): List<Playlist>?
    suspend fun getFavouriteAlbums(userID: String) : List<Album>?
    suspend fun getUserCreatedPlaylist(playlistID: String,userID: String): Boolean
    suspend fun addNewPlaylist(playlist: Playlist):Boolean
}