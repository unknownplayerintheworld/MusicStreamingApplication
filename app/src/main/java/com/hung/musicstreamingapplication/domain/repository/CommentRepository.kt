package com.hung.musicstreamingapplication.domain.repository

import com.hung.musicstreamingapplication.data.model.Comment

interface CommentRepository {
    suspend fun getCommentBySong(songID: String): List<Comment>?
    suspend fun writeComment(userID: String,content: String,songID: String,parentID: String = ""): Int
    suspend fun delComment(commentID: String): Int
    suspend fun getChildComments(commentID: String): List<Comment>?
    suspend fun unFavComment(commentID: String,userID: String): Int
    suspend fun favComment(commentID: String,userID: String):Int
}