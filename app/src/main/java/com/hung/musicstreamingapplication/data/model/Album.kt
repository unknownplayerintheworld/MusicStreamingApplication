package com.hung.musicstreamingapplication.data.model

data class Album(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val songIDs: List<String> = emptyList(),
    val authorIDs: List<String> = emptyList(),
    val created_at: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val authorName: String = ""
)
