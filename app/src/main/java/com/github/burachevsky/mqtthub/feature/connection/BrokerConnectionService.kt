package com.github.burachevsky.mqtthub.feature.connection

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.burachevsky.mqtthub.App
import com.github.burachevsky.mqtthub.AppActivity
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.notification.NotificationChannelId
import com.github.burachevsky.mqtthub.common.notification.NotificationId
import com.github.burachevsky.mqtthub.common.notification.notifyPayloadUpdate
import com.github.burachevsky.mqtthub.di.Name
import com.github.burachevsky.mqtthub.domain.connection.BrokerConnection
import com.github.burachevsky.mqtthub.domain.connection.BrokerConnectionEvent
import com.github.burachevsky.mqtthub.domain.connection.BrokerConnectionPool
import com.github.burachevsky.mqtthub.domain.connection.NotifyPayloadUpdate
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.domain.usecase.broker.GetBroker
import com.github.burachevsky.mqtthub.domain.usecase.broker.ObserveCurrentBroker
import com.github.burachevsky.mqtthub.domain.usecase.tile.ObserveTopicUpdates
import com.github.burachevsky.mqtthub.domain.usecase.tile.UpdatePayloadAndGetTilesToNotify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class BrokerConnectionService : Service() {

    private val serviceJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + serviceJob)

    @Inject
    @Named(Name.MQTT_EVENT_BUS)
    lateinit var eventBus: EventBus

    @Inject
    lateinit var getBroker: GetBroker

    @Inject
    lateinit var observeCurrentBroker: ObserveCurrentBroker

    @Inject
    lateinit var connectionPool: BrokerConnectionPool

    @Inject
    lateinit var updatePayloadAndGetTilesToNotify: UpdatePayloadAndGetTilesToNotify

    @Inject
    lateinit var observeTopicUpdates: ObserveTopicUpdates

    private var brokerConnection: BrokerConnection? = null

    private var notificationId = NotificationId.next()

    override fun onCreate() {
        super.onCreate()
        (application as App).appComponent.inject(this)
        initConnectionService()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            Action.DISCONNECT, Action.DISMISS -> {
                stopSelf()
            }

            Action.RETRY -> {
                brokerConnection {
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
                brokerConnection?.stop(BrokerConnectionEvent::Terminated)

                broker ?: return@collect

                startForeground(broker.name, NotificationConfig.Connecting())

                brokerConnection = BrokerConnection(
                    broker,
                    eventBus,
                    connectionPool,
                    updatePayloadAndGetTilesToNotify,
                    observeTopicUpdates,
                )

                brokerConnection { start() }
            }
        }
    }

    private fun subscribeOnAppEvents() {
        eventBus.subscribe<BrokerConnectionEvent>(scope) { event ->
            when (event) {
                is BrokerConnectionEvent.Connected -> {
                    brokerConnection {
                        startForeground(broker.name, NotificationConfig.Connected())
                    }
                }

                is BrokerConnectionEvent.FailedToConnect,
                is BrokerConnectionEvent.LostConnection -> {
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
            NotificationChannelId.BROKER_CONNECTION
        )
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle(brokerName)
            .setContentText(getString(config.textRes))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, AppActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .apply {
                config.action?.invoke(this@BrokerConnectionService)
                    ?.forEach(::addAction)
            }
        return builder.build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        brokerConnection?.stop()
        serviceJob.cancel()
    }

    private inline fun brokerConnection(block: BrokerConnection.() -> Unit) {
        brokerConnection?.block()
    }
}