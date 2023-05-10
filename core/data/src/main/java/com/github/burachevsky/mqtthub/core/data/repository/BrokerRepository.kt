package com.github.burachevsky.mqtthub.core.data.repository

import com.github.burachevsky.mqtthub.core.model.Broker
import kotlinx.coroutines.flow.Flow

interface BrokerRepository {

    fun observeBrokers(): Flow<List<Broker>>

    fun observeBroker(id: Long): Flow<Broker>

    suspend fun insertBroker(broker: Broker): Broker

    suspend fun updateBroker(broker: Broker)

    suspend fun deleteBroker(id: Long)
}