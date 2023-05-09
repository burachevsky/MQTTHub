package com.github.burachevsky.mqtthub.core.data.repository.impl

import com.github.burachevsky.mqtthub.core.data.mapper.asEntity
import com.github.burachevsky.mqtthub.core.data.mapper.asModel
import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.core.data.repository.CurrentIdsRepository
import com.github.burachevsky.mqtthub.core.db.dao.BrokerDao
import com.github.burachevsky.mqtthub.core.model.Broker
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class BrokerRepositoryImpl @Inject constructor(
    private val brokerDao: BrokerDao,
    private val currentIdsRepository: CurrentIdsRepository,
) : BrokerRepository {

    override suspend fun getBrokers(): List<Broker> {
        return brokerDao.getAll().map { it.asModel() }
    }

    override fun observeBrokers(): Flow<List<Broker>> {
        return brokerDao.observeBrokers().map { entities ->
            entities.map { it.asModel() }
        }
    }

    override fun observeCurrentBroker(): Flow<Broker?> {
        return channelFlow {
            coroutineScope {
                var job: Job? = null
                var currentId: Long?

                currentIdsRepository
                    .observeCurrentBrokerId()
                    .distinctUntilChanged()
                    .collect { id ->
                        job?.cancel()

                        currentId = id

                        if (id == null) {
                            send(null)
                        } else {
                            job = launch {
                                brokerDao.observeBroker(id)
                                    .onEach { broker ->
                                        if (isActive && id == currentId) {
                                            send(broker.asModel())
                                        }
                                    }
                                    .collect()
                            }
                        }
                    }
            }
        }
    }

    override suspend fun getBroker(id: Long): Broker {
        return brokerDao.getById(id)!!.asModel()
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