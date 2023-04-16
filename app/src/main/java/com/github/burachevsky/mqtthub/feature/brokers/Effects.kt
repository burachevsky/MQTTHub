package com.github.burachevsky.mqtthub.feature.brokers

import com.github.burachevsky.mqtthub.common.effect.UIEffect

data class BrokerDeleted(
    val brokerId: Long,
) : UIEffect