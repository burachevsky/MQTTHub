package com.github.burachevsky.mqtthub.feature.addbroker

import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent
import com.github.burachevsky.mqtthub.data.entity.Broker

data class BrokerAdded(
    val broker: Broker
) : AppEvent

data class BrokerEdited(
    val broker: Broker
) : AppEvent