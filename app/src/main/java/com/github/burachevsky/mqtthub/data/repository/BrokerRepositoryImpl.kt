package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.dao.BrokerDao
import com.github.burachevsky.mqtthub.data.entity.Broker
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class BrokerRepositoryImpl @Inject constructor(
    private val brokerDao: BrokerDao,
    private val currentIdsRepository: CurrentIdsRepository,
) : BrokerRepository {

    override suspend fun getBrokers(): List<Broker> {
        return brokerDao.getAll()
    }

    override fun observeBrokers(): Flow<List<Broker>> {
        return brokerDao.observeBrokers()
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
                                    .collect { broker ->
                                        if (isActive && id == currentId) {
                                            send(broker)
                                        }
                                    }
                            }
                        }
                    }
            }
        }
    }

    override suspend fun getBroker(id: Long): Broker {
        return brokerDao.getById(id)!!
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