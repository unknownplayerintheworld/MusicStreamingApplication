package com.hung.musicstreamingapplication.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.hung.musicstreamingapplication.data.remote.FirebaseAuthService
import com.hung.musicstreamingapplication.domain.repository.SignUpRepository
import com.hung.musicstreamingapplication.presentation.verify.EmailVerificationRequest
import com.hung.musicstreamingapplication.presentation.verify.EmailVerificationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SignUpRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val fb: FirebaseAuth,
    private val fbs: FirebaseAuthService
) : SignUpRepository{

    override suspend fun signUp(email: String, username: String, password: String): Boolean {
        val email_trim = email.trim()
        try {
            return suspendCoroutine { continuation ->
                fb.createUserWithEmailAndPassword(email_trim, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = fb.currentUser
                            if (user != null) {
                                user.sendEmailVerification()
                                    .addOnCompleteListener { verificationTask ->
                                        if (verificationTask.isSuccessful) {
                                            Log.d(TAG, "Verification email sent to ${user.email}")

                                            // Tạo một coroutine scope để chờ xác minh email
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    // Kiểm tra xem người dùng đã xác minh email chưa
                                                    while (!user.isEmailVerified) {
                                                        delay(5000) // Chờ 5 giây trước khi kiểm tra lại
                                                        user.reload().await() // Tải lại thông tin người dùng
                                                    }
                                                    Log.d(TAG, "User email verified successfully.")
                                                    val userData = hashMapOf(
                                                        "userID" to user.uid,
                                                        "username" to username,
                                                        "profilePicture" to "",
                                                        "createdAt" to System.currentTimeMillis()
                                                    )
                                                    db.collection("users").document(user.uid).set(userData).await()
                                                    continuation.resume(true)  // Tiếp tục khi xác minh thành công

                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Failed to verify email: ${e.message}")
                                                    continuation.resumeWithException(e)
                                                }
                                            }
                                        } else {
                                            Log.e(TAG, "Failed to send verification email.", verificationTask.exception)
                                            continuation.resume(false)
                                        }
                                    }
                            } else {
                                continuation.resume(false)
                                Log.e("Sign Up Repo","user creaation failed!")
                            }
                        } else {
                            continuation.resume(false)
                            Log.e("Sign Up Repo","Sign Up failed")
                        }
                    }
                }
        }catch (e: Exception){
            Log.e("Sign Up Repo","Some thing went wrong!")
            return false
        }
    }

    override suspend fun isEmailExists(email: String): Boolean {
//        return try {
//            val user = fb.currentUser
//        } catch (e: Exception) {
//            false
//        }
        return false
    }

    override suspend fun isUserExists(username: String): Boolean {
        val query = db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .await()
        return !query.isEmpty
    }

    override suspend fun sendEmailVerify(request: EmailVerificationRequest): Result<EmailVerificationResponse> {
        return suspendCancellableCoroutine { continuation ->
            fbs.sendEmailVerification(request).enqueue(object : Callback<EmailVerificationResponse> {
                override fun onResponse(call: Call<EmailVerificationResponse>, response: Response<EmailVerificationResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            continuation.resume(Result.success(responseBody))
                        } else {
                            continuation.resume(Result.failure(Exception("Response body is null")))
                        }
                    } else {
                        continuation.resume(Result.failure(Exception("Error: ${response.message()}")))
                    }
                }

                override fun onFailure(call: Call<EmailVerificationResponse>, t: Throwable) {
                    continuation.resume(Result.failure(t))
                }
            })
        }
    }
    suspend fun resendVerificationEmail(user: FirebaseUser) {
        try {
            user.sendEmailVerification().await()
            Log.d("AuthRepository", "Verification email resent successfully")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to resend verification email: ${e.message}")
            // Xử lý lỗi gửi lại email
        }
    }
}