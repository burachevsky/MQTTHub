package com.github.burachevsky.mqtthub.core.mqtt

import com.github.burachevsky.mqtthub.core.common.throttle
import com.github.burachevsky.mqtthub.core.model.TopicUpdate
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.ObserveTopicUpdates
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.UpdatePayloadAndGetTilesToNotify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

internal class SubscriptionManager(
    private val connection: MqttConnection,
    private val savePayloadAndGetTilesToNotify: UpdatePayloadAndGetTilesToNotify,
    private val observeTopicUpdates: ObserveTopicUpdates,
) {
    private val subscriptions = ConcurrentHashMap<String, TopicSubscription>()

    private var topicObserverJob: Job? = null

    init {
        connection.launchIfNotCanceled {
            connection.eventBus.subscribe<MqttConnectionEvent>(this) {
                when (it) {
                    is MqttConnectionEvent.Connected -> {
                        launchTopicObserver()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun launchTopicObserver() {
        topicObserverJob = connection.connectionScope.launch(Dispatchers.Default) {
            observeTopicUpdates().collect { update ->
                when (update) {
                    is TopicUpdate.TopicsAdded -> {
                        subscribe(update.set)
                    }

                    is TopicUpdate.TopicsRemoved -> {
                        unsubscribe(update.set)
                    }
                }
            }
        }
    }

    private suspend fun subscribe(topicsSet: Set<String>) {
        connection.execSafely {
            val topics = topicsSet
                .filter { it.isNotEmpty() }
                .toTypedArray()

            if (topics.isEmpty()) return@execSafely

            val topicsString = topics.joinToString(", ")
            Timber.d("SubscriptionManager: subscribing to topics [$topicsString]")

            mqttClient.subscribe(topics)

            topics.forEach { topic ->
                val messageFlow = createMessageFlow()

                subscriptions[topic] = TopicSubscription(
                    job = messageFlow.collectFromTopic(topic),
                    flow = messageFlow
                )
            }
        }
    }

    private suspend fun unsubscribe(topicsSet: Set<String>) {
        connection.execSafely {
            val topics = topicsSet
                .filter { it.isNotEmpty() }
                .toTypedArray()

            if (topics.isEmpty()) return@execSafely

            val topicsString = topics.joinToString(", ")
            Timber.d("SubscriptionManager: unsubscribing from topics [$topicsString]")

            mqttClient.unsubscribe(topics)

            topics.forEach { topic ->
                subscriptions.remove(topic)?.job?.cancel()
            }
        }
    }

    suspend fun messageArrived(topic: String, message: MqttMessage) {
        subscriptions[topic]?.flow?.emit(message)
    }

    fun clear() {
        Timber.d("SubscriptionManager: clear")
        topicObserverJob?.cancel()

        subscriptions.forEach { (_, subscription) ->
            subscription.job.cancel()
        }

        subscriptions.clear()
    }

    private fun createMessageFlow(): MutableSharedFlow<MqttMessage> {
        return MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    private suspend fun SharedFlow<MqttMessage>.collectFromTopic(
        topic: String
    ): Job {
        return connection.connectionScope.launch(Dispatchers.Default) {
            throttle(periodMillis = 16)
                .collect { message ->
                    val payload = String(message.payload)
                    val notifyList = savePayloadAndGetTilesToNotify(topic, payload)

                    if (notifyList.isNotEmpty()) {
                        connection.reportIfNotCanceled {
                            NotifyPayloadUpdate(notifyList)
                        }
                    }

                    connection.reportIfNotCanceled {
                        MqttMessageArrived(connection, topic, payload)
                    }
                }
        }
    }
}