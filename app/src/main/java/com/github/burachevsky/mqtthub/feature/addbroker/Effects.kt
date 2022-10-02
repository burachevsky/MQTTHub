package com.github.burachevsky.mqtthub.feature.addbroker

import com.github.burachevsky.mqtthub.common.effect.UIEffect
import com.github.burachevsky.mqtthub.data.entity.Broker

data class BrokerAdded(
    val broker: Broker
) : UIEffect

data class BrokerEdited(
    val broker: Broker
) : UIEffect