package com.hung.musicstreamingapplication.di

import com.hung.musicstreamingapplication.data.remote.FirebaseAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private const val BASE_URL = "https://identitytoolkit.googleapis.com/v1/"
    @Provides
    @Singleton
    fun retrofitInstance() : Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    fun provideFirebaseAuthService(retrofit: Retrofit): FirebaseAuthService {
        return retrofit.create(FirebaseAuthService::class.java)
    }
}