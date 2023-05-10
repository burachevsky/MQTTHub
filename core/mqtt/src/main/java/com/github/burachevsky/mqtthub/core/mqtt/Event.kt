package com.github.burachevsky.mqtthub.core.mqtt

import com.github.burachevsky.mqtthub.core.eventbus.AppEvent
import com.github.burachevsky.mqtthub.core.model.Tile

interface MqttEvent : AppEvent

sealed class MqttConnectionEvent : MqttEvent {
    abstract val connection: MqttConnection

    data class Connected(
        override val connection: MqttConnection,
        val reconnected: Boolean,
    ) : MqttConnectionEvent()

    data class FailedToConnect(
        override val connection: MqttConnection,
        val cause: Throwable?,
    ) : MqttConnectionEvent()

    data class LostConnection(
        override val connection: MqttConnection,
        val cause: Throwable?,
    ) : MqttConnectionEvent()

    data class Terminated(
        override val connection: MqttConnection,
        val cause: Throwable?,
    ) : MqttConnectionEvent()
}

data class MqttMessageArrived(
    val connection: MqttConnection,
    val topic: String,
    val message: String,
): MqttEvent

data class NotifyPayloadUpdate(val notifyList: List<Tile>) : MqttEvent