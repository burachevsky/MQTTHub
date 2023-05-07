package com.github.burachevsky.mqtthub.feature.connection

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.github.burachevsky.mqtthub.core.ui.R

sealed class NotificationConfig(
    val textRes: Int,
    val action: ((Context) -> List<NotificationCompat.Action>)?
) {
    class Connected : NotificationConfig(
        textRes = R.string.broker_connection_service_message_connected,
        action = { context ->
            listOf(
                NotificationCompat.Action(
                    null,
                    context.getString(R.string.broker_connection_service_button_disconnect),
                    PendingIntent.getService(
                        context,
                        0,
                        Intent(context, BrokerConnectionService::class.java)
                            .apply { action = Action.DISCONNECT },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                    )
                )
            )
        }
    )

    class Connecting : NotificationConfig(
        textRes = R.string.broker_connection_service_message_connecting,
        action = null
    )

    class ConnectionFailed : NotificationConfig(
        textRes = R.string.broker_connection_service_message_connection_failed,
        action = { context ->
            listOf(
                NotificationCompat.Action(
                    null,
                    context.getString(R.string.broker_connection_service_button_retry),
                    PendingIntent.getService(
                        context,
                        0,
                        Intent(context, BrokerConnectionService::class.java)
                            .apply { action = Action.RETRY },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                    )
                ),
                NotificationCompat.Action(
                    null,
                    context.getString(R.string.broker_connection_service_button_dismiss),
                    PendingIntent.getService(
                        context,
                        0,
                        Intent(context, BrokerConnectionService::class.java)
                            .apply { action = Action.DISMISS },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                    )
                )
            )
        }
    )
}