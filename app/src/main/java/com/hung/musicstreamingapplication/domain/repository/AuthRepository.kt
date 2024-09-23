package com.hung.musicstreamingapplication.domain.repository

import android.content.Context

interface AuthRepository {
    fun saveIdToken(context: Context, idToken: String)
    fun getIdToken(context: Context): String?
    fun clearIdToken(context: Context)
}