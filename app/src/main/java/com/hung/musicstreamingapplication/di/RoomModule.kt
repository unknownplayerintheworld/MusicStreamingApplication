package com.hung.musicstreamingapplication.di

import androidx.room.Room
import com.hung.musicstreamingapplication.data.local.dao.SongDao
import com.hung.musicstreamingapplication.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun providesRoomDatabase(
        @ApplicationContext context: android.content.Context
    ): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "song_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase): SongDao{
        return appDatabase.songDao()
    }
}