package com.cbouvat.android.saracroche.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cbouvat.android.saracroche.R
import com.cbouvat.android.saracroche.util.NotificationUtils

object NotificationService {
    private const val TAG = "NotificationService"
    private val NOTIFICATION_ICON = R.drawable.notification_icon;

    fun sendBlockedCallNotification(context: Context, phoneNumber: String?) {
        if (phoneNumber.isNullOrBlank()) {
            sendUnknownBlockedCallNotification(context)
        } else {
            sendKnownBlockedCallNotification(context, phoneNumber)
        }
    }

    private fun sendUnknownBlockedCallNotification(context: Context) {
        Log.d(TAG, "Sending notification for blocked unknown call")

        val notificationId = "unknown-caller-${System.currentTimeMillis()}".hashCode()

        val notification =
            NotificationCompat.Builder(context, NotificationUtils.BLOCKED_UNKNOWN_CALLS_CHANNEL_ID)
                .setSmallIcon(NOTIFICATION_ICON)
                .setContentTitle("Appel bloqué")
                .setContentText("Numéro masqué")
                .setPriority(NotificationUtils.BLOCKED_UNKNOWN_CALLS_NOTIFICATION_PRIORITY)
                .setAutoCancel(true)

        send(context, notificationId, notification)

    }

    private fun sendKnownBlockedCallNotification(context: Context, phoneNumber: String) {
        Log.d(TAG, "Sending notification for blocked call from: $phoneNumber")

        val notificationId = "${phoneNumber}-${System.currentTimeMillis()}".hashCode()

        val notification =
            NotificationCompat.Builder(context, NotificationUtils.BLOCKED_CALLS_CHANNEL_ID)
                .setSmallIcon(NOTIFICATION_ICON)
                .setContentTitle("Appel bloqué")
                .setContentText(phoneNumber)
                .setPriority(NotificationUtils.BLOCKED_CALLS_NOTIFICATION_PRIORITY)
                .setAutoCancel(true)

        send(context, notificationId, notification)
    }

    private fun send(
        context: Context,
        notificationId: Int,
        notification: NotificationCompat.Builder
    ) {
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "POST_NOTIFICATIONS permission not granted")
                return
            }
            notify(notificationId, notification.build())
        }
    }
}
