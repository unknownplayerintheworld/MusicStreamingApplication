package com.hung.musicstreamingapplication.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FIrestoreLocalModule {
    @Provides
    @Singleton
    fun provideFirebaseFirestoreLocal(): FirebaseFirestore {
        return Firebase.firestore
    }
}