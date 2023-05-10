package com.github.burachevsky.mqtthub.core.data.repository.impl

import com.github.burachevsky.mqtthub.core.data.mapper.asEntity
import com.github.burachevsky.mqtthub.core.data.mapper.asModel
import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.core.db.dao.BrokerDao
import com.github.burachevsky.mqtthub.core.model.Broker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BrokerRepositoryImpl @Inject constructor(
    private val brokerDao: BrokerDao
) : BrokerRepository {

    override fun observeBrokers(): Flow<List<Broker>> {
        return brokerDao.observeAll().map { entities ->
            entities.map { it.asModel() }
        }
    }

    override fun observeBroker(id: Long): Flow<Broker> {
        return brokerDao.observe(id).map { it.asModel() }
    }

    override suspend fun insertBroker(broker: Broker): Broker {
        val id = brokerDao.insert(broker.asEntity())
        return broker.copy(id = id)
    }

    override suspend fun updateBroker(broker: Broker) {
        return brokerDao.update(broker.asEntity())
    }

    override suspend fun deleteBroker(id: Long) {
        return brokerDao.delete(id)
    }
}