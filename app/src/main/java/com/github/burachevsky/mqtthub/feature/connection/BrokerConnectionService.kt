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
import com.github.burachevsky.mqtthub.domain.usecase.tile.GetAllTiles
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
    lateinit var getAllTiles: GetAllTiles

    private var brokerConnection: BrokerConnection? = null

    override fun onCreate() {
        super.onCreate()
        (application as App).appComponent.inject(this)
        initBrokerConnection()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun initBrokerConnection() {
        scope.launch {
            eventBus.subscribe<BrokerConnectionEvent>(this) { event ->
                when (event) {
                    is BrokerConnectionEvent.Connected -> {
                        brokerConnection {
                            launch {
                                getAllTiles()
                                    .map { it.subscribeTopic }
                                    .let(::subscribe)

                                val notification = createNotification(broker.name)
                                startForeground(NotificationId.next(), notification)
                            }
                        }
                    }

                    else -> {}
                }
            }

            eventBus.subscribe<NotifyPayloadUpdate>(this) {
                this@BrokerConnectionService.notifyPayloadUpdate(it.notifyList)
            }

            observeCurrentBroker().collect { broker ->
                brokerConnection?.stop(BrokerConnectionEvent::Terminated)

                broker ?: return@collect

                brokerConnection = BrokerConnection(
                    broker,
                    eventBus,
                    connectionPool,
                    updatePayloadAndGetTilesToNotify,
                )

                brokerConnection { start() }
            }
        }
    }

    private fun createNotification(brokerName: String): Notification {
        val builder = NotificationCompat.Builder(
            this,
            NotificationChannelId.BROKER_CONNECTION
        )
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle(brokerName)
            .setContentText(getString(R.string.broker_connection_service_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, AppActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                NotificationCompat.Action(
                    null,
                    getString(R.string.broker_connection_service_button_disconnect),
                    PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent(this, BrokerConnectionBroadcastReceiver::class.java)
                            .apply { action = Action.DISCONNECT },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                    )
                )
            )
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