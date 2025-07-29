package com.cbouvat.android.saracroche.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.telecom.TelecomManager
import android.util.Log

/**
 * Utility for managing call screening permissions
 */
object PermissionUtils {

    private const val TAG = "PermissionUtils"

    /**
     * Check if the app is set as the default call screening app
     */
    fun isCallScreeningEnabled(context: Context): Boolean {
        Log.d(TAG, "Checking call screening status")
        return try {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager?
            val isDefaultDialer = telecomManager?.defaultDialerPackage == context.packageName
            val hasCallScreeningRole = isCallScreeningRoleGranted(context)
            
            Log.d(TAG, "Package name: ${context.packageName}")
            Log.d(TAG, "Default dialer package: ${telecomManager?.defaultDialerPackage}")
            Log.d(TAG, "Is default dialer: $isDefaultDialer")
            Log.d(TAG, "Has call screening role: $hasCallScreeningRole")
            
            val result = isDefaultDialer || hasCallScreeningRole
            Log.d(TAG, "Call screening enabled: $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error checking call screening status", e)
            false
        }
    }

    /**
     * Check if call screening role is granted (API 29+)
     */
    private fun isCallScreeningRoleGranted(context: Context): Boolean {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val roleManager =
                    context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager?
                val result = roleManager?.isRoleHeld(android.app.role.RoleManager.ROLE_CALL_SCREENING) ?: false
                Log.d(TAG, "Call screening role granted: $result")
                result
            } else {
                Log.d(TAG, "API < Q, no call screening role")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking call screening role", e)
            false
        }
    }

    /**
     * Open call screening settings
     */
    fun openCallScreeningSettings(context: Context) {
        Log.d(TAG, "openCallScreeningSettings called")
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                Log.d(TAG, "API >= Q, using RoleManager")
                val roleManager =
                    context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager
                val intent =
                    roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_CALL_SCREENING)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                
                // Check if the intent can be resolved
                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    context.startActivity(intent)
                    Log.d(TAG, "Role intent launched successfully")
                } else {
                    Log.w(TAG, "Role intent cannot be resolved, falling back to phone settings")
                    openPhoneSettings(context)
                }
            } else {
                Log.d(TAG, "API < Q, using phone settings")
                openPhoneSettings(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening call screening settings", e)
            openPhoneSettings(context)
        }
    }
    
    private fun openPhoneSettings(context: Context) {
        try {
            // Try to open phone app settings first
            val phoneIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            phoneIntent.data = Uri.parse("package:com.google.android.dialer")
            phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            if (context.packageManager.resolveActivity(phoneIntent, 0) != null) {
                context.startActivity(phoneIntent)
                Log.d(TAG, "Phone app settings opened")
                return
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not open phone app settings", e)
        }
        
        try {
            // Fallback to general phone settings
            val phoneSettingsIntent = Intent(Settings.ACTION_SOUND_SETTINGS)
            phoneSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(phoneSettingsIntent)
            Log.d(TAG, "General phone settings opened")
        } catch (e: Exception) {
            Log.w(TAG, "Could not open phone settings", e)
            // Final fallback to app settings
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Log.d(TAG, "App settings opened as final fallback")
            } catch (fallbackException: Exception) {
                Log.e(TAG, "Error in all fallback attempts", fallbackException)
            }
        }
    }
}
