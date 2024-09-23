package com.hung.musicstreamingapplication.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.hung.musicstreamingapplication.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val sharedPref : SharedPreferences
) :AuthRepository{
    override fun saveIdToken(context: Context, idToken: String) {
        sharedPref.edit().putString("idtoken",idToken).apply()
    }

    override fun getIdToken(context: Context): String? {
        return sharedPref.getString("idtoken",null)
    }

    override fun clearIdToken(context: Context) {
        sharedPref.edit().remove("idtoken").apply()
    }
}