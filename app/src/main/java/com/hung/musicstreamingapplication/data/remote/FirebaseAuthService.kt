package com.hung.musicstreamingapplication.data.remote

import com.hung.musicstreamingapplication.BuildConfig
import com.hung.musicstreamingapplication.presentation.verify.EmailVerificationRequest
import com.hung.musicstreamingapplication.presentation.verify.EmailVerificationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FirebaseAuthService {
    @POST("accounts:sendOobCode?key=${BuildConfig.API_KEY}")
    fun sendEmailVerification(@Body request: EmailVerificationRequest): Call<EmailVerificationResponse>
}