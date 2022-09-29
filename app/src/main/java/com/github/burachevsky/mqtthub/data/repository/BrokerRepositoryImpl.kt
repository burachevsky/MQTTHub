package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.DomainBroker
import com.github.burachevsky.mqtthub.data.local.AppDatabase
import com.github.burachevsky.mqtthub.data.local.entity.Broker
import javax.inject.Inject

class BrokerRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : BrokerRepository {

    override suspend fun getBrokers(): List<DomainBroker> {
        return db.brokerDao()
            .getAllBrokers()
            .map {
                DomainBroker(
                    id = it.id,
                    name = it.name,
                    address = it.address,
                    port = it.port,
                    clientId = it.clientId
                )
            }
    }

    override suspend fun insertBroker(domainBroker: DomainBroker): DomainBroker {
        return db.brokerDao()
            .insertBroker(
                Broker(
                    name = domainBroker.name,
                    address = domainBroker.address,
                    port = domainBroker.port,
                    clientId = domainBroker.clientId
                )
            )
            .let { id ->
                domainBroker.copy(id = id)
            }
    }

    override suspend fun updateBroker(domainBroker: DomainBroker) {
        return db.brokerDao()
            .updateBroker(
                Broker(
                    name = domainBroker.name,
                    address = domainBroker.address,
                    port = domainBroker.port,
                    clientId = domainBroker.clientId
                ).also { it.id = domainBroker.id }
            )
    }

    override suspend fun deleteBroker(id: Long) {
        return db.brokerDao().deleteBroker(id)
    }
}