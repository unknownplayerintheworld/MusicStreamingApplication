package com.hung.musicstreamingapplication.domain.repository

import com.hung.musicstreamingapplication.presentation.sign_in.UserData

interface LoginRepository {
    suspend fun login()
    suspend fun CreateUserFromOAuth2(userData: UserData,onSuccess:(String) -> Unit,onFailure:(String) ->Unit)
    suspend fun GetUserFromDB()
    suspend fun loginState() : Boolean
    suspend fun loginWithEmailorUsername(identifier: String,password: String): Boolean
    fun getCurrentUserID() : String?
}