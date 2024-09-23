    package com.hung.musicstreamingapplication.data.model

    data class Playlist(
        val id: String = "",
        val name:String = "",
        val description: String = "",
        val imageUrl:String = "",
        val songIDs: List<String> = emptyList()
    )
