package com.github.burachevsky.mqtthub.feature.connection

import com.github.burachevsky.mqtthub.domain.connection.BrokerConnection
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent

data class TerminateBrokerConnection(
    val brokerId: Long
) : AppEvent

data class BrokerConnectionAction(
    val brokerId: Long,
    val action: suspend BrokerConnection.() -> Unit
) : AppEvent