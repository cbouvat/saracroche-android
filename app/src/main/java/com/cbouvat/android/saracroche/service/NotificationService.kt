package com.cbouvat.android.saracroche.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cbouvat.android.saracroche.util.NotificationUtils

object NotificationService {
    fun sendBlockedCallNotification(context: Context, phoneNumber: String?) {
        Log.d("NotificationService", "Sending notification for blocked call from: $phoneNumber")
        val notificationId = "${phoneNumber}-${System.currentTimeMillis()}".hashCode()

        val notification =
            NotificationCompat.Builder(context, NotificationUtils.BLOCKED_CALL_CHANNEL_ID)
                //TODO: icon
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setContentTitle("Appel bloqué")
                .setContentText(phoneNumber ?: "Numéro masqué")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, notification.build())
        }

    }
}
