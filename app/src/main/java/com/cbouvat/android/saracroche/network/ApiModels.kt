package com.cbouvat.android.saracroche.network

data class ReportRequest(
    val phone: Long,
    val device_id: String
)

data class ErrorResponse(
    val message: String? = null,
    val error: String? = null,
    val detail: String? = null
)
