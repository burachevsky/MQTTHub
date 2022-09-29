package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.DomainBroker

interface BrokerRepository {

    suspend fun getBrokers(): List<DomainBroker>

    suspend fun insertBroker(domainBroker: DomainBroker): DomainBroker

    suspend fun updateBroker(domainBroker: DomainBroker)

    suspend fun deleteBroker(id: Long)
}