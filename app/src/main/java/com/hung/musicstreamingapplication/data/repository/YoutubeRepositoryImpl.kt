package com.hung.musicstreamingapplication.data.repository

import com.hung.musicstreamingapplication.data.model.YouTubeResponse
import com.hung.musicstreamingapplication.data.remote.YoutubeAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class YoutubeRepositoryImpl @Inject constructor(
    private val retrofit: YoutubeAPI
) {
    fun getPopularVideos(onResult: (List<String>) -> Unit) {
        retrofit.popularKeywords("snippet","mostPopular","VN","10","AIzaSyCoa5A_fH7dzJIdNN_KW-RY7mYY-UoSUH8").enqueue(object : Callback<YouTubeResponse> {
            override fun onResponse(call: Call<YouTubeResponse>, response: Response<YouTubeResponse>) {
                if (response.isSuccessful) {
                    // Lấy danh sách video title từ response
                    val videoTitles = response.body()?.items?.map { it.snippet.title } ?: emptyList()
                    // Gọi callback với danh sách video titles
                    onResult(videoTitles)
                } else {
                    // Xử lý lỗi nếu có
                    println("Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<YouTubeResponse>, t: Throwable) {
                // Xử lý lỗi kết nối
                t.printStackTrace()
            }
        })
    }
}