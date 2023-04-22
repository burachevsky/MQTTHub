package com.github.burachevsky.mqtthub.domain.connection

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

internal class SubscriptionManager(
    private val connection: BrokerConnection,
) {

    private val subscriptions = ConcurrentHashMap<String, MutableSharedFlow<MqttMessage>>()

    fun subscribeIfHaveNotAlready(topics: List<String>) {
        val newTopics = topics.filter { it.isNotEmpty() && !subscriptions.containsKey(it) }
            .distinct()

        if (newTopics.isEmpty()) return

        val topicsString = newTopics.joinToString(", ")
        Timber.i("BrokerConnection: subscribing to topics [$topicsString]")

        newTopics.forEach { topic ->
            subscriptions[topic] = createMessageFlow()
            launchMessageCollecting(topic)
        }

        if (newTopics.isNotEmpty()) {
            connection.mqttClient.subscribe(newTopics.toTypedArray())
        }
    }

    fun subscribeIfHaveNotAlready(topic: String) {
        subscribeIfHaveNotAlready(listOf(topic))
    }

    fun unsubscribe(topics: List<String>) {
        val distinctTopics = topics.filter { it.isNotEmpty() }
            .distinct()

        val topicsString = distinctTopics.joinToString(", ")
        Timber.i("BrokerConnection: unsubscribing from topics [$topicsString]")

        distinctTopics.forEach(subscriptions::remove)

        if (distinctTopics.isNotEmpty()) {
            connection.mqttClient.unsubscribe(distinctTopics.toTypedArray())
        }
    }

    fun unsubscribe(topic: String) {
        unsubscribe(listOf(topic))
    }

    suspend fun messageArrived(topic: String, message: MqttMessage) {
        subscriptions[topic]?.emit(message)
    }

    fun clear() {
        subscriptions.clear()
    }

    private fun createMessageFlow(): MutableSharedFlow<MqttMessage> {
        return MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    private fun launchMessageCollecting(topic: String) = with(connection) {
        launchIfNotCanceled {
            subscriptions[topic]?.collect { message ->
                reportIfNotCanceled {
                    MqttMessageArrived(connection, topic, String(message.payload))
                }
            }
        }
    }
}