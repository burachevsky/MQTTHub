package com.github.burachevsky.mqtthub.common.ext

import com.github.burachevsky.mqtthub.data.entity.Broker

fun Broker.getServerAddress(): String {
    return "tcp://$address:$port"
}