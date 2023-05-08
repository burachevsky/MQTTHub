package com.github.burachevsky.mqtthub.core.mqtt

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import org.eclipse.paho.client.mqttv3.MqttMessage

internal data class TopicSubscription(
    val job: Job,
    val flow: MutableSharedFlow<MqttMessage>
)