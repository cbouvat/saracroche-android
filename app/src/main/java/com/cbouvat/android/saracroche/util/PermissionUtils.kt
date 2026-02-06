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
        return try {
            val telecomManager =
                context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager?

            // Check if app is default dialer
            val isDefaultDialer = telecomManager?.defaultDialerPackage == context.packageName

            // Check if call screening role is granted (API 29+)
            val hasCallScreeningRole = isCallScreeningRoleGranted(context)

            Log.d(
                TAG,
                "Call screening status - isDefaultDialer: $isDefaultDialer, hasCallScreeningRole: $hasCallScreeningRole"
            )

            // Return true if any of the call screening mechanisms are enabled
            isDefaultDialer || hasCallScreeningRole
        } catch (t: Throwable) {
            Log.e(TAG, "Error checking call screening status", t)
            false
        }
    }

    /**
     * Check if call screening role is granted (API 29+)
     */
    private fun isCallScreeningRoleGranted(context: Context): Boolean {
        return try {
            val roleManager =
                context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager?
            roleManager?.isRoleHeld(android.app.role.RoleManager.ROLE_CALL_SCREENING) ?: false
        } catch (t: Throwable) {
            Log.e(TAG, "Error checking call screening role", t)
            false
        }
    }

    /**
     * Create an intent to request the call screening role (API 29+).
     * Returns the intent to be launched by the caller.
     */
    fun createCallScreeningRoleIntent(context: Context): Intent? {
        return try {
            val roleManager =
                context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager?
            roleManager?.createRequestRoleIntent(android.app.role.RoleManager.ROLE_CALL_SCREENING)
        } catch (t: Throwable) {
            Log.e(TAG, "Error creating call screening role intent", t)
            null
        }
    }

    /**
     * Open call screening settings
     */
    fun openCallScreeningSettings(context: Context) {
        try {
            Log.d(TAG, "Opening call screening settings")

            // First try to request call screening role (API 29+)
            Log.d(TAG, "Attempting to request call screening role (API 29+)")
            val intent = createCallScreeningRoleIntent(context)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    Log.d(TAG, "Starting call screening role request")
                    context.startActivity(intent)
                    return
                }
            }

            // Fallback: Try to open default apps settings
            Log.d(TAG, "Fallback: Opening default apps settings")
            if (openDefaultAppsSettings(context)) {
                return
            }

            // Final fallback: open general phone settings
            Log.d(TAG, "Final fallback: Opening phone settings")
            openPhoneSettings(context)
        } catch (t: Throwable) {
            Log.e(TAG, "Error opening call screening settings", t)
            openPhoneSettings(context)
        }
    }

    /**
     * Request call screening role for API 29+
     */
    private fun requestCallScreeningRole(context: Context): Boolean {
        return try {
            val roleManager =
                context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager?
            roleManager?.let {
                val intent =
                    it.createRequestRoleIntent(android.app.role.RoleManager.ROLE_CALL_SCREENING)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    Log.d(TAG, "Starting call screening role request")
                    context.startActivity(intent)
                    true
                } else {
                    Log.w(TAG, "No activity found to handle call screening role request")
                    false
                }
            } ?: false
        } catch (t: Throwable) {
            Log.e(TAG, "Error requesting call screening role", t)
            false
        }
    }

    /**
     * Open default apps settings
     */
    private fun openDefaultAppsSettings(context: Context): Boolean {
        val defaultAppsIntents = listOf(
            // Try to open specific default apps settings for calls
            Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"),
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
            // Try to open assistant and voice input settings (sometimes includes call screening)
            Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
        )

        for (intent in defaultAppsIntents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    Log.d(TAG, "Opening default apps settings with intent: ${intent.action}")
                    context.startActivity(intent)
                    return true
                }
            } catch (t: Throwable) {
                Log.w(TAG, "Could not open default apps settings with intent: ${intent.action}", t)
            }
        }

        return false
    }

    /**
     * Open phone settings as fallback
     */
    private fun openPhoneSettings(context: Context) {
        val settingsIntents = listOf(
            // Try to open default apps settings for calls
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
            // Try phone app settings
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:com.android.phone")
            },
            // Try Google Dialer settings
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:com.google.android.dialer")
            },
            // General phone settings
            Intent(Settings.ACTION_SOUND_SETTINGS),
            // App settings as last resort
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        )

        for (intent in settingsIntents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    context.startActivity(intent)
                    Log.d(TAG, "Opened settings with intent: ${intent.action}")
                    return
                }
            } catch (t: Throwable) {
                Log.w(TAG, "Could not open settings with intent: ${intent.action}", t)
            }
        }

        Log.e(TAG, "Failed to open any settings")
    }
}
