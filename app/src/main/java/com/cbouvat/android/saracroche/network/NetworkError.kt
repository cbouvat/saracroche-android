package com.cbouvat.android.saracroche.network

import android.content.Context
import androidx.annotation.StringRes
import com.cbouvat.android.saracroche.R

sealed class NetworkError(@param:StringRes val messageRes: Int, vararg val formatArgs: Any) : Exception() {
    object InvalidURL : NetworkError(R.string.error_invalid_url)
    object NoData : NetworkError(R.string.error_no_data)
    object DecodingError : NetworkError(R.string.error_decoding)
    data class ServerError(val code: Int, val serverMessage: String?) : NetworkError(
        if (serverMessage != null) R.string.error_server_with_message else R.string.error_server,
        code, serverMessage ?: ""
    )

    object NetworkUnavailable : NetworkError(R.string.error_network_unavailable)
    object Timeout : NetworkError(R.string.error_timeout)
    object Unknown : NetworkError(R.string.error_unknown)

    fun getUserMessage(context: Context): String {
        return if (formatArgs.isNotEmpty()) {
            context.getString(messageRes, *formatArgs)
        } else {
            context.getString(messageRes)
        }
    }

    val userMessage: String
    get() = when (this) {
        is ServerError -> {
            if (formatArgs.isNotEmpty() && formatArgs[0] is Int && formatArgs[1] is String) {
                "R.string.error_server_with_message"
            } else {
                "R.string.error_server"
            }
        }
        else -> "R.string.error_unknown"
    }
}