package com.hung.musicstreamingapplication.data.model

import com.google.firebase.Timestamp

data class Comment(
    var id: String = "",
    val interactiveUserIDs: List<String> = emptyList(),
    val parentID : String = "",
    val songID: String = "",
    val userID: String = "",
    var imageUrl: String = "",
    var username: String = "",
    val content: String = "",
    val created_at : Timestamp = Timestamp.now()
)
