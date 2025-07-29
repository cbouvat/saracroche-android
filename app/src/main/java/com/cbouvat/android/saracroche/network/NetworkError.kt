package com.cbouvat.android.saracroche.network

sealed class NetworkError(message: String) : Exception(message) {
    object InvalidURL : NetworkError("URL invalide.")
    object NoData : NetworkError("Aucune donnée reçue du serveur.")
    object DecodingError : NetworkError("Erreur lors du traitement des données.")
    data class ServerError(val code: Int, val serverMessage: String?) : NetworkError(
        serverMessage ?: "Erreur serveur ($code). Veuillez réessayer plus tard."
    )
    object NetworkUnavailable : NetworkError("Connexion réseau indisponible. Vérifiez votre connexion Internet.")
    object Timeout : NetworkError("Délai d'attente dépassé. Veuillez réessayer.")
    object Unknown : NetworkError("Une erreur inattendue s'est produite.")

    val userMessage: String
        get() = message ?: "Une erreur inattendue s'est produite."
}
