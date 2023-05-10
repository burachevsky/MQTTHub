package com.github.burachevsky.mqtthub.core.domain.usecase.broker

import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.core.model.Broker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBroker @Inject constructor(
    private val brokerRepository: BrokerRepository
) {

    operator fun invoke(id: Long): Flow<Broker> {
        return brokerRepository.observeBroker(id)
    }
}