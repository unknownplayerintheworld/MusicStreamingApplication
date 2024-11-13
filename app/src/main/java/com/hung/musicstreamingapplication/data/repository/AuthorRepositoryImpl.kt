package com.hung.musicstreamingapplication.data.repository

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Author
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.domain.repository.AuthorRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthorRepositoryImpl @Inject constructor(
  private val firestore: FirebaseFirestore
) :AuthorRepository{
    @OptIn(UnstableApi::class)
    override suspend fun getAlbumsAuthor(authorID: String): List<Album>? {
        val albumsSnapshot = firestore.collection("album")
            .whereArrayContains("authorIDs", authorID) // Tìm album mà nghệ sĩ tham gia (dùng mảng authorIDs)
            .get()
            .await()

        Log.d("FirestoreQuery", "Số lượng album tìm thấy: ${albumsSnapshot.size()}")

// Chuyển đổi các document thành danh sách album
        return albumsSnapshot.documents.mapNotNull { document ->
            document.toObject(Album::class.java)?.apply {
                id = document.id // Gán ID bằng document ID
                Log.d("AlbumData", "Album ID: $id, Album Name: $name")
            }
        }
    }

    @OptIn(UnstableApi::class)
    override suspend fun getPlaylistsAuthor(authorID: String): List<Playlist>? {
        // Lấy tất cả các playlist
        val playlistsSnapshot = firestore.collection("playlist").get().await()

        Log.d("FirestoreQuery", "Số lượng playlist tìm thấy: ${playlistsSnapshot.size()}")

        // Danh sách để chứa playlist hợp lệ
        val validPlaylists = mutableListOf<Playlist>()

        for (playlistDoc in playlistsSnapshot.documents) {
            val playlist = playlistDoc.toObject(Playlist::class.java)?.apply {
                id = playlistDoc.id
            }
            playlist?.let {
                if(!it.songIDs.isNullOrEmpty()) {
                    Log.d("PlaylistData", "Kiểm tra playlist với ID: ${playlistDoc.id}")
                    Log.d("AUTHOR_REPO", it.songIDs.toString())
                    // Kiểm tra từng songID trong playlist
                    val songsSnapshot = firestore.collection("song")
                        .whereIn(FieldPath.documentId(), it.songIDs) // Lấy các bài hát theo songIDs
                        .get()
                        .await()

                    Log.d(
                        "PlaylistData",
                        "Số lượng bài hát trong playlist: ${songsSnapshot.size()}"
                    )

                    // Kiểm tra xem tất cả các bài hát trong playlist có authorID đúng hay không
                    val allSongsBelongToAuthor = songsSnapshot.documents.all { songDoc ->
                        val song = songDoc.toObject(Song::class.java)
                        val belongsToAuthor =
                            song?.authorIDs?.all { authorId -> authorId == authorID } == true
                        Log.d(
                            "SongCheck",
                            "Bài hát ID: ${songDoc.id}, Tất cả thuộc về authorID: $belongsToAuthor"
                        )
                        belongsToAuthor
                    }

                    if (allSongsBelongToAuthor) {
                        // Nếu tất cả bài hát đều thuộc về authorID, thêm playlist vào danh sách hợp lệ
                        validPlaylists.add(it.apply { id = playlistDoc.id }) // Gán id cho playlist
                        Log.d("ValidPlaylist", "Playlist hợp lệ với ID: ${playlistDoc.id}")
                    } else {
                        Log.d("InvalidPlaylist", "Playlist với ID: ${playlistDoc.id} không hợp lệ")
                    }
                }
            }
        }

        Log.d("Result", "Số lượng playlist hợp lệ: ${validPlaylists.size}")
        return validPlaylists
    }

    override suspend fun getTopRelatedAuthors(artistId: String): List<Author>? {
        // Lấy tất cả các bài hát của nghệ sĩ hiện tại
        val songsSnapshot = firestore.collection("song")
            .whereArrayContains("authorIDs", artistId) // Tìm bài hát của nghệ sĩ hiện tại
            .get()
            .await()

        // Tạo một bản đồ để đếm tần suất xuất hiện của các nghệ sĩ
        val authorFrequencyMap = mutableMapOf<String, Int>()

        for (songDoc in songsSnapshot.documents) {
            val song = songDoc.toObject(Song::class.java)
            song?.authorIDs?.forEach { authorID ->
                if (authorID != artistId) { // Loại bỏ nghệ sĩ hiện tại
                    authorFrequencyMap[authorID] = authorFrequencyMap.getOrDefault(authorID, 0) + 1
                }
            }
        }

        // Lấy top 3 nghệ sĩ dựa trên tần suất
        val topAuthors = authorFrequencyMap.entries.sortedByDescending { it.value }
            .take(3)
            .map { it.key }

        if(topAuthors.isEmpty()){
            return emptyList()
        }
        // Lấy thông tin nghệ sĩ từ ID
        return firestore.collection("author")
            .whereIn(FieldPath.documentId(), topAuthors)
            .get()
            .await()
            .documents.mapNotNull {
               val author = it.toObject(Author::class.java)
                author?.apply {
                    id = it.id
                }
                }
    }

    @OptIn(UnstableApi::class)
    override suspend fun getAuthorFav(userID: String): List<Author>? {
        return try{
            val snapshot = firestore.collection("favourite")
                .whereEqualTo("userID",userID)
                .whereEqualTo("type","author")
                .get().await()
            if(!snapshot.isEmpty){
                val authors = mutableListOf<Author>()
                snapshot.documents.forEach {
                    it ->
                    val itemID = it.get("itemID")
                    val authorsnap = firestore.collection("author")
                        .whereEqualTo(FieldPath.documentId(),itemID)
                        .get()
                        .await()
                    authorsnap.documents.forEach{
                        authordoc ->
                        val author = authordoc.toObject(Author::class.java)
                        author?.let {
                            authors.add(it)
                        }
                    }
                }
                authors
            }
            else{
                emptyList()
            }
        }catch (e: Exception){
            Log.d("AUTHOR_REPO",e.message.toString())
            emptyList()
        }
    }
}