package com.github.burachevsky.mqtthub.core.data.mapper

import com.github.burachevsky.mqtthub.core.db.entity.BrokerEntity
import com.github.burachevsky.mqtthub.core.model.Broker

fun Broker.asEntity(): BrokerEntity {
    return BrokerEntity(
        id = id,
        name = name,
        address = address,
        port = port,
        clientId = clientId,
    )
}

fun BrokerEntity.asModel(): Broker {
    return Broker(
        id = id,
        name = name,
        address = address,
        port = port,
        clientId = clientId,
    )
}