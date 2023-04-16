package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.Broker

interface BrokerRepository {

    suspend fun getBrokers(): List<Broker>

    suspend fun getBroker(id: Long): Broker

    suspend fun getCurrentBroker(): Broker?

    suspend fun insertBroker(broker: Broker): Broker

    suspend fun updateBroker(broker: Broker)

    suspend fun deleteBroker(id: Long)
}