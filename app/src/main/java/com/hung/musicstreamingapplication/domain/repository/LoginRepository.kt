package com.hung.musicstreamingapplication.domain.repository
import com.hung.musicstreamingapplication.data.model.User
import com.hung.musicstreamingapplication.presentation.sign_in.UserData

interface LoginRepository {
    suspend fun login()
    suspend fun logout(): Boolean
    suspend fun CreateUserFromOAuth2(userData: UserData,onSuccess:(String) -> Unit,onFailure:(String) ->Unit)
    suspend fun GetUserFromDB(userID: String): User
    suspend fun loginState() : Boolean
    suspend fun loginWithEmailorUsername(identifier: String,password: String): Boolean
    fun getCurrentUserID() : String?
}