package com.hung.musicstreamingapplication.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hung.musicstreamingapplication.data.model.Comment
import com.hung.musicstreamingapplication.data.model.User
import com.hung.musicstreamingapplication.domain.repository.CommentRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): CommentRepository {
    @SuppressLint("SuspiciousIndentation")
    override suspend fun getCommentBySong(songID: String): List<Comment>? {
        return try {
            val snapshot = firestore.collection("comment")
                .whereEqualTo("parentID","")
                .whereEqualTo("songID", songID)
                .get()
                .await()

            // Lấy danh sách các bình luận
            val comments = snapshot.mapNotNull { doc ->
                doc.toObject(Comment::class.java)?.apply {
                    id = doc.id
                }
            }

            // Lấy thông tin profile của từng user dựa trên userID trong từng comment
            val commentsWithProfile = comments.mapNotNull { comment ->
                val userSnapshot =
                    firestore.collection("users") // Giả sử bạn có collection "users" lưu thông tin user
                        .document(comment.userID)
                        .get()
                        .await()

                // Lấy thông tin người dùng và cập nhật vào comment
                val user = userSnapshot.toObject(User::class.java)
                comment.username = user?.username.toString()
                comment.imageUrl =
                    user?.profilePicture.toString() // Giả sử User có trường profilePicture
                comment
            }

            commentsWithProfile
        } catch (e: Exception) {
            Log.e("Comment_Repo", e.message.toString())
            emptyList()
        }
    }


    override suspend fun writeComment(
        userID: String,
        content: String,
        songID: String,
        parentID: String
    ): Int {
        return try {
            // Ghi lại thông tin trước khi thêm tài liệu
            Log.d("CommentRepositoryImpl", "Adding comment: songID=$songID, userID=$userID, content=$content, parentID=$parentID")

            val data = hashMapOf(
                "songID" to songID,
                "userID" to userID,
                "content" to content,
                "parentID" to parentID,
                "created_at" to Timestamp.now(),
                "interactiveUserIDs" to emptyList<String>()
            )

            // Thêm tài liệu mới vào collection "comments"
            firestore.collection("comment")
                .add(data)
                .await() // Chờ cho việc thêm tài liệu hoàn tất

            Log.d("CommentRepositoryImpl", "Comment added successfully.")
            1 // Thao tác thành công, bạn có thể thêm mã xử lý khác nếu cần
        } catch (e: Exception) {
            // Ghi lại lỗi chi tiết
            Log.e("CommentRepositoryImpl", "Error adding comment: ${e.message}", e)
            0 // Xử lý lỗi nếu có
        }
    }

    override suspend fun getChildComments(commentID: String): List<Comment>? {
        return try {
            // Lấy tất cả các comment từ collection "comments"
            val commentsSnapshot = firestore.collection("comment").get().await()

            // Danh sách comment con
            val childComments = mutableListOf<Comment>()

            // Lặp qua từng comment để kiểm tra parentID
            for (document in commentsSnapshot.documents) {
                val comment = document.toObject(Comment::class.java) // Chuyển đổi document thành đối tượng Comment
                comment?.let {
                    // Gán id cho comment từ document ID
                    it.id = document.id

                    // Kiểm tra nếu parentID của comment là commentID
                    if (it.parentID == commentID) {
                        // Lấy thông tin user từ collection "users" bằng userID của comment
                        val userSnapshot = firestore.collection("users").document(it.userID).get().await()
                        val username = userSnapshot.getString("username")
                        val profilePicture = userSnapshot.getString("profilePicture")

                        // Gán thông tin user cho comment
                        it.username = username ?: "Unknown User" // Gán username hoặc giá trị mặc định
                        it.imageUrl = profilePicture ?: "" // Gán profilePicture hoặc giá trị mặc định

                        // Thêm comment vào danh sách comment con
                        childComments.add(it)
                    }
                }
            }

            // Trả về danh sách comment con
            childComments
        } catch (e: Exception) {
            // Xử lý lỗi nếu có
            Log.e("CommentRepositoryImpl", "Error getting child comments: ${e.message}", e)
            null // Trả về null trong trường hợp có lỗi
        }
    }


    override suspend fun favComment(commentID: String, userID: String): Int {
        return try {
            // Lấy tài liệu comment từ Firestore
            val commentRef = firestore.collection("comment").document(commentID)

            // Cập nhật trường interactiveUserIDs, thêm userID vào mảng
            commentRef.update("interactiveUserIDs", FieldValue.arrayUnion(userID)).await()

            Log.d("FavouriteComment", "User $userID đã được thêm vào danh sách yêu thích của comment $commentID")
            1
        } catch (e: Exception) {
            Log.e("FavouriteComment", "Lỗi khi thêm userID vào interactiveUserIDs: ${e.message}", e)
            0
        }
    }

        override suspend fun unFavComment(commentID: String, userID: String) :Int{
            return try {
                // Lấy tài liệu comment từ Firestore
                val commentRef = firestore.collection("comment").document(commentID)

                // Cập nhật trường interactiveUserIDs, xóa userID khỏi mảng
                commentRef.update("interactiveUserIDs", FieldValue.arrayRemove(userID)).await()

                Log.d("UnfavouriteComment", "User $userID đã được xóa khỏi danh sách yêu thích của comment $commentID")
                0
            } catch (e: Exception) {
                Log.e("UnfavouriteComment", "Lỗi khi xóa userID khỏi interactiveUserIDs: ${e.message}", e)
                1
            }
        }

    override suspend fun delComment(commentID: String): Int {
        return try {
            // Truy cập collection "comment" và xóa bình luận theo commentID
            firestore.collection("comment").document(commentID).delete().await()
            Log.d("CommentRepository", "Comment deleted successfully")
            1 // Trả về true nếu xóa thành công
        } catch (e: Exception) {
            // Xử lý lỗi nếu có
            Log.e("CommentRepository", "Error deleting comment: ${e.message}", e)
            0 // Trả về false nếu gặp lỗi
        }
    }
}