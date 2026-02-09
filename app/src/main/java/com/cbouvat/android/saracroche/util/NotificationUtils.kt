package com.cbouvat.android.saracroche.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

object NotificationUtils {
    const val BLOCKED_CALLS_CHANNEL_ID = "blocked_calls_channel"
    const val BLOCKED_CALLS_CHANNEL_NAME = "Appels bloqués"
    const val BLOCKED_CALLS_CHANNEL_DESCRIPTION = "Notifications des appels bloqués."
    const val BLOCKED_CALLS_CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT
    const val BLOCKED_CALLS_NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_DEFAULT

    const val BLOCKED_UNKNOWN_CALLS_CHANNEL_ID = "blocked_unknown_calls_channel"
    const val BLOCKED_UNKNOWN_CALLS_CHANNEL_NAME = "Appels masqués bloqués"
    const val BLOCKED_UNKNOWN_CALLS_CHANNEL_DESCRIPTION =
        "Notifications des appels masqués bloqués."
    const val BLOCKED_UNKNOWN_CALLS_CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT
    const val BLOCKED_UNKNOWN_CALLS_NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_DEFAULT

    private fun createCallsNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            BLOCKED_CALLS_CHANNEL_ID,
            BLOCKED_CALLS_CHANNEL_NAME,
            BLOCKED_CALLS_CHANNEL_IMPORTANCE
        )
            .apply { description = BLOCKED_CALLS_CHANNEL_DESCRIPTION }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    private fun createUnknownCallsNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            BLOCKED_UNKNOWN_CALLS_CHANNEL_ID,
            BLOCKED_UNKNOWN_CALLS_CHANNEL_NAME,
            BLOCKED_UNKNOWN_CALLS_CHANNEL_IMPORTANCE
        )
            .apply { description = BLOCKED_UNKNOWN_CALLS_CHANNEL_DESCRIPTION }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    fun createAllNotificationChannels(context: Context) {
        createCallsNotificationChannel(context)
        createUnknownCallsNotificationChannel(context)
    }
}
