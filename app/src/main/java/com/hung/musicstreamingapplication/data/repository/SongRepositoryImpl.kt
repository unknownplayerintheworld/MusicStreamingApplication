package com.hung.musicstreamingapplication.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.domain.repository.SongRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SongRepository {
    override suspend fun getRandomSong(): List<Song> {
        return try {
            val snapshot = firestore.collection("song")
                .whereEqualTo("status", true)
                .limit(5)
                .get().await()

            var songs = snapshot.documents.map { document ->
                // Lấy danh sách authorIDs từ bài hát
                val authorIDs = document.get("authorIDs") as? List<String> ?: emptyList()

                // Lấy tên của tất cả các tác giả từ collection author dựa trên authorIDs
                val authorNames = authorIDs.mapNotNull { authorID ->
                    val authorSnapshot = firestore.collection("author").document(authorID).get().await()
                    authorSnapshot.getString("name")
                }.joinToString(", ") // Nối tên tác giả bằng dấu phẩy

                Song(
                    id = document.id,
                    authorIDs = authorIDs,
                    authorName = authorNames, // Thêm chuỗi authorName vào
                    createdAt = document.getTimestamp("created_at") ?: Timestamp.now(),
                    duration = document.getDouble("duration")?.toFloat() ?: 0.0f,
                    genreIDs = document.get("genreIDs") as? List<String> ?: emptyList(),
                    imageUrl = document.getString("imageUrl") ?: "",
                    link = document.getString("link") ?: "",
                    name = document.getString("name") ?: "",
                    status = document.getBoolean("status") ?: false,
                    playcount = document.getDouble("playcount")?.toInt() ?: 0
                )
            }

            // Log danh sách bài hát
            songs.forEach { song ->
                Log.d("SongRepository", "Song: $song")
            }

            songs = songs.shuffled().take(5) // Trộn và lấy ngẫu nhiên 5 bài hát
            songs
        } catch (e: Exception) {
            Log.e("SongRepository", "Error fetching random songs: ", e)
            emptyList()
        }
    }


    override suspend fun getRecentlySong(userID: String): List<Song> {
        return try {
            // Lấy danh sách các songID từ collection "history", sắp xếp theo trường "created_at"
            val snapshot = firestore.collection("history")
                .whereEqualTo("userID", userID)
                .orderBy("created_at", Query.Direction.DESCENDING) // Sắp xếp theo thời gian tạo
                .limit(8) // Giới hạn số lượng bản ghi lấy ra
                .get()
                .await()

            val songIDs = snapshot.documents.mapNotNull { it.getString("songID") }

            val songs = mutableListOf<Song>()
            for (songId in songIDs) {
                // Lấy từng bài hát từ collection "song"
                val songSnapshot = firestore.collection("song").document(songId).get().await()
                val song = songSnapshot.toObject(Song::class.java)

                if (song != null && song.authorIDs.isNotEmpty()) {
                    val authorNames = mutableListOf<String>()

                    // Lấy tên của từng tác giả từ collection "author"
                    for (authorId in song.authorIDs) {
                        val authorSnapshot = firestore.collection("author").document(authorId).get().await()
                        val authorName = authorSnapshot.getString("name")

                        // Thêm tên của từng tác giả vào danh sách
                        authorName?.let { authorNames.add(it) }
                    }

                    // Nối các tên tác giả thành một chuỗi và gán vào authorNames của bài hát
                    song.authorName = authorNames.joinToString(", ")
                    Log.d("SongRepository", "Song Image: ${song.imageUrl}")
                    songs.add(song) // Thêm bài hát vào danh sách
                }
            }
            songs

        } catch (e: Exception) {
            Log.e("SongRepository", "Error fetching recently songs: ", e)
            emptyList()
        }
    }
    override suspend fun recommendBestPlaylist(userID: String): Playlist? {
        return try {
            Log.d("RecommendBestPlaylist", "Starting recommendation for user: $userID")

            // Lấy lịch sử nghe của người dùng và thống kê thể loại phổ biến nhất
            val historySnapshot = firestore.collection("history")
                .whereEqualTo("userID", userID)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(50) // Lấy 50 bài gần nhất trong lịch sử nghe
                .get()
                .await()

            Log.d("RecommendBestPlaylist", "History snapshot size: ${historySnapshot.size()}")

            val genreCount = mutableMapOf<String, Int>()

            // Đếm số lần xuất hiện của từng thể loại trong lịch sử nghe
            for (historyDoc in historySnapshot.documents) {
                val songID = historyDoc.getString("songID")
                Log.d("RecommendBestPlaylist", "Processing history doc: ${historyDoc.id}, songID: $songID")

                if (!songID.isNullOrEmpty()) {
                    val songSnapshot = firestore.collection("song").document(songID).get().await()
                    val song = songSnapshot.toObject(Song::class.java)

                    Log.d("RecommendBestPlaylist", "Retrieved song for ID $songID: $song")

                    song?.genreIDs?.forEach { genre ->
                        genreCount[genre] = genreCount.getOrDefault(genre, 0) + 1
                        Log.d("RecommendBestPlaylist", "Incremented genre count for $genre: ${genreCount[genre]}")
                    }
                }
            }

            Log.d("RecommendBestPlaylist", "Final genre count: $genreCount")

            // Xác định thể loại phổ biến nhất
            val popularGenre = genreCount.maxByOrNull { it.value }?.key ?: return null
            Log.d("RecommendBestPlaylist", "Most popular genre: $popularGenre")

            // Lấy tất cả các playlist
            val playlistSnapshot = firestore.collection("playlist").get().await()
            Log.d("RecommendBestPlaylist", "Total playlists retrieved: ${playlistSnapshot.size()}")

            var bestPlaylist: Playlist? = null
            var highestAveragePlayCount = 0.0

            // Duyệt qua tất cả các playlist
            for (document in playlistSnapshot.documents) {
                Log.d("RecommendBestPlaylist", "Document ID: ${document.id}")
                val playlist = document.toObject(Playlist::class.java)?.copy(id = document.id) // Copy and set the id manually
                Log.d("RecommendBestPlaylist", "Playlist ID: ${document.id}, Playlist data: $playlist")

                // Kiểm tra nếu playlist chứa bài hát
                if (playlist != null && playlist.songIDs.isNotEmpty()) {
                    Log.d("RecommendBestPlaylist", "Playlist has ${playlist.songIDs.size} songs")

                    // Lấy danh sách bài hát trong playlist
                    val songSnapshot = firestore.collection("song")
                        .whereIn(FieldPath.documentId(), playlist.songIDs)
                        .get()
                        .await()

                    val genreSongs = mutableListOf<Song>()
                    var totalPlayCount = 0

                    // Lọc bài hát có genreIDs chứa thể loại phổ biến nhất và tính tổng playCount
                    for (songDoc in songSnapshot.documents) {
                        val song = songDoc.toObject(Song::class.java)
                        Log.d("RecommendBestPlaylist", "Song in playlist: ${song?.name}, Genre: ${song?.genreIDs}")

                        // Kiểm tra nếu bài hát có chứa thể loại phổ biến nhất trong danh sách genreIDs
                        if (song != null && song.genreIDs.contains(popularGenre)) {
                            genreSongs.add(song)
                            totalPlayCount += song.playcount ?: 0
                            Log.d("RecommendBestPlaylist", "Added song: ${song.name}, Total play count: $totalPlayCount")
                        }
                    }

                    // In ra các giá trị để kiểm tra
                    Log.d("RecommendBestPlaylist", "Playlist ID: ${playlist.id}, Genre Songs Count: ${genreSongs.size}")

                    // Kiểm tra nếu playlist có ít nhất 7 bài thuộc thể loại phổ biến
                    if (genreSongs.size >= 1) {
                        val averagePlayCount = totalPlayCount.toDouble() / genreSongs.size
                        Log.d("RecommendBestPlaylist", "Average play count for playlist: $averagePlayCount")

                        // Nếu lượt nghe trung bình lớn hơn hiện tại, chọn playlist này
                        if (averagePlayCount > highestAveragePlayCount) {
                            highestAveragePlayCount = averagePlayCount
                            bestPlaylist = playlist
                            Log.d("RecommendBestPlaylist", "Updated best playlist: ${playlist.id}, Highest average play count: $highestAveragePlayCount")
                        }
                    }
                }
            }

            Log.d("RecommendBestPlaylist", "Best playlist selected: ${bestPlaylist?.id}, Highest average play count: $highestAveragePlayCount")
            bestPlaylist // Trả về playlist có lượt nghe trung bình cao nhất

        } catch (e: Exception) {
            Log.e("RecommendBestPlaylist", "Error recommending playlist: ", e)
            null
        }
    }

    override suspend fun getHotAlbum(): List<Album> {
        val albumCollection = firestore.collection("album")  // Collection for albums
        val songCollection = firestore.collection("song")  // Collection for songs
        return try {
            // Step 1: Fetch all albums
            Log.d("HotAlbum", "Fetching all albums from Firestore...")
            val albumSnapshot = albumCollection.get().await()
            val albumList = albumSnapshot.documents.map { doc ->
                // Step 2: Map each document to an Album object and assign document ID as album.id
                val album = doc.toObject(Album::class.java)
                album?.copy(id = doc.id) ?: Album()  // Safely handle null Album objects
            }
            Log.d("HotAlbum", "Fetched ${albumList.size} albums.")

            // Step 3: For each album, fetch songs and calculate average play_in_month
            val albumPlayCounts = albumList.map { album ->
                Log.d("HotAlbum", "Processing album: ${album.name}, ID: ${album.id}")

                // Step 4: Fetch songs by album's songIDs
                Log.d("HotAlbum", "Fetching songs for album ID: ${album.id} with song IDs: ${album.songIDs}")
                val songsSnapshot = songCollection.whereIn(FieldPath.documentId(), album.songIDs).get().await()
                val songs = songsSnapshot.toObjects(Song::class.java)
                Log.d("HotAlbum", "Fetched ${songs.size} songs for album ID: ${album.id}")

                // Step 5: Calculate the average play_in_month for each album
                val averagePlayInMonth = if (songs.isNotEmpty()) {
                    val totalPlayInMonth = songs.sumOf { it.play_in_month }
                    Log.d("HotAlbum", "Total play_in_month for album ID: ${album.id} is $totalPlayInMonth")
                    totalPlayInMonth / songs.size.toDouble()
                } else {
                    Log.d("HotAlbum", "No songs found for album ID: ${album.id}")
                    0.0
                }

                // Log the calculated average
                Log.d("HotAlbum", "Average play_in_month for album ID: ${album.id} is $averagePlayInMonth")

                // Return the album along with its average play_in_month
                Pair(album, averagePlayInMonth)
            }

            // Step 6: Sort albums by average play_in_month in descending order and return the top 4
            Log.d("HotAlbum", "Sorting albums by average play_in_month...")
            val sortedAlbums = albumPlayCounts.sortedByDescending { it.second }
                .take(4)
                .map { it.first }  // Only return the album objects

            Log.d("HotAlbum", "Top 4 albums selected: ${sortedAlbums.map { it.name }}")
            sortedAlbums

        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching top albums", e)
            emptyList()
        }
    }



    override suspend fun getTrending(): List<Song> {
        val songsCollection = firestore.collection("song")  // Tên collection của bạn
        val authorsCollection = firestore.collection("author")  // Tên collection chứa thông tin author
        return try {
            // Lấy 9 bài hát có playcount cao nhất
            val snapshot = songsCollection.orderBy("play_in_week", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(9)
                .get()
                .await()

            // Chuyển đổi dữ liệu snapshot thành danh sách các đối tượng Song và cập nhật ID từ document.id
            val songsList = snapshot.documents.map { document ->
                val song = document.toObject(Song::class.java)?.copy(id = document.id)
                song
            }.filterNotNull() // Loại bỏ những bài hát null nếu có lỗi parsing

            // Duyệt qua từng bài hát để lấy tên của các author từ mảng authorIDs
            val updatedSongsList = songsList.map { song ->
                val authorNames = song.authorIDs.map { authorID ->
                    val authorSnapshot = authorsCollection.document(authorID).get().await()
                    // Lấy tên tác giả từ document author
                    authorSnapshot.getString("name") ?: "Unknown Author" // Trả về "Unknown Author" nếu không có tên
                }
                // Gán tên tác giả vào trường authorName của bài hát
                song.copy(authorName = authorNames.joinToString(", ")) // Gộp các tên tác giả thành chuỗi
            }

            // Log thông tin từng bài hát với tên tác giả
            updatedSongsList.forEach { song ->
                Log.d("SongInfo", "ID: ${song.id}, Title: ${song.name}, Authors: ${song.authorName}, PlayCount: ${song.playcount}")
            }

            updatedSongsList
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching songs", e)
            emptyList()
        }
    }

    override suspend fun getRelatedSong(songID: String): List<Song> {
        return try {
            // Lấy thông tin bài hát hiện tại
            val currentSongSnapshot = firestore.collection("song")
                .document(songID)
                .get()
                .await()

            val currentSong = currentSongSnapshot.toObject(Song::class.java)?.apply {
                id = currentSongSnapshot.id  // Gán documentID làm id cho bài hát
            } ?: return emptyList()

            // Lấy 5 bài hát ngẫu nhiên cùng nghệ sĩ
            val authorSongs = firestore.collection("song")
                .whereArrayContains("authorIDs", currentSong.authorIDs[0]) // Lấy theo nghệ sĩ đầu tiên trong danh sách
                .limit(5)
                .get()
                .await()
                .documents
                .mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Song::class.java)?.apply {
                        id = documentSnapshot.id // Gán documentID làm id cho bài hát
                    }
                }

            // Lấy 5 bài hát ngẫu nhiên cùng thể loại
            val genreSongs = firestore.collection("song")
                .whereArrayContains("genreIDs", currentSong.genreIDs[0]) // Lấy theo thể loại đầu tiên trong danh sách
                .limit(5)
                .get()
                .await()
                .documents
                .mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Song::class.java)?.apply {
                        id = documentSnapshot.id // Gán documentID làm id cho bài hát
                    }
                }

            // Ghép danh sách bài hát từ tác giả và thể loại, xáo trộn và loại bỏ trùng lặp dựa trên tên bài hát
            val relatedSongs = (authorSongs + genreSongs)
                .shuffled()
                .distinctBy { song -> song.id }
                .take(10)

            relatedSongs

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


}
