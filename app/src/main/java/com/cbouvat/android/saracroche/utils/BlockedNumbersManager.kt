package com.cbouvat.android.saracroche.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.annotation.RequiresApi

/**
 * Utilitaire pour gérer les numéros bloqués via TelecomManager
 */
object BlockedNumbersManager {

    /**
     * Vérifie si l'appareil supporte la gestion des numéros bloqués
     */
    fun isBlockedNumbersSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    /**
     * Ouvre l'interface système de gestion des numéros bloqués
     *
     * @param context Le contexte de l'application
     * @return true si l'intent a pu être lancé, false sinon
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun openBlockedNumbersManager(context: Context): Boolean {
        return try {
            val telecomManager =
                context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager

            if (telecomManager == null) {
                showError(context, "Service téléphonique non disponible")
                return false
            }

            val intent = telecomManager.createManageBlockedNumbersIntent()

            if (intent == null) {
                showError(context, "Gestion des numéros bloqués non supportée")
                return false
            }

            // Vérifier qu'il y a une activité pour traiter cet intent
            if (intent.resolveActivity(context.packageManager) == null) {
                showError(context, "Aucune application disponible pour gérer les numéros bloqués")
                return false
            }

            context.startActivity(intent)
            true
        } catch (e: Exception) {
            showError(context, "Erreur lors de l'ouverture : ${e.message}")
            false
        }
    }

    /**
     * Vérifie si l'appareil peut gérer les numéros bloqués
     * et affiche un message approprié si ce n'est pas le cas
     */
    fun checkAndOpenBlockedNumbersManager(context: Context): Boolean {
        return if (!isBlockedNumbersSupported()) {
            showError(context, "Cette fonctionnalité nécessite Android 7.0 ou supérieur")
            false
        } else {
            openBlockedNumbersManager(context)
        }
    }

    /**
     * Crée un intent pour bloquer directement un numéro spécifique
     * Note: Cette méthode nécessite des permissions spéciales et ne fonctionne
     * que pour certaines applications système
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun createBlockNumberIntent(phoneNumber: String, context: Context): Intent? {
        return try {
            val telecomManager =
                context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            telecomManager?.createManageBlockedNumbersIntent()?.apply {
                // Ajouter le numéro comme paramètre extra si supporté
                putExtra("android.telecom.extra.PHONE_NUMBER", phoneNumber)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Affiche un message d'erreur à l'utilisateur
     */
    private fun showError(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Obtient des informations sur les capacités de blocage de l'appareil
     */
    fun getBlockingCapabilitiesInfo(context: Context): String {
        return buildString {
            appendLine("Informations sur le blocage de numéros :")
            appendLine("• Android version: ${Build.VERSION.RELEASE}")
            appendLine("• API Level: ${Build.VERSION.SDK_INT}")
            appendLine("• Blocage supporté: ${if (isBlockedNumbersSupported()) "Oui" else "Non"}")

            if (isBlockedNumbersSupported()) {
                val telecomManager =
                    context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
                val intentAvailable = telecomManager?.createManageBlockedNumbersIntent() != null
                appendLine("• Interface de gestion: ${if (intentAvailable) "Disponible" else "Non disponible"}")
            }
        }
    }
}
