package com.cbouvat.android.saracroche.network

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import com.cbouvat.android.saracroche.config.Config
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.nio.charset.StandardCharsets

class NetworkService(private val context: Context) {
    private val gson = Gson()
    
    companion object {
        private const val TIMEOUT_SECONDS = 10
        private const val RESOURCE_TIMEOUT_SECONDS = 30
    }

    suspend fun reportPhoneNumber(phoneNumber: String) {
        withContext(Dispatchers.IO) {
            val url = URL(Config.REPORT_SERVER_URL)
            val connection = url.openConnection() as HttpURLConnection
            
            try {
                // Configuration de la connexion
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = TIMEOUT_SECONDS * 1000
                connection.readTimeout = RESOURCE_TIMEOUT_SECONDS * 1000

                // Création de la requête
                val requestData = ReportRequest(
                    number = phoneNumber,
                    deviceId = getDeviceId(),
                    appVersion = getAppVersion()
                )

                // Envoi des données
                val jsonData = gson.toJson(requestData)
                connection.outputStream.use { outputStream ->
                    outputStream.write(jsonData.toByteArray(StandardCharsets.UTF_8))
                }

                // Vérification de la réponse
                val responseCode = connection.responseCode
                when (responseCode) {
                    in 200..299 -> {
                        // Succès, rien à faire
                        return@withContext
                    }
                    in 400..499, in 500..599 -> {
                        val errorMessage = extractErrorMessage(connection)
                        throw NetworkError.ServerError(responseCode, errorMessage)
                    }
                    else -> {
                        throw NetworkError.Unknown
                    }
                }
            } catch (e: SocketTimeoutException) {
                throw NetworkError.Timeout
            } catch (e: IOException) {
                throw NetworkError.NetworkUnavailable
            } catch (e: NetworkError) {
                throw e
            } catch (e: Exception) {
                throw NetworkError.Unknown
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun extractErrorMessage(connection: HttpURLConnection): String? {
        return try {
            val errorStream = connection.errorStream
            if (errorStream != null) {
                val errorText = errorStream.bufferedReader().use { it.readText() }
                
                // Tentative de parsing JSON
                try {
                    val errorResponse = gson.fromJson(errorText, ErrorResponse::class.java)
                    errorResponse.message ?: errorResponse.error ?: errorResponse.detail
                } catch (e: JsonSyntaxException) {
                    // Si ce n'est pas du JSON, retourner le texte brut
                    if (errorText.isNotBlank()) errorText else null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getDeviceId(): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }
}
