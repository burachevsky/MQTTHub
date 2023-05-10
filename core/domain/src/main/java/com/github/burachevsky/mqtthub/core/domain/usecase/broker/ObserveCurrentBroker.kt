package com.github.burachevsky.mqtthub.core.domain.usecase.broker

import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.core.data.repository.CurrentIdsRepository
import com.github.burachevsky.mqtthub.core.model.Broker
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class ObserveCurrentBroker @Inject constructor(
    private val brokerRepository: BrokerRepository,
    private val currentIdsRepository: CurrentIdsRepository,
) {

    operator fun invoke(): Flow<Broker?> {
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
                                brokerRepository.observeBroker(id)
                                    .onEach { broker ->
                                        if (isActive && id == currentId) {
                                            send(broker)
                                        }
                                    }
                                    .collect()
                            }
                        }
                    }
            }
        }
    }
}