package com.hung.musicstreamingapplication.presentation.verify

data class EmailVerificationRequest(
    val requestType: String = "VERIFY_EMAIL",
    val idToken: String
)
data class EmailVerificationResponse(
    val email: String,
    val kind: String,
    val requestType: String
)