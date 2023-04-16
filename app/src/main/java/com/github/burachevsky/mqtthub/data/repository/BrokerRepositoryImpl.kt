package com.github.burachevsky.mqtthub.data.repository

import androidx.room.Transaction
import com.github.burachevsky.mqtthub.data.dao.BrokerDao
import com.github.burachevsky.mqtthub.data.entity.Broker
import javax.inject.Inject

class BrokerRepositoryImpl @Inject constructor(
    private val brokerDao: BrokerDao
) : BrokerRepository {

    override suspend fun getBrokers(): List<Broker> {
        return brokerDao.getAll()
    }

    override suspend fun getBroker(id: Long): Broker {
        return brokerDao.getById(id)
    }

    @Transaction
    override suspend fun getCurrentBroker(): Broker? {
        return brokerDao.getFirst().firstOrNull()
    }

    override suspend fun insertBroker(broker: Broker): Broker {
        val id = brokerDao.insert(broker)
        return broker.copy(id = id)
    }

    override suspend fun updateBroker(broker: Broker) {
        return brokerDao.update(broker)
    }

    override suspend fun deleteBroker(id: Long) {
        return brokerDao.delete(id)
    }
}