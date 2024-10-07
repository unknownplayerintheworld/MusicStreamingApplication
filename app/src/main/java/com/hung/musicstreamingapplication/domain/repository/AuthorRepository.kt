package com.hung.musicstreamingapplication.domain.repository

import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Author
import com.hung.musicstreamingapplication.data.model.Playlist

interface AuthorRepository {
    suspend fun getAlbumsAuthor(authorID: String): List<Album>?
    suspend fun getPlaylistsAuthor(authorID: String): List<Playlist>?
    suspend fun getTopRelatedAuthors(artistId: String): List<Author>?
}