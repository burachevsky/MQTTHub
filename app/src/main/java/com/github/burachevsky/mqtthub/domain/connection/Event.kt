package com.github.burachevsky.mqtthub.domain.connection

import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent

interface BrokerEvent : AppEvent

sealed class BrokerConnectionEvent : BrokerEvent {
    abstract val connection: BrokerConnection

    data class Connected(
        override val connection: BrokerConnection,
        val reconnected: Boolean,
    ) : BrokerConnectionEvent()

    data class FailedToConnect(
        override val connection: BrokerConnection,
        val cause: Throwable?,
    ) : BrokerConnectionEvent()

    data class LostConnection(
        override val connection: BrokerConnection,
        val cause: Throwable?,
    ) : BrokerConnectionEvent()

    data class Terminated(
        override val connection: BrokerConnection,
        val cause: Throwable?,
    ) : BrokerConnectionEvent()
}

data class MqttMessageArrived(
    val connection: BrokerConnection,
    val topic: String,
    val message: String,
): BrokerEvent

data class NotifyPayloadUpdate(val notifyList: List<Tile>) : BrokerEvent