package com.hung.musicstreamingapplication.data.remote

import com.hung.musicstreamingapplication.data.model.YouTubeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeAPI {
    @GET("videos")
    fun popularKeywords(
        @Query("part") part: String = "snippet",
        @Query("chart") chart: String = "mostPopular",
        @Query("regionCode") regionCode: String = "VN",
        @Query("videoCategoryId") videoCategoryId: String = "10",
        @Query("key") apikey: String =""
    ):Call<YouTubeResponse>
}