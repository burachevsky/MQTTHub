package com.github.burachevsky.mqtthub.feature.addbroker

import com.github.burachevsky.mqtthub.common.effect.UIEffect

data class BrokerAdded(
    val brokerInfo: BrokerInfo
) : UIEffect

data class BrokerUpdated(
    val brokerInfo: BrokerInfo
) : UIEffect