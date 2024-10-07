package com.hung.musicstreamingapplication.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hung.musicstreamingapplication.data.model.User
import com.hung.musicstreamingapplication.domain.repository.LoginRepository
import com.hung.musicstreamingapplication.presentation.sign_in.UserData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LoginRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val fb: FirebaseAuth
): LoginRepository {
    override suspend fun login() {
        TODO("Not yet implemented")
    }

    override suspend fun CreateUserFromOAuth2(
        userData: UserData,
        onSuccess:(String) -> Unit,
        onFailure:(String) -> Unit
    ) {
        val user = hashMapOf(
            "userID" to userData.userId,
            "username" to userData.username,
            "profilePicture" to userData.profilePictureUrl,
            "createdAt" to Timestamp.now()
        )
        val documentRef = db.collection("users").document(userData.userId)
        documentRef.get()
            .addOnSuccessListener {
                if(it.exists()){
                    onSuccess(userData.userId + " already exists!")
                }
                else{
                    db.collection("users")
                        .document(userData.userId)
                        .set(user)
                        .addOnSuccessListener {
                            onSuccess(userData.username + "created successfully!")
                        }
                        .addOnFailureListener{
                                e -> onFailure(e.toString())
                        }
                }
            }
    }

    override suspend fun GetUserFromDB(userID: String): User {
        return try {
            suspendCancellableCoroutine { continuation ->
                db.collection("users")
                    .document(userID)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Chuyển dữ liệu từ tài liệu sang đối tượng User
                            val user = document.toObject(User::class.java)?.copy(userID = document.id)
                            Log.d("FirestoreData", "Retrieved User: $user")
                            continuation.resume(user ?: User()) // Trả về user nếu không null, ngược lại trả về User mặc định
                        } else {
                            Log.d("FirestoreData", "No user found with ID: $userID")
                            continuation.resume(User()) // Trả về User mặc định nếu không tìm thấy
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("FirestoreError", "Error getting document: ", exception)
                        continuation.resumeWithException(exception) // Ném ra exception nếu có lỗi
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            User() // Trả về User mặc định nếu có lỗi
        }
    }

    override suspend fun loginWithEmailorUsername(identifier: String, password: String): Boolean {
        return try {
            // Kiểm tra nếu identifier là email hay username
            val isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()

            if (isEmail) {
                // Đăng nhập bằng email
                val authResult = fb.signInWithEmailAndPassword(identifier, password).await()
                val user = authResult.user ?: throw Exception("Failed to get user")

                // Kiểm tra thông tin người dùng trong Firestore
                val userDocument = db.collection("users").document(user.uid).get().await()
                if (userDocument.exists()) {
                    userDocument.data
                    // Bạn có thể kiểm tra thêm thông tin nếu cần
                    return true
                } else {
                    return false
                }
            } else {
                // Đăng nhập bằng username
                // Tìm uid dựa trên username trong Firestore
                val querySnapshot = db.collection("users").whereEqualTo("username", identifier).get().await()
                if (querySnapshot.documents.isNotEmpty()) {
                    val userDocument = querySnapshot.documents[0]
                    val email = userDocument.getString("email") ?: throw Exception("Email not found for username")

                    // Đăng nhập bằng email và mật khẩu
                    val authResult = fb.signInWithEmailAndPassword(email, password).await()
                    val user = authResult.user ?: throw Exception("Failed to get user")

                    // Kiểm tra thông tin người dùng trong Firestore
                    return userDocument.id == user.uid
                } else {
                    // Không tìm thấy username trong Firestore
                    Log.e("Login Repo","Cannot find username")
                    return false
                }
            }
        } catch (e: Exception) {
            Log.e("SignUpRepository", "Login failed: ${e.message}")
            return false
        }
    }

    override suspend fun loginState() : Boolean {
        return fb.currentUser != null
    }

    override fun getCurrentUserID(): String? {
        return fb.currentUser?.uid
    }

    override suspend fun logout(): Boolean {
        fb.signOut()
        return try{
            fb.currentUser==null
        }catch(e: Exception){
            Log.e("Exception in Login REPOSITORY",e.message.toString())
            false
        }
    }
}