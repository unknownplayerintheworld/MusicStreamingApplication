    package com.hung.musicstreamingapplication.data.model


    data class Playlist(
        var id: String = "",
        val name:String = "",
        val description: String = "",
        val imageUrl:String = "",
        val songIDs: List<String> = emptyList(),
        val created_at: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
        val author: String = "",
        val userID: String = "",
        val userName: String? = ""
    )
