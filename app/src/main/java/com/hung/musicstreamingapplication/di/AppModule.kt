package com.hung.musicstreamingapplication.di

import android.content.Context
import android.content.SharedPreferences
import androidx.media3.exoplayer.ExoPlayer
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hung.musicstreamingapplication.data.remote.FirebaseAuthService
import com.hung.musicstreamingapplication.data.remote.LoginAPI
import com.hung.musicstreamingapplication.data.remote.YoutubeAPI
import com.hung.musicstreamingapplication.data.repository.AuthorRepositoryImpl
import com.hung.musicstreamingapplication.data.repository.CommentRepositoryImpl
import com.hung.musicstreamingapplication.data.repository.LoginRepositoryImpl
import com.hung.musicstreamingapplication.data.repository.PlaylistRepositoryImpl
import com.hung.musicstreamingapplication.data.repository.SignUpRepositoryImpl
import com.hung.musicstreamingapplication.data.repository.YoutubeRepositoryImpl
import com.hung.musicstreamingapplication.domain.repository.LoginRepository
import com.hung.musicstreamingapplication.domain.repository.SignUpRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLoginAPI(): LoginAPI{
        return Retrofit.Builder()
            .baseUrl("https://test.com")
            .build()
            .create(LoginAPI::class.java)

    }

    @Provides
    @Singleton
    fun provideLoginRepository(
        firestore: FirebaseFirestore,
        auth : FirebaseAuth
    ): LoginRepository{
        return LoginRepositoryImpl(firestore,auth)
    }

    @Provides
    @Singleton
    fun provideSignUpRepository(
        db: FirebaseFirestore,
        fb: FirebaseAuth,
        fbs: FirebaseAuthService
    ): SignUpRepository{
        return SignUpRepositoryImpl(db,fb,fbs)
    }

    @Provides
    @Singleton
    fun provideYoutubeRepository(
        retrofit: YoutubeAPI
    ):YoutubeRepositoryImpl{
        return YoutubeRepositoryImpl(retrofit)
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(
        firestore: FirebaseFirestore
    ):PlaylistRepositoryImpl{
        return PlaylistRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAuthorRepository(
        firestore: FirebaseFirestore
    ): AuthorRepositoryImpl {
        return AuthorRepositoryImpl(firestore)
    }
    @Provides
    @Singleton
    fun provideCommentRepository(
        firestore: FirebaseFirestore
    ): CommentRepositoryImpl {
        return CommentRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            "my_shared_prefs",
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer{
        return ExoPlayer.Builder(context).build()
    }
}