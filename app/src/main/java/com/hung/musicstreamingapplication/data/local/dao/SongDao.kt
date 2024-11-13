package com.hung.musicstreamingapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hung.musicstreamingapplication.data.model.Song

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: Song)

    @Query("SELECT * FROM song")
    suspend fun getAllDownloadedSong(): List<Song>

    @Query("SELECT * FROM song where id = :songID")
    suspend fun getSongByID(songID: String) : Song

    @Query("SELECT * FROM song WHERE LOWER(name) LIKE LOWER('%' || :songName || '%')")
    suspend fun getSongByName(songName: String): List<Song>
}
