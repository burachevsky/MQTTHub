package com.github.burachevsky.mqtthub.feature.mqttservice

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.burachevsky.mqtthub.core.domain.usecase.broker.ObserveCurrentBroker
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.ObserveTopicUpdates
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.UpdatePayloadAndGetTilesToNotify
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.eventbus.MQTT_EVENT_BUS
import com.github.burachevsky.mqtthub.core.mqtt.MqttConnection
import com.github.burachevsky.mqtthub.core.mqtt.MqttConnectionEvent
import com.github.burachevsky.mqtthub.core.mqtt.MqttConnectionPool
import com.github.burachevsky.mqtthub.core.mqtt.NotifyPayloadUpdate
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.ext.applicationAs
import com.github.burachevsky.mqtthub.core.ui.notification.NotificationChannelId
import com.github.burachevsky.mqtthub.core.ui.notification.NotificationId
import com.github.burachevsky.mqtthub.core.ui.notification.notifyPayloadUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class MqttService : Service() {

    private val serviceJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + serviceJob)

    @Inject
    @Named(MQTT_EVENT_BUS)
    lateinit var eventBus: EventBus

    @Inject
    lateinit var observeCurrentBroker: ObserveCurrentBroker

    @Inject
    lateinit var connectionPool: MqttConnectionPool

    @Inject
    lateinit var updatePayloadAndGetTilesToNotify: UpdatePayloadAndGetTilesToNotify

    @Inject
    lateinit var observeTopicUpdates: ObserveTopicUpdates

    private var mqttConnection: MqttConnection? = null

    private var notificationId = NotificationId.next()

    override fun onCreate() {
        super.onCreate()
        applicationAs<MqttServiceComponent.Provider>()
            .mqttServiceComponent()
            .inject(this)
        initConnectionService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.DISCONNECT, Action.DISMISS -> {
                stopSelf()
            }

            Action.RETRY -> {
                mqttConnection {
                    restart()
                }
            }
        }

        return START_STICKY
    }

    private fun initConnectionService() {
        scope.launch {
            subscribeOnAppEvents()

            observeCurrentBroker().collect { broker ->
                mqttConnection?.stop(MqttConnectionEvent::Terminated)

                broker ?: return@collect

                startForeground(broker.name, NotificationConfig.Connecting())

                mqttConnection = MqttConnection(
                    broker,
                    eventBus,
                    connectionPool,
                    updatePayloadAndGetTilesToNotify,
                    observeTopicUpdates,
                )

                mqttConnection { start() }
            }
        }
    }

    private fun subscribeOnAppEvents() {
        eventBus.subscribe<MqttConnectionEvent>(scope) { event ->
            when (event) {
                is MqttConnectionEvent.Connected -> {
                    mqttConnection {
                        startForeground(broker.name, NotificationConfig.Connected())
                    }
                }

                is MqttConnectionEvent.FailedToConnect,
                is MqttConnectionEvent.LostConnection -> {
                    startForeground(
                        event.connection.broker.name,
                        NotificationConfig.ConnectionFailed()
                    )
                }

                else -> {}
            }
        }

        eventBus.subscribe<NotifyPayloadUpdate>(scope) {
            notifyPayloadUpdate(it.notifyList)
        }
    }

    private fun startForeground(brokerName: String, config: NotificationConfig) {
        startForeground(
            notificationId,
            createNotification(
                brokerName,
                config,
            )
        )
    }

    private fun createNotification(brokerName: String, config: NotificationConfig): Notification {
        val builder = NotificationCompat.Builder(
            this,
            NotificationChannelId.MQTT_CONNECTION
        )
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle(brokerName)
            .setContentText(getString(config.textRes))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent().setClassName(
                        this,
                        "com.github.burachevsky.mqtthub.AppActivity"
                    ),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .apply {
                config.action?.invoke(this@MqttService)
                    ?.forEach(::addAction)
            }
        return builder.build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttConnection?.stop()
        serviceJob.cancel()
    }

    private inline fun mqttConnection(block: MqttConnection.() -> Unit) {
        mqttConnection?.block()
    }
}