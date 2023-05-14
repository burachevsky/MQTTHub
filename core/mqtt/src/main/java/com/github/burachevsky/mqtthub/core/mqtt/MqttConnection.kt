package com.github.burachevsky.mqtthub.core.mqtt

import com.github.burachevsky.mqtthub.core.domain.usecase.tile.ObserveTopicUpdates
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.UpdatePayloadAndGetTilesToNotify
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.model.Broker
import com.github.burachevsky.mqtthub.core.model.Tile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import timber.log.Timber

class MqttConnection(
    val broker: Broker,
    internal val eventBus: EventBus,
    connectionPool: MqttConnectionPool,
    updatePayloadAndGetTilesToNotify: UpdatePayloadAndGetTilesToNotify,
    observeTopicUpdates: ObserveTopicUpdates,
) {

    private val connectionJob = SupervisorJob()
    internal val connectionScope = CoroutineScope(Dispatchers.IO + connectionJob)

    private val subscriptionManager = SubscriptionManager(
        this,
        updatePayloadAndGetTilesToNotify,
        observeTopicUpdates,
    )

    internal val mqttClient: MqttClient =
        MqttClient(
            broker.getServerAddress(),
            broker.clientId,
            MemoryPersistence()
        )

    private var state = State.DISCONNECTED

    val isRunning get() = state == State.RUNNING
    val isDisconnected get() = state == State.DISCONNECTED
    val isCanceled get() = state == State.CANCELED

    private val mqttCallback = object : MqttCallbackExtended {
        override fun connectionLost(cause: Throwable?) {
            Timber.e(cause)
            reportIfNotCanceled {
                Timber.d("MqttConnection: lost connection: $cause")
                state = State.DISCONNECTED
                MqttConnectionEvent.LostConnection(this@MqttConnection, cause)
            }
        }

        override fun messageArrived(topic: String, message: MqttMessage) {
            launchIfNotCanceled {
                Timber.d("MqttConnection: receiving message from topic $topic: $message")

                subscriptionManager.messageArrived(topic, message)
            }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {}

        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            reportIfNotCanceled {
                Timber.d("MqttConnection: connected to $serverURI (reconnect=$reconnect)")
                state = State.RUNNING
                MqttConnectionEvent.Connected(this@MqttConnection, reconnect)
            }
        }
    }

    init {
        mqttClient.setCallback(mqttCallback)
        connectionPool.setConnection(broker.id, this)
    }

    fun start() {
        launchIfNotCanceled {
            Timber.d("MqttConnection: connecting to $broker")

            execSafely(MqttConnectionEvent::FailedToConnect) {
                mqttClient.connect(
                    MqttConnectOptions().apply {
                        isAutomaticReconnect = true
                        isCleanSession = false
                    }
                )
            }
        }
    }

    fun restart() {
        launchIfNotCanceled {
            Timber.d("MqttConnection: reconnecting to $broker")

            execSafely(MqttConnectionEvent::FailedToConnect) {
                mqttClient.reconnect()
            }
        }
    }

    fun stop(event: MqttConnectionEventGenerator? = null) {
        if (isCanceled)
            return

        Timber.d("MqttConnection: canceling broker connection: $broker")
        state = State.CANCELED
        mqttClient.setCallback(null)
        subscriptionManager.clear()
        connectionScope.launch {
            try {
                mqttClient.disconnect()
            } catch (_: Exception) {
            } finally {
                eventBus.send(
                    event?.invoke(this@MqttConnection, null)
                        ?: MqttConnectionEvent.LostConnection(
                            this@MqttConnection,
                            null
                        )
                )
                connectionJob.cancel()
            }
        }
    }

    fun publish(tile: Tile, payload: String) {
        launchIfNotCanceled {
            if (tile.publishTopic.isNotEmpty()) {
                Timber.d("MqttConnection: publishing payload to topic ${tile.publishTopic}: $payload")
                mqttClient.publish(
                    tile.publishTopic,
                    payload.toByteArray(),
                    tile.qos,
                    tile.retained
                )
            }
        }
    }

    private enum class State {
        RUNNING, DISCONNECTED, CANCELED
    }
}