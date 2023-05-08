package com.github.burachevsky.mqtthub.core.ui.notification

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.ui.R

fun Context.createNotificationChannels() {
    getSystemService<NotificationManager>()
        ?.createNotificationChannels(
            listOf(
                NotificationChannel(
                    NotificationChannelId.PAYLOAD_UPDATES,
                    getString(R.string.notification_channel_name_payload_updates),
                    NotificationManager.IMPORTANCE_HIGH,
                ),
                NotificationChannel(
                    NotificationChannelId.MQTT_CONNECTION,
                    getString(R.string.notification_channel_name_mqtt_connection),
                    NotificationManager.IMPORTANCE_HIGH,
                ),
            )
        )
}

fun Context.isNotificationsPermissionGranted(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
        val status = ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
        return status == PERMISSION_GRANTED
    }

    return true
}

fun Context.notifyPayloadUpdate(notifyList: List<Tile>) {
    notifyList.forEach(::notifyPayloadUpdate)
}

fun Context.notifyPayloadUpdate(tile: Tile) {
    val builder = NotificationCompat
        .Builder(this, NotificationChannelId.PAYLOAD_UPDATES)
        .setSmallIcon(R.drawable.ic_notification_small)
        .setContentTitle(tile.name)
        .setContentText(tile.payload.stringValue)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setGroup("${tile.id}")
        .setGroupSummary(true)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent().setClassName(
                    this,
                    "com.github.burachevsky.mqtthub.AppActivity"
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .setAutoCancel(true)

    val permissionGranted = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            val status = ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
            status == PERMISSION_GRANTED
        }

        else -> true
    }

    if (permissionGranted) {
        NotificationManagerCompat.from(this)
            .notify(NotificationId.next(), builder.build())
    }
}