package com.hung.musicstreamingapplication.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.domain.repository.PlaylistRepository
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) :PlaylistRepository{
    override suspend fun addPlaylistToFavourite(userID: String, playlistID: String):Boolean {
        try{
            val favData = hashMapOf(
                "userID" to userID,
                "itemID" to playlistID,
                "type" to "playlist",
                "created_at" to FieldValue.serverTimestamp()
            )
            firestore.collection("favourite")
                .add(favData)
                .await()
            Log.d("FavouriteRepository", "Playlist added to favourites successfully.")
            return true
        }catch (e:Exception){
            Log.e("FavouriteRepository", "Error adding playlist to favourites: ", e)
            return false
        }
    }


    override suspend fun delPlaylistFromFavourite(userID: String, playlistID: String) : Boolean{
        return try{
            val snapshot = firestore.collection("favourite")
                .whereEqualTo("userID",userID)
                .whereEqualTo("itemID",playlistID)
                .get()
                .await()
            for (doc in snapshot.documents){
                firestore.collection("favourite").document(doc.id).delete().await()
            }
            Log.d("FavouriteRepository", "Playlist removed from favourites successfully.")
            true
        } catch (e: Exception) {
            Log.e("FavouriteRepository", "Error removing playlist from favourites: ", e)
            false
        }
    }

    override suspend fun getFavouriteStatus(userID: String, playlistID: String): Boolean {
        return try{
            val snapshot = firestore.collection("favourite")
                .whereEqualTo("userID",userID)
                .whereEqualTo("itemID",playlistID)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e:Exception){
            Log.e("FavouriteRepository", "Error fetching fav list: ", e)
            false
        }
    }

    override suspend fun addAlbumToFavourite(userID: String, albumID: String): Boolean {
        try{
            val favData = hashMapOf(
                "userID" to userID,
                "itemID" to albumID,
                "type" to "album",
                "created_at" to FieldValue.serverTimestamp()
            )
            firestore.collection("favourite")
                .add(favData)
                .await()
            Log.d("FavouriteRepository", "Album added to favourites successfully.")
            return true
        }catch (e:Exception){
            Log.e("FavouriteRepository", "Error adding album to favourites: ", e)
            return false
        }
    }

    override suspend fun delAlbumFromFavourite(userID: String, albumID: String): Boolean {
        return try{
            val snapshot = firestore.collection("favourite")
                .whereEqualTo("userID",userID)
                .whereEqualTo("itemID",albumID)
                .get()
                .await()
            for (doc in snapshot.documents){
                firestore.collection("favourite").document(doc.id).delete().await()
            }
            Log.d("FavouriteRepository", "Album removed from favourites successfully.")
            true
        } catch (e: Exception) {
            Log.e("FavouriteRepository", "Error removing album from favourites: ", e)
            false
        }
    }

    override suspend fun getFavouriteStatusAlbum(userID: String, albumID: String): Boolean {
        return try{
            val snapshot = firestore.collection("favourite")
                .whereEqualTo("userID",userID)
                .whereEqualTo("itemID",albumID)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e:Exception){
            Log.e("FavouriteRepository", "Error fetching fav list: ", e)
            false
        }
    }

    override suspend fun getFavouriteAlbums(userID: String): List<Album>? {
        return try{
            val snapshot = firestore.collection("favourite")
                .whereEqualTo("userID",userID)
                .whereEqualTo("type","album")
                .get()
                .await()
            val albumIDs = snapshot.documents.mapNotNull {
                it.getString("itemID")
            }
            val albumList = mutableListOf<Album>()
            for(albumID in albumIDs){
                val albumSnapshot = firestore.collection("album")
                    .document(albumID)
                    .get()
                    .await()
                val album = albumSnapshot.toObject(Album::class.java)
                if(album!=null){
                    album.id = albumID
                    albumList.add(album)
                }
            }
            albumList
        }catch (e: Exception){
            Log.e("PlaylistRepository","Error fetching fav album list...")
            emptyList()
        }
    }

    override suspend fun getFavouritePlaylists(userID: String): List<Playlist>? {
        return try{
            val snapshot = firestore.collection("favourite")
                .whereEqualTo("userID",userID)
                .whereEqualTo("type","playlist")
                .get()
                .await()
            val albumIDs = snapshot.documents.mapNotNull {
                it.getString("itemID")
            }
            val albumList = mutableListOf<Playlist>()
            for(albumID in albumIDs){
                val albumSnapshot = firestore.collection("playlist")
                    .document(albumID)
                    .get()
                    .await()
                val album = albumSnapshot.toObject(Playlist::class.java)
                if(album!=null){
                    album.id = albumID
                    albumList.add(album)
                }
            }
            albumList
        }catch (e: Exception){
            Log.e("PlaylistRepository","Error fetching fav album list...")
            emptyList()
        }
    }

    override suspend fun getUserCreatedPlaylist(playlistID: String, userID: String): Boolean {
        return try{
            val snapshot = firestore.collection("playlist")
                .whereEqualTo("userID",userID)
                .whereEqualTo(FieldPath.documentId(),playlistID)
                .get()
                .await()
            !snapshot.isEmpty
        }catch (e: Exception){
            false
        }
    }

    override suspend fun addNewPlaylist(playlist: Playlist): Boolean {
        return try {
            // Tạo document mới trong collection "playlists"
            val newPlaylistRef = firestore.collection("playlist")
                .document()  // Tạo ID tự động cho playlist

            // Tạo HashMap từ đối tượng Playlist
            val playlistData = hashMapOf(
                "userID" to playlist.userID,
                "name" to playlist.name,
                "description" to "",
                "imageUrl" to "",
                "songIDs" to ArrayList<String>(),
                "created_at" to Timestamp.now()
            )

            // Thêm dữ liệu vào document mới
            newPlaylistRef.set(playlistData).await()

            // Trả về true nếu thêm thành công
            true
        } catch (e: Exception) {
            // In lỗi nếu có ngoại lệ
            e.printStackTrace()
            false
        }
    }
}