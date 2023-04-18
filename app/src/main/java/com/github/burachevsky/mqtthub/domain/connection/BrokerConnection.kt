package com.github.burachevsky.mqtthub.domain.connection

import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.ext.getServerAddress
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.entity.Tile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import timber.log.Timber

class BrokerConnection(
    val broker: Broker,
    val eventBus: EventBus,
) : MqttCallbackExtended {

    val connectionScope = CoroutineScope(Dispatchers.IO)

    private val subscriptionManager = SubscriptionManager(this)

    internal val mqttClient: MqttClient = MqttClient(
        broker.getServerAddress(),
        broker.clientId,
        MemoryPersistence()
    )

    private var state = State.DISCONNECTED

    val isRunning get() = state == State.RUNNING
    val isDisconnected get() = state == State.DISCONNECTED
    val isCanceled get() = state == State.CANCELED

    init {
        mqttClient.setCallback(this)
    }

    override fun connectionLost(cause: Throwable?) {
        Timber.e(cause)
        reportIfNotCanceled {
            Timber.i("BrokerConnection: lost connection: $cause")
            state = State.DISCONNECTED
            BrokerConnectionEvent.LostConnection(this@BrokerConnection, cause)
        }
    }

    override fun messageArrived(topic: String, message: MqttMessage) {
        launchIfNotCanceled {
            Timber.i("BrokerConnection: receiving message from topic $topic: $message")

            subscriptionManager.messageArrived(topic, message)
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        reportIfNotCanceled {
            Timber.i("BrokerConnection: connected to $serverURI (reconnect=$reconnect)")
            state = State.RUNNING
            BrokerConnectionEvent.Connected(this@BrokerConnection, reconnect)
        }
    }

    fun start() {
        launchIfNotCanceled {
            Timber.i("BrokerConnection: connecting to $broker")

            execSafely(BrokerConnectionEvent::FailedToConnect) {
                mqttClient.connect()
            }
        }
    }

    fun restart() {
        launchIfNotCanceled {
            Timber.i("BrokerConnection: reconnecting to $broker")

            execSafely(BrokerConnectionEvent::FailedToConnect) {
                mqttClient.reconnect()
            }
        }
    }

    fun stop() {
        Timber.i("BrokerConnection: canceling broker connection: $broker")
        state = State.CANCELED
        mqttClient.setCallback(null)
        subscriptionManager.clear()
        connectionScope.launch {
            try {
                mqttClient.disconnect()
            } catch (_: Exception) {
            } finally {
                cancel()
            }
        }
    }

    fun subscribe(topic: String) {
        launchIfNotCanceled {
            subscriptionManager.subscribeIfHaveNotAlready(topic)
        }
    }

    fun subscribe(topics: List<String>) {
        launchIfNotCanceled {
            subscriptionManager.subscribeIfHaveNotAlready(topics)
        }
    }

    fun unsubscribe(topic: String) {
        launchIfNotCanceled {
            subscriptionManager.unsubscribe(topic)
        }
    }

    fun unsubscribe(topics: List<String>) {
        launchIfNotCanceled {
            subscriptionManager.unsubscribe(topics)
        }
    }

    fun publish(tile: Tile, payload: String) {
        launchIfNotCanceled {
            Timber.i("BrokerConnection: publishing payload to topic ${tile.publishTopic}: $payload")
            mqttClient.publish(tile.publishTopic, payload.toByteArray(), tile.qos, tile.retained)
        }
    }

    private enum class State {
        RUNNING, DISCONNECTED, CANCELED
    }
}