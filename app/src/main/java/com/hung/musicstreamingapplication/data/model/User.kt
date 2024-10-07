package com.hung.musicstreamingapplication.data.model


data class User (
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val email: String = "",
    val profilePicture: String = "",
    val userID: String = "",
    val username: String = ""
)