package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.Broker
import kotlinx.coroutines.flow.Flow

interface BrokerRepository {

    suspend fun getBrokers(): List<Broker>

    fun observeBrokers(): Flow<List<Broker>>

    fun observeCurrentBroker(): Flow<Broker?>

    suspend fun getBroker(id: Long): Broker

    suspend fun insertBroker(broker: Broker): Broker

    suspend fun updateBroker(broker: Broker)

    suspend fun deleteBroker(id: Long)
}