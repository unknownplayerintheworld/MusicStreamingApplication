package com.hung.musicstreamingapplication.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.hung.musicstreamingapplication.data.local.dao.SongDao
import com.hung.musicstreamingapplication.data.model.Album
import com.hung.musicstreamingapplication.data.model.Author
import com.hung.musicstreamingapplication.data.model.Playlist
import com.hung.musicstreamingapplication.data.model.Song
import com.hung.musicstreamingapplication.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Locale
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val songDao: SongDao,
    private val storage: FirebaseStorage,
    private val contentResolver: ContentResolver,
    private val sharePref: SharedPreferences
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
                    playcount = document.getDouble("playcount")?.toInt() ?: 0,
                    lyrics = document.getString("lyrics") ?: ""
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
                .whereEqualTo("type","song")
                .orderBy("created_at", Query.Direction.DESCENDING) // Sắp xếp theo thời gian tạo
                .limit(12) // Giới hạn số lượng bản ghi lấy ra
                .get()
                .await()

            val songIDs = snapshot.documents.mapNotNull { it.getString("itemID") }

            val songs = mutableListOf<Song>()
            for (songId in songIDs) {
                // Lấy từng bài hát từ collection "song"
                val songSnapshot = firestore.collection("song")
                    .document(songId)
                    .get().await()
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
                    song.id = songSnapshot.id
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
    override suspend fun recommendBestPlaylists(userID: String): List<Playlist> {
        return try {
            Log.d("RecommendBestPlaylists", "Starting recommendation for user: $userID")

            // Fetch user's listening history and determine the most popular genre
            val historySnapshot = firestore.collection("history")
                .whereEqualTo("userID", userID)
                .whereEqualTo("type", "song")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(50) // Get the 50 most recent songs in history
                .get()
                .await()

            Log.d("RecommendBestPlaylists", "History snapshot size: ${historySnapshot.size()}")

            // If history is empty, return 4 random playlists
            if (historySnapshot.isEmpty) {
                Log.d("RecommendBestPlaylists", "No history found, returning random playlists.")

                // Fetch all playlists
                val allPlaylistsSnapshot = firestore.collection("playlist").get().await()
                val allPlaylists = allPlaylistsSnapshot.documents.mapNotNull { document ->
                    document.toObject(Playlist::class.java)?.copy(id = document.id)
                }

                // Shuffle the playlists to randomize the selection
                return allPlaylists.shuffled().take(4)
            }

            val genreCount = mutableMapOf<String, Int>()

            // Count occurrences of each genre in the listening history
            for (historyDoc in historySnapshot.documents) {
                val songID = historyDoc.getString("itemID")
                Log.d("RecommendBestPlaylists", "Processing history doc: ${historyDoc.id}, songID: $songID")

                if (!songID.isNullOrEmpty()) {
                    val songSnapshot = firestore.collection("song").document(songID).get().await()
                    val song = songSnapshot.toObject(Song::class.java)

                    Log.d("RecommendBestPlaylists", "Retrieved song for ID $songID: $song")

                    song?.genreIDs?.forEach { genre ->
                        genreCount[genre] = genreCount.getOrDefault(genre, 0) + 1
                    }
                }
            }

            Log.d("RecommendBestPlaylists", "Final genre count: $genreCount")

            // Determine the most popular genre
            val popularGenre = genreCount.maxByOrNull { it.value }?.key ?: return emptyList()

            // Fetch all playlists
            val playlistSnapshot = firestore.collection("playlist").get().await()
            val playlistScores = mutableListOf<Pair<Playlist, Double>>()

            // Iterate through all playlists and check for songs that match the popular genre
            for (document in playlistSnapshot.documents) {
                val playlist = document.toObject(Playlist::class.java)?.copy(id = document.id)

                // Check if the playlist contains songs
                if (playlist != null && playlist.songIDs.isNotEmpty()) {
                    val songSnapshot = firestore.collection("song")
                        .whereIn(FieldPath.documentId(), playlist.songIDs)
                        .get()
                        .await()

                    val genreSongs = mutableListOf<Song>()
                    var totalPlayCount = 0

                    // Filter songs that have the popular genre
                    for (songDoc in songSnapshot.documents) {
                        val song = songDoc.toObject(Song::class.java)

                        // Check if the song contains the popular genre
                        if (song != null && song.genreIDs.contains(popularGenre)) {
                            genreSongs.add(song)
                            totalPlayCount += song.playcount
                        }
                    }

                    // Only add playlist if it has at least 1 song in the popular genre
                    if (genreSongs.isNotEmpty()) {
                        val averagePlayCount = totalPlayCount.toDouble() / genreSongs.size
                        playlistScores.add(Pair(playlist, averagePlayCount))
                    }
                }
            }

            // If no playlists match the popular genre, return random playlists
            if (playlistScores.isEmpty()) {
                Log.d("RecommendBestPlaylists", "No matching playlists, returning random playlists.")

                // Fetch all playlists and shuffle to select random ones
                val allPlaylistsSnapshot = firestore.collection("playlist").get().await()
                val allPlaylists = allPlaylistsSnapshot.documents.mapNotNull { document ->
                    document.toObject(Playlist::class.java)?.copy(id = document.id)
                }

                // Shuffle and return 4 random playlists
                return allPlaylists.shuffled().take(4)
            }

            // Sort playlists by average play count and return the top 4
            return playlistScores
                .sortedByDescending { it.second }
                .take(4)
                .map { it.first }

        } catch (e: Exception) {
            Log.e("RecommendBestPlaylists", "Error recommending playlists: ", e)
            emptyList()
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
                .whereEqualTo("status",true)
                .limit(9)
                .get()
                .await()

            // Chuyển đổi dữ liệu snapshot thành danh sách các đối tượng Song và cập nhật ID từ document.id
            val songsList = snapshot.documents.mapNotNull { document ->
                val song = document.toObject(Song::class.java)?.copy(id = document.id)
                song
            } // Loại bỏ những bài hát null nếu có lỗi parsing

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
                .whereArrayContains("authorIDs", currentSong.authorIDs[0])
                .whereEqualTo("status",true)
                .limit(5)
                .get()
                .await()
                .documents
                .mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Song::class.java)?.apply {
                        id = documentSnapshot.id
                    }
                }

            // Lấy 5 bài hát ngẫu nhiên cùng thể loại
            val genreSongs = firestore.collection("song")
                .whereArrayContains("genreIDs", currentSong.genreIDs[0])
                .whereEqualTo("status",true)
                .limit(5)
                .get()
                .await()
                .documents
                .mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Song::class.java)?.apply {
                        id = documentSnapshot.id
                    }
                }

            // Ghép hai danh sách lại và xáo trộn
            val allSongs = (authorSongs + genreSongs + currentSong).shuffled().distinctBy { it.id }.take(10)

            // Lấy tên tác giả cho từng bài hát và gán vào từng bài
            for (song in allSongs) {
                val authorNames = song.authorIDs.mapNotNull { authorID ->
                    val authorSnapshot = firestore.collection("author")
                        .document(authorID)
                        .get()
                        .await()
                    authorSnapshot.getString("name") // Lấy tên tác giả
                }.joinToString(separator = ", ") // Ghép tên tác giả thành chuỗi

                // Gán tên tác giả vào bài hát
                song.authorName = authorNames
            }

            return allSongs

        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    override suspend fun getMostRecentlySong(userID: String): Song? {
        return try {
            // Lấy danh sách các songID từ collection "history", sắp xếp theo trường "created_at"
            val snapshot = firestore.collection("history")
                .whereEqualTo("type","song")
                .whereEqualTo("userID", userID)
                .orderBy("created_at", Query.Direction.DESCENDING) // Sắp xếp theo thời gian tạo (gần đây nhất)
                .limit(1) // Giới hạn chỉ lấy bài hát gần đây nhất
                .get()
                .await()

            val songID = snapshot.documents.firstOrNull()?.getString("itemID")

            songID?.let { id ->
                // Lấy bài hát từ collection "song"
                val songSnapshot = firestore.collection("song").document(id).get().await()
                val song = songSnapshot.toObject(Song::class.java)?.apply {
                    this.id = songSnapshot.id
                }

                if (song != null && song.authorIDs.isNotEmpty()) {
                    val authorNames = mutableListOf<String>()

                    // Lấy tên của từng tác giả từ collection "author"
                    for (authorId in song.authorIDs) {
                        val authorSnapshot = firestore.collection("author").document(authorId).get().await()
                        val authorName = authorSnapshot.getString("name")

                        // Thêm tên của từng tác giả vào danh sách
                        authorName?.let { authorNames.add(it) }
                    }

                    // Nối các tên tác giả thành một chuỗi và gán vào authorName của bài hát
                    song.authorName = authorNames.joinToString(", ")
                }

                return song // Trả về bài hát gần đây nhất

            } ?: run {
                // Nếu không tìm thấy bài hát nào
                Log.d("SongRepository", "No song found in history for user $userID")
                return null
            }

        } catch (e: Exception) {
            Log.e("SongRepository", "Error fetching most recently song: ", e)
            return null
        }
    }

    override suspend fun getSongRecordFromSearching(keyword: String): List<Song>? {
        val trimmedKeyword = keyword.trim()

        // Chuyển keyword thành chữ thường và chữ hoa chữ cái đầu
        val lowercaseKeyword = trimmedKeyword.lowercase()
        val capitalizedKeyword = trimmedKeyword.split(" ").joinToString(" ") { it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        } }

        // Tìm kiếm với từ khóa chữ thường
        val lowercaseSearch = firestore.collection("song")
            .orderBy("name")
            .whereEqualTo("status",true)
            .startAt(lowercaseKeyword)
            .endAt(lowercaseKeyword + '\uf8ff')
            .get()
            .await()

        // Tìm kiếm với từ khóa chữ cái đầu viết hoa
        val capitalizedSearch = firestore.collection("song")
            .orderBy("name")
            .whereEqualTo("status",true)
            .startAt(capitalizedKeyword)
            .endAt(capitalizedKeyword + '\uf8ff')
            .get()
            .await()

        // Lưu trữ các bài hát từ cả hai kết quả
        val songs = mutableListOf<Song>()

        // Thêm kết quả từ lowercaseSearch
        lowercaseSearch.documents.forEach { document ->
            document.toObject(Song::class.java)?.let {
                it.id = document.id
                songs.add(it) }
        }

        // Thêm kết quả từ capitalizedSearch
        capitalizedSearch.documents.forEach { document ->
            document.toObject(Song::class.java)?.let {
                it.id = document.id
                songs.add(it) }
        }

        // Lọc trùng lặp: sử dụng Set hoặc kiểm tra dựa trên ID của bài hát
        val uniqueSongs = songs.distinctBy { it.id }

            // Lấy tên tác giả từ authorIDs và cập nhật vào authorName cho từng bài hát
            for (song in uniqueSongs) {
                // Lấy danh sách authorIDs của bài hát
                val authorIDs = song.authorIDs
                val authorNames = mutableListOf<String>()

                // Duyệt qua từng authorID để lấy tên tác giả
                for (authorID in authorIDs) {
                    val authorSnapshot = firestore.collection("author")
                        .document(authorID)
                        .get()
                        .await()

                    // Lấy trường name của tác giả
                    val authorName = authorSnapshot.getString("name") ?: "Unknown"
                    authorNames.add(authorName)
                }

                // Gán danh sách tên tác giả vào trường authorName
                song.authorName = authorNames.joinToString(", ") // Nối các tên tác giả thành chuỗi
            }

            return uniqueSongs
        }

    override suspend fun getAlbumRecordFromSearching(keyword: String): List<Album>? {
        val trimmedKeyword = keyword.trim()

// Tìm kiếm chính xác
        val exactSearch = firestore.collection("album")
            .whereEqualTo("name", trimmedKeyword)
            .get()
            .await()

// Tìm kiếm mờ
        val fuzzySearch = firestore.collection("album")
            .orderBy("name")
            .startAt(trimmedKeyword)
            .endAt(trimmedKeyword + '\uf8ff')
            .get()
            .await()

        val albums = mutableListOf<Album>()
        val uniqueAlbumIds = mutableSetOf<String>() // Tập hợp để theo dõi id album đã thêm

// Xử lý kết quả tìm kiếm chính xác
        exactSearch.documents.forEach { document ->
            document.toObject(Album::class.java)?.let { album ->
                album.id = document.id // Gán document id cho id của album
                albums.add(album) // Thêm vào danh sách
                uniqueAlbumIds.add(album.id) // Thêm id vào tập hợp
            }
        }

// Xử lý kết quả tìm kiếm mờ
        fuzzySearch.documents.forEach { document ->
            document.toObject(Album::class.java)?.let { album ->
                album.id = document.id // Gán document id cho id của album
                // Kiểm tra xem id album đã tồn tại chưa
                if (!uniqueAlbumIds.contains(album.id)) {
                    albums.add(album) // Chỉ thêm nếu id chưa tồn tại
                    uniqueAlbumIds.add(album.id) // Thêm id vào tập hợp
                }
            }
        }

        return albums

    }

    override suspend fun getAuthorRecordFromSearching(keyword: String): List<Author>? {
        val trimmedKeyword = keyword.trim()

        val exactSearch = firestore.collection("author")
            .whereEqualTo("name", trimmedKeyword)
            .get()
            .await()

        val fuzzySearch = firestore.collection("author")
            .orderBy("name")
            .startAt(trimmedKeyword)
            .endAt(trimmedKeyword + '\uf8ff')
            .get()
            .await()

        val authors = mutableListOf<Author>()
        exactSearch.documents.forEach { document ->
            document.toObject(Author::class.java)?.let { author ->
                author.id = document.id // Gán document id cho id của author
                authors.add(author)
            }
        }
        fuzzySearch.documents.forEach { document ->
            document.toObject(Author::class.java)?.let { author ->
                author.id = document.id // Gán document id cho id của author
                authors.add(author)
            }
        }

        return authors
    }

    override suspend fun getPlaylistRecordFromSearching(keyword: String): List<Playlist>? {
        val trimmedKeyword = keyword.trim()

        val exactSearch = firestore.collection("playlist")
            .whereEqualTo("name", trimmedKeyword)
            .get()
            .await()

        val fuzzySearch = firestore.collection("playlist")
            .orderBy("name")
            .startAt(trimmedKeyword)
            .endAt(trimmedKeyword + '\uf8ff')
            .get()
            .await()

        val playlists = mutableListOf<Playlist>()
        val uniquePlaylistIds = mutableSetOf<String>() // Tập hợp để theo dõi id playlist đã thêm

// Xử lý kết quả tìm kiếm chính xác
        exactSearch.documents.forEach { document ->
            document.toObject(Playlist::class.java)?.let { playlist ->
                playlist.id = document.id // Gán document id cho id của playlist
                playlists.add(playlist) // Thêm vào danh sách
                uniquePlaylistIds.add(playlist.id) // Thêm id vào tập hợp
            }
        }

// Xử lý kết quả tìm kiếm mờ
        fuzzySearch.documents.forEach { document ->
            document.toObject(Playlist::class.java)?.let { playlist ->
                playlist.id = document.id // Gán document id cho id của playlist
                // Kiểm tra xem id playlist đã tồn tại chưa
                if (!uniquePlaylistIds.contains(playlist.id)) {
                    playlists.add(playlist) // Chỉ thêm nếu id chưa tồn tại
                    uniquePlaylistIds.add(playlist.id) // Thêm id vào tập hợp
                }
            }
        }

        return playlists

    }

    override suspend fun getSongFromPlaylist(playlistID: String): List<Song>? {
        // Lấy playlist từ Firestore
        val playlistSnapshot = firestore.collection("playlist")
            .document(playlistID)
            .get()
            .await()

        // Kiểm tra xem playlist có tồn tại không
        if (!playlistSnapshot.exists()) {
            return emptyList() // Trả về danh sách rỗng nếu không tìm thấy playlist
        }

        // Lấy songIDs từ playlist
        val songIDs = playlistSnapshot.get("songIDs") as? List<String> ?: return emptyList()

        // Lấy danh sách bài hát từ songIDs
        val songs = mutableListOf<Song>()
        for (songID in songIDs) {
            val songSnapshot = firestore.collection("song")
                .document(songID)
                .get()
                .await()

            // Chỉ thêm bài hát nếu tồn tại
            if (songSnapshot.exists()) {
                val song = songSnapshot.toObject(Song::class.java)?.apply {
                    id = songSnapshot.id // Gán document ID làm ID của bài hát
                }

                song?.let {
                    // Lấy authorIDs và gán authorName
                    val authorIDs = it.authorIDs
                    val authorNames = mutableListOf<String>()

                    for (authorID in authorIDs) {
                        val authorSnapshot = firestore.collection("author")
                            .document(authorID)
                            .get()
                            .await()

                        // Lấy tên tác giả
                        val authorName = authorSnapshot.getString("name") ?: "Unknown"
                        authorNames.add(authorName)
                    }

                    // Gán danh sách tên tác giả vào trường authorName
                    it.authorName = authorNames.joinToString(", ") // Nối các tên tác giả thành chuỗi
                    songs.add(it) // Thêm bài hát vào danh sách
                }
            }

        }

        return songs
    }

    override suspend fun getSongFromAlbum(albumID: String): List<Song>? {
        // Lấy playlist từ Firestore
        val playlistSnapshot = firestore.collection("album")
            .document(albumID)
            .get()
            .await()

        // Kiểm tra xem playlist có tồn tại không
        if (!playlistSnapshot.exists()) {
            return emptyList() // Trả về danh sách rỗng nếu không tìm thấy playlist
        }

        // Lấy songIDs từ playlist
        val songIDs = playlistSnapshot.get("songIDs") as? List<String> ?: return emptyList()

        // Lấy danh sách bài hát từ songIDs
        val songs = mutableListOf<Song>()
        for (songID in songIDs) {
            val songSnapshot = firestore.collection("song")
                .document(songID)
                .get()
                .await()

            // Chỉ thêm bài hát nếu tồn tại
            if (songSnapshot.exists()) {
                val song = songSnapshot.toObject(Song::class.java)?.apply {
                    id = songSnapshot.id // Gán document ID làm ID của bài hát
                }

                song?.let {
                    // Lấy authorIDs và gán authorName
                    val authorIDs = it.authorIDs
                    val authorNames = mutableListOf<String>()

                    for (authorID in authorIDs) {
                        val authorSnapshot = firestore.collection("author")
                            .document(authorID)
                            .get()
                            .await()

                        // Lấy tên tác giả
                        val authorName = authorSnapshot.getString("name") ?: "Unknown"
                        authorNames.add(authorName)
                    }

                    // Gán danh sách tên tác giả vào trường authorName
                    it.authorName = authorNames.joinToString(", ") // Nối các tên tác giả thành chuỗi
                    songs.add(it) // Thêm bài hát vào danh sách
                }
            }

        }

        return songs
    }

    override suspend fun getHotAuthorSongs(authorID: String): List<Song>? {
        return try {
            val snapshot = firestore.collection("song")
                .whereArrayContains("authorIDs", authorID) // Truy vấn những bài hát có authorID trong mảng
                .limit(5)
                .get()
                .await()

            if (snapshot.isEmpty) {
                emptyList() // Trả về danh sách rỗng nếu không tìm thấy bài hát
            } else {
                snapshot.documents.map { document ->
                    val song = document.toObject(Song::class.java) ?: Song()
                    song.id = document.id // Gán documentId cho id của bài hát

                    // Lấy danh sách tên tác giả từ collection "author" và ghép chuỗi với dấu phẩy
                    val authorNames = song.authorIDs.mapNotNull { authorId ->
                        val authorSnapshot = firestore.collection("author").document(authorId).get().await()
                        if (authorSnapshot.exists()) {
                            authorSnapshot.getString("name")
                        } else {
                            null
                        }
                    }

                    // Ghép các tên tác giả thành một chuỗi với dấu phẩy
                    song.authorName = authorNames.joinToString(", ")

                    song // Trả về đối tượng Song sau khi đã gán id và tên tác giả
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Trả về danh sách rỗng nếu có lỗi xảy ra
        }
    }

    override suspend fun getAlbumRecently(userID: String,count: Int): List<Album>? {
        return try {
            // Lấy danh sách từ collection "history", lọc theo "album" và giới hạn kết quả
            val snapshot = firestore.collection("history")
                .whereEqualTo("userID", userID)
                .whereEqualTo("type", "album") // Lọc chỉ lấy các bản ghi có type là "album"
                .orderBy("created_at", Query.Direction.DESCENDING) // Sắp xếp theo thời gian tạo
                .limit(count.toLong()) // Giới hạn số lượng bản ghi
                .get()
                .await()

            // Lấy danh sách itemID từ snapshot
            val albumIDs = snapshot.documents.mapNotNull { it.getString("itemID") }

            // Truy vấn thông tin album từ collection "album" dựa trên itemID
            val albums = mutableListOf<Album>()
            for (albumId in albumIDs) {
                val albumSnapshot = firestore.collection("album")
                    .document(albumId)
                    .get()
                    .await()
                val album = albumSnapshot.toObject(Album::class.java)

                // Nếu album không null, gán id và thêm vào danh sách albums
                if (album != null) {
                    album.id = albumId
                    albums.add(album)
                }
            }
            albums // Trả về danh sách album

        } catch (e: Exception) {
            Log.e("AlbumRepository", "Error fetching recently albums: ", e)
            emptyList() // Trả về danh sách rỗng nếu có lỗi xảy ra
        }
    }

    override suspend fun getPlaylistRecently(userID: String,count: Int): List<Playlist>? {
        return try {
            // Lấy danh sách từ collection "history", lọc theo "playlist" và giới hạn kết quả
            val snapshot = firestore.collection("history")
                .whereEqualTo("userID", userID)
                .whereEqualTo("type", "playlist") // Lọc chỉ lấy các bản ghi có type là "playlist"
                .orderBy("created_at", Query.Direction.DESCENDING) // Sắp xếp theo thời gian tạo
                .limit(count.toLong()) // Giới hạn số lượng bản ghi
                .get()
                .await()

            // Lấy danh sách itemID từ snapshot
            val playlistIDs = snapshot.documents.mapNotNull { it.getString("itemID") }

            // Truy vấn thông tin playlist từ collection "playlist" dựa trên itemID
            val playlists = mutableListOf<Playlist>()
            for (playlistId in playlistIDs) {
                val playlistSnapshot = firestore.collection("playlist")
                    .document(playlistId)
                    .get()
                    .await()
                val playlist = playlistSnapshot.toObject(Playlist::class.java)

                // Nếu playlist không null, gán id và thêm vào danh sách playlists
                if (playlist != null) {
                    playlist.id = playlistId
                    playlists.add(playlist)
                }
            }
            playlists // Trả về danh sách playlist

        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error fetching recently playlists: ", e)
            emptyList() // Trả về danh sách rỗng nếu có lỗi xảy ra
        }
    }

    override suspend fun getAuthorRecently(userID: String, count: Int): List<Author>? {
        return try {
            // Lấy danh sách từ collection "history", lọc theo "playlist" và giới hạn kết quả
            val snapshot = firestore.collection("history")
                .whereEqualTo("userID", userID)
                .whereEqualTo("type", "author") // Lọc chỉ lấy các bản ghi có type là "playlist"
                .orderBy("created_at", Query.Direction.DESCENDING) // Sắp xếp theo thời gian tạo
                .limit(count.toLong()) // Giới hạn số lượng bản ghi
                .get()
                .await()

            // Lấy danh sách itemID từ snapshot
            val playlistIDs = snapshot.documents.mapNotNull { it.getString("itemID") }

            // Truy vấn thông tin playlist từ collection "playlist" dựa trên itemID
            val playlists = mutableListOf<Author>()
            for (playlistId in playlistIDs) {
                val playlistSnapshot = firestore.collection("author")
                    .document(playlistId)
                    .get()
                    .await()
                val playlist = playlistSnapshot.toObject(Author::class.java)

                // Nếu playlist không null, gán id và thêm vào danh sách playlists
                if (playlist != null) {
                    playlist.id = playlistId
                    playlists.add(playlist)
                }
            }
            playlists // Trả về danh sách playlist

        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error fetching recently songs: ", e)
            emptyList() // Trả về danh sách rỗng nếu có lỗi xảy ra
        }
    }

    override suspend fun getSongRecently(userID: String, count: Int): List<Song>? {
        return try {
            // Lấy danh sách từ collection "history", lọc theo "playlist" và giới hạn kết quả
            val snapshot = firestore.collection("history")
                .whereEqualTo("userID", userID)
                .whereEqualTo("type", "song") // Lọc chỉ lấy các bản ghi có type là "playlist"
                .orderBy("created_at", Query.Direction.DESCENDING) // Sắp xếp theo thời gian tạo
                .limit(count.toLong()) // Giới hạn số lượng bản ghi
                .get()
                .await()

            // Lấy danh sách itemID từ snapshot
            val playlistIDs = snapshot.documents.mapNotNull { it.getString("itemID") }

            // Truy vấn thông tin playlist từ collection "playlist" dựa trên itemID
            val playlists = mutableListOf<Song>()
            for (playlistId in playlistIDs) {
                val playlistSnapshot = firestore.collection("song")
                    .document(playlistId)
                    .get()
                    .await()
                val playlist = playlistSnapshot.toObject(Song::class.java)

                // Nếu playlist không null, gán id và thêm vào danh sách playlists
                if (playlist != null) {
                    playlist.id = playlistId
                    playlists.add(playlist)
                }
            }
            playlists // Trả về danh sách playlist

        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error fetching recently songs: ", e)
            emptyList() // Trả về danh sách rỗng nếu có lỗi xảy ra
        }
    }

    override suspend fun getOnlineSong(): List<Song>? {
        return try{
            val snapshot = firestore.collection("song")
                .whereEqualTo("status",true)
                .limit(30)
                .get()
                .await()
            if(!snapshot.isEmpty){
                val songs = mutableListOf<Song>()
                for(doc in snapshot.documents){
                    val song = doc.toObject(Song::class.java)
                    song?.let{
                        it.id = doc.id
                        songs.add(it)
                    }
                }
                songs
            }
            else{
                emptyList()
            }
        }catch (e: Exception){
            emptyList()
        }
    }

    override suspend fun getOnlineSongByKW(kw: String): List<Song>? {
        return try {
            // Tạo keyword cho khoảng tìm kiếm theo từ khóa đã nhập
            val keyword = kw.trim().lowercase()  // Chuyển về dạng chữ thường, bỏ khoảng trắng
            val endKeyword = keyword + '\uf8ff'  // '\uf8ff' là ký tự sau cùng trong UTF-8

            // Lấy bản ghi trong khoảng từ keyword đến endKeyword (với 'name' là trường tìm kiếm)
            val snapshot = firestore.collection("song")
                .whereGreaterThanOrEqualTo("name", keyword)  // Tìm bản ghi có giá trị >= keyword
                .whereLessThanOrEqualTo("name", endKeyword)  // Tìm bản ghi có giá trị <= endKeyword
                .limit(30)  // Giới hạn 30 bản ghi
                .get()
                .await()

            // Kiểm tra xem snapshot có dữ liệu không
            if (!snapshot.isEmpty) {
                // Tạo danh sách các bài hát
                val songs = mutableListOf<Song>()
                for (document in snapshot.documents) {
                    // Parse dữ liệu document thành đối tượng Song
                    val song = document.toObject(Song::class.java)
                    if (song != null) {
                        song.id = document.id
                        songs.add(song)
                    }
                }
                songs
            } else {
                // Nếu không có dữ liệu
                emptyList()
            }
        } catch (e: Exception) {
            // In lỗi nếu có ngoại lệ
            e.printStackTrace()
            null
        }
    }

    override suspend fun getRecentlySongByKw(kw: String, count: Int, userID: String): List<Song>? {
        return try {
            val trimmedKeyword = kw.trim()

            // Chuyển keyword thành chữ thường và chữ hoa chữ cái đầu
            val lowercaseKeyword = trimmedKeyword.lowercase()
            val capitalizedKeyword = trimmedKeyword.split(" ").joinToString(" ") { it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            } }

            // Lấy danh sách bài hát gần đây từ collection "history" dựa trên userID và type là "song"
            val snapshot = firestore.collection("history")
                .whereEqualTo("userID", userID)
                .whereEqualTo("type", "song")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(count.toLong())
                .get()
                .await()

            // Lấy danh sách itemID (tức là songID) từ snapshot
            val songIDs = snapshot.documents.mapNotNull { it.getString("itemID") }

            // Lưu trữ các bài hát từ cả hai kết quả tìm kiếm
            val songs = mutableListOf<Song>()

            // Tìm kiếm với từ khóa chữ thường
            val lowercaseSearch = firestore.collection("song")
                .whereIn(FieldPath.documentId(), songIDs) // Chỉ lấy những songID đã tìm thấy
                .whereEqualTo("status", true)
                .orderBy("name")
                .startAt(lowercaseKeyword)
                .endAt(lowercaseKeyword + '\uf8ff')
                .get()
                .await()

            // Tìm kiếm với từ khóa chữ cái đầu viết hoa
            val capitalizedSearch = firestore.collection("song")
                .whereIn(FieldPath.documentId(), songIDs)
                .whereEqualTo("status", true)
                .orderBy("name")
                .startAt(capitalizedKeyword)
                .endAt(capitalizedKeyword + '\uf8ff')
                .get()
                .await()

            // Thêm kết quả từ lowercaseSearch
            lowercaseSearch.documents.forEach { document ->
                document.toObject(Song::class.java)?.let {
                    it.id = document.id
                    songs.add(it) }
            }

            // Thêm kết quả từ capitalizedSearch
            capitalizedSearch.documents.forEach { document ->
                document.toObject(Song::class.java)?.let {
                    it.id = document.id
                    songs.add(it) }
            }

            // Lọc trùng lặp dựa trên ID của bài hát
            val uniqueSongs = songs.distinctBy { it.id }

            // Lấy tên tác giả từ authorIDs và cập nhật vào authorName cho từng bài hát
            for (song in uniqueSongs) {
                val authorIDs = song.authorIDs
                val authorNames = mutableListOf<String>()

                for (authorID in authorIDs) {
                    val authorSnapshot = firestore.collection("author")
                        .document(authorID)
                        .get()
                        .await()

                    val authorName = authorSnapshot.getString("name") ?: "Unknown"
                    authorNames.add(authorName)
                }

                song.authorName = authorNames.joinToString(", ")
            }

            uniqueSongs
        } catch (e: Exception) {
            Log.e("SongRepository", "Error fetching recently songs by keyword: ", e)
            emptyList()
        }
    }

    override suspend fun addSongToPlaylist(playlistID: String,songID: String): Int {
        return try {
            // Tham chiếu tới document của playlist dựa trên playlistID
            val playlistRef = firestore.collection("playlist")
                .document(playlistID)

            // Truy vấn để lấy document của playlist
            val playlistSnapshot = playlistRef.get().await()

            // Nếu playlist tồn tại
            if (playlistSnapshot.exists()) {
                // Lấy danh sách songIDs hiện tại trong playlist
                val currentSongIDs = playlistSnapshot.get("songIDs") as? ArrayList<String> ?: arrayListOf()

                // Kiểm tra nếu songID đã tồn tại trong playlist chưa
                if (!currentSongIDs.contains(songID)) {
                    // Thêm songID mới vào mảng songIDs
                    currentSongIDs.add(songID)

                    // Cập nhật lại mảng songIDs trong Firestore
                    playlistRef.update("songIDs", currentSongIDs).await()

                    // Trả về true nếu thêm thành công
                    1
                } else {
                    // Nếu bài hát đã tồn tại trong playlist, không cần thêm
                    0
                }
            } else {
                // Nếu playlist không tồn tại
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Trả về false nếu có lỗi xảy ra
            0
        }
    }

    override suspend fun removeSongFromPlaylist(playlist: Playlist, song: Song): Int {
        return try {
            // Lấy document của playlist dựa trên playlistID
            val playlistRef = firestore.collection("playlist").document(playlist.id)

            // Lấy dữ liệu playlist từ Firestore
            val playlistSnapshot = playlistRef.get().await()
            if (playlistSnapshot.exists()) {
                // Lấy danh sách songIDs từ document
                val songIDs = playlistSnapshot.get("songIDs") as? MutableList<String>

                if (songIDs != null && songIDs.contains(song.id)) {
                    // Xóa songID khỏi danh sách
                    songIDs.remove(song.id)

                    // Cập nhật lại document với danh sách songIDs đã xóa
                    playlistRef.update("songIDs", songIDs).await()

                    1 // Trả về true nếu thành công
                } else {
                    0 // Nếu songID không tồn tại trong playlist, trả về false
                }
            } else {
                0 // Nếu playlist không tồn tại, trả về false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0 // Nếu có lỗi, trả về false
        }
    }

    override suspend fun savePlayCount(userID: String, songID: String): Boolean {
        return try {
            val currentTime = System.currentTimeMillis()
            val editor = sharePref.edit()

            val key = "${userID}_${songID}"
            editor.putString("${key}_${userID}", userID)
            editor.putString("${key}_${songID}", songID)
            editor.putLong("${key}_lastPlayedTime", currentTime)

            editor.apply()
            // Cập nhật lượt chơi nhạc trong Firestore
            val songRef = firestore.collection("song").document(songID)

            // Tăng các lượt chơi
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(songRef)

                // Lấy các giá trị hiện tại
                val playCount = snapshot.getLong("playcount") ?: 0
                val playInMonth = snapshot.getLong("play_in_month") ?: 0
                val playInWeek = snapshot.getLong("play_in_week") ?: 0

                // Tăng các giá trị lên 1
                transaction.update(songRef, "playcount", playCount + 1)
                transaction.update(songRef, "play_in_month", playInMonth + 1)
                transaction.update(songRef, "play_in_week", playInWeek + 1)
            }
            true
        }
        catch (e:Exception){
            Log.e("SAVE_PC",e.message.toString())
            false
        }
    }

    override suspend fun getLastPlayedTime(userID: String, songID: String): Long {
        val key = "${userID}_${songID}"
        return sharePref.getLong("${key}_lastPlayedTime",0)
    }

    override suspend fun upsertHistory(userID: String, itemID: String, type: String): Boolean {
        return try {
            Log.d("UPSERT","calling!")
            // Tìm kiếm bản ghi có userID và itemID
            val querySnapshot = firestore.collection("history")
                .whereEqualTo("itemID", itemID)
                .whereEqualTo("userID", userID)
                .get()
                .await()

            // Kiểm tra xem bản ghi đã tồn tại chưa
            if (!querySnapshot.isEmpty) {
                // Nếu đã tồn tại, cập nhật trường created_at
                val document = querySnapshot.documents.first()
                firestore.collection("history").document(document.id)
                    .update("created_at", Timestamp.now())
                    .await()
                true
            } else {
                // Nếu chưa tồn tại, thêm bản ghi mới
                val historyData = hashMapOf(
                    "created_at" to Timestamp.now(), // Lưu thời gian hiện tại
                    "itemID" to itemID,
                    "type" to type,
                    "userID" to userID
                )

                // Chèn dữ liệu vào collection "history"
                firestore.collection("history")
                    .add(historyData)
                    .await() // Chờ cho đến khi dữ liệu được chèn thành công
                true
            }

        // Trả về true nếu thành công
        } catch (e: Exception) {
            Log.e("UPSERT_HISTORY", "Error adding/updating song in history: ${e.message}")
            false // Trả về false nếu có lỗi xảy ra
        }
    }

    override suspend fun downloadSong(songID: String): Boolean {
        return try {
            // Step 1: Get song info from Firestore
            val document = firestore.collection("song").document(songID).get().await()
            if (!document.exists()) return false

            val name = document.getString("name") ?: return false
            val authorIds = document.get("authorIDs") as? List<String> ?: return false // Lấy danh sách authorIds
            val downloadUrl = document.getString("link") ?: return false

            // Step 2: Create a local temporary file where the song will be downloaded
            val localFile = File.createTempFile("downloaded_song_$songID", ".mp3")

            // Step 3: Get a reference to the file from the download URL
            val storageRef = storage.getReferenceFromUrl(downloadUrl)

            // Step 4: Download the file and await the result
            storageRef.getFile(localFile).await()

            // Step 5: Fetch author names from Firestore using authorIDs
            val authorNames = mutableListOf<String>()
            for (authorId in authorIds) {
                val authorDoc = firestore.collection("author").document(authorId).get().await()
                val authorName = authorDoc.getString("name") // Assume "name" field stores author name
                if (authorName != null) {
                    authorNames.add(authorName)
                }
            }
            // Convert the list of author names to a string (you can format it however you like)
            val authorNamesString = authorNames.joinToString(", ")

            // Step 6: Save the song file to MediaStore
            val savedUri = saveToMediaStore(localFile, name)
                ?: return false // Failed to save to MediaStore

            // Step 7: Save the song info to Room with the same id from Firestore
            val song = Song(
                id = songID,
                name = name,
                authorIDs = authorIds,
                authorName = authorNamesString, // Store author names in the Song entity
                link = savedUri.toString()
            )
            songDao.insert(song)

            true // Successfully downloaded and saved the song
        } catch (e: Exception) {
            // Handle any exceptions here (e.g., log errors)
            false
        }
    }


    override suspend fun saveToMediaStore(file: File, title: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, "$title.mp3")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC) // Đường dẫn trong MediaStore
            put(MediaStore.Audio.Media.IS_PENDING, 1) // Cờ đánh dấu file đang được xử lý
        }
        val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        // Step 1: Insert into MediaStore
        val uri = contentResolver.insert(audioCollection, contentValues) ?: return null

        try {
            // Step 2: Mở output stream để ghi file vào MediaStore
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Step 3: Xác nhận file đã hoàn tất
            contentValues.clear()
            contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0) // Bỏ cờ "pending"
            contentResolver.update(uri, contentValues, null, null)

            return uri // Trả về URI của file trong MediaStore
        } catch (e: Exception) {
            // Nếu có lỗi xảy ra thì xóa file khỏi MediaStore
            contentResolver.delete(uri, null, null)
            return null
        }
    }

    override suspend fun getAllDownloadSongs(isAsc : Boolean): List<Song> {
        val songs = songDao.getAllDownloadedSong()
        return if(isAsc){
            songs.sortedBy {
                it.name.lowercase()
            }
        }else{
            songs.sortedByDescending {
                it.name.lowercase()
            }
        }
    }

    override suspend fun searchSongByName(name: String): List<Song>? {
        return songDao.getSongByName(name)
    }

    override suspend fun getFavouriteSongs(userID: String): List<Song> {
        return try {
            val snapshot = firestore.collection("favourite")
                .whereEqualTo("userID", userID)
                .whereEqualTo("type","song")
                .get()
                .await()
            snapshot.documents.forEach { doc ->
                Log.d("FV_SONG_REPO", "Document data: ${doc.data}") // In ra toàn bộ dữ liệu
            }

            if (snapshot.documents.isNotEmpty()) {
                val songs = mutableListOf<Song>()

                snapshot.documents.forEach { doc ->
                    val itemID = doc.get("itemID")

                    Log.d("FV_SONG_REPO", "itemID: $itemID")
                        val songsnap = firestore.collection("song")
                            .whereEqualTo(FieldPath.documentId(), itemID)
                            .get()
                            .await()

                        Log.d("FV_SONG_REPO", "song snapshot size: ${songsnap.documents.size}")

                        songsnap.documents.forEach { songdoc ->
                            val song = songdoc.toObject(Song::class.java)
                            song?.let {
                                it.id = songdoc.id
                                val authorIDs = it.authorIDs ?: emptyList<String>()
                                val authorNames = mutableListOf<String>()

                                Log.d("FV_SONG_REPO", "authorIDs: $authorIDs")

                                // Lặp qua từng authorID và truy vấn tên
                                for (authorID in authorIDs) {
                                    val authorSnap = firestore.collection("author")
                                        .document(authorID)
                                        .get()
                                        .await()

                                    val authorName = authorSnap.getString("name")
                                    Log.d("FV_SONG_REPO", "author name: $authorName")

                                    authorName?.let {
                                        authorNames.add(it)
                                    }
                                }

                                it.authorName = authorNames.joinToString(",")
                                songs.add(it)
                            }
                        }

                }
                songs
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.d("FV_SONG_REPO", "Error: ${e.message}")
            emptyList() // Trả về danh sách rỗng nếu có lỗi
        }
    }



    override suspend fun addSongToFavourite(userID: String, songID: String): Boolean {
        try {
            val favData = hashMapOf(
                "userID" to userID,
                "itemID" to songID,
                "type" to "song",
                "created_at" to Timestamp.now()
            )
            firestore.collection("favourite").add(favData).await()
            return true
        }catch (e: Exception){
            Log.d("SONG_FAV_REPO",e.message.toString())
            return false
        }
    }

    override suspend fun delSongFromFavourite(userID: String, songID: String): Boolean {
        return try{
            val snapShot = firestore.collection("favourite")
                .whereEqualTo("userID",userID)
                .whereEqualTo("itemID",songID)
                .get()
                .await()
            for (doc in snapShot.documents)
            {
                firestore.collection("favourite").document(doc.id).delete().await()
            }
            Log.d("DEL_FAV_SONG_REPO","Xóa thành công")
            true
        }catch (e: Exception){
            Log.d("DEL_FAV_SONG_REPO",e.message.toString())
            false
        }
    }
}