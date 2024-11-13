package com.hung.musicstreamingapplication.data.model

import java.io.File

sealed class DownloadResult {
    object Loading : DownloadResult()
    data class Success(val file: File) : DownloadResult()
    data class Error(val error: String) : DownloadResult()
}