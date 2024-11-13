package com.hung.musicstreamingapplication.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hung.musicstreamingapplication.data.local.dao.SongDao
import com.hung.musicstreamingapplication.data.model.Converters
import com.hung.musicstreamingapplication.data.model.Song

@Database(entities = [Song::class],version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

}