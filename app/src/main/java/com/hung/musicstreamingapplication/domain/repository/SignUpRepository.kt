package com.hung.musicstreamingapplication.domain.repository

import com.hung.musicstreamingapplication.presentation.verify.EmailVerificationRequest
import com.hung.musicstreamingapplication.presentation.verify.EmailVerificationResponse

interface SignUpRepository{
    suspend fun signUp(email : String,username: String,password:String):Boolean
    suspend fun isEmailExists(email: String) : Boolean
    suspend fun isUserExists(username: String) : Boolean
    suspend fun sendEmailVerify(request: EmailVerificationRequest):Result<EmailVerificationResponse>
}