package com.github.burachevsky.mqtthub.domain.connection

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import org.eclipse.paho.client.mqttv3.MqttMessage

data class TopicSubscription(
    val job: Job,
    val flow: MutableSharedFlow<MqttMessage>
)