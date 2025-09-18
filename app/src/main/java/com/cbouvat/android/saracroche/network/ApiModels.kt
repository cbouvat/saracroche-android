package com.cbouvat.android.saracroche.network

data class ReportRequest(
    val number: String,
    val deviceId: String
)

data class ErrorResponse(
    val message: String? = null,
    val error: String? = null,
    val detail: String? = null
)
