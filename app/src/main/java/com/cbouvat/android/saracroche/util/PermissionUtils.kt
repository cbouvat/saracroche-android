package com.cbouvat.android.saracroche.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.telecom.TelecomManager

/**
 * Utility for managing call screening permissions
 */
object PermissionUtils {

    /**
     * Check if the app is set as the default call screening app
     */
    fun isCallScreeningEnabled(context: Context): Boolean {
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        return telecomManager.defaultDialerPackage == context.packageName ||
            isCallScreeningRoleGranted(context)
    }

    /**
     * Check if call screening role is granted (API 29+)
     */
    private fun isCallScreeningRoleGranted(context: Context): Boolean {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val roleManager =
                    context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager
                roleManager.isRoleHeld(android.app.role.RoleManager.ROLE_CALL_SCREENING)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Open call screening settings
     */
    fun openCallScreeningSettings(context: Context) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val roleManager =
                    context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager
                val intent =
                    roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_CALL_SCREENING)
                context.startActivity(intent)
            } else {
                // Fallback to general phone settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            // Fallback to app settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
        }
    }
}
