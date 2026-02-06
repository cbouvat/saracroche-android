package com.cbouvat.android.saracroche.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationUtils {
    val BLOCKED_CALL_CHANNEL_ID = "blocked_call_channel"
    val BLOCKED_CALL_CHANNEL_NAME = "Appels bloqués"
    val BLOCKED_CALL_CHANNEL_DESCRIPTION = "Notifications des appels bloqués."
    val BLOCKED_CALL_CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT


    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            BLOCKED_CALL_CHANNEL_ID,
            BLOCKED_CALL_CHANNEL_NAME,
            BLOCKED_CALL_CHANNEL_IMPORTANCE
        )
            .apply { description = BLOCKED_CALL_CHANNEL_DESCRIPTION }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel);
    }
}
