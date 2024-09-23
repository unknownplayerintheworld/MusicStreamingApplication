package com.hung.musicstreamingapplication.data.remote

import retrofit2.http.GET

interface LoginAPI {
    @GET
    suspend fun login()
}