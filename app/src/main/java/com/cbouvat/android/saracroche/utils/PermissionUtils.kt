package com.cbouvat.android.saracroche.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Extensions pour faciliter la gestion des permissions liées au blocage de numéros
 */

/**
 * Permissions nécessaires pour la gestion des numéros bloqués
 */
object BlockingPermissions {
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE
    )

    const val PERMISSION_REQUEST_CODE = 100
}

/**
 * Vérifie si toutes les permissions nécessaires sont accordées
 */
fun Activity.hasBlockingPermissions(): Boolean {
    return BlockingPermissions.REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Demande les permissions nécessaires pour la gestion des numéros bloqués
 */
fun Activity.requestBlockingPermissions() {
    ActivityCompat.requestPermissions(
        this,
        BlockingPermissions.REQUIRED_PERMISSIONS,
        BlockingPermissions.PERMISSION_REQUEST_CODE
    )
}

/**
 * Vérifie les permissions manquantes
 */
fun Activity.getMissingBlockingPermissions(): List<String> {
    return BlockingPermissions.REQUIRED_PERMISSIONS.filter { permission ->
        ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Vérifie si l'utilisateur a refusé définitivement une permission
 */
fun Activity.shouldShowBlockingPermissionRationale(): Boolean {
    return BlockingPermissions.REQUIRED_PERMISSIONS.any { permission ->
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
    }
}
