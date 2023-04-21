package com.github.burachevsky.mqtthub.feature.brokers

import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent

data class BrokerDeleted(
    val brokerId: Long,
) : AppEvent