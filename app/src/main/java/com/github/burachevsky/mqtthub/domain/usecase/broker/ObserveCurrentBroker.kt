package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.repository.BrokerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentBroker @Inject constructor(
    private val brokerRepository: BrokerRepository
) {

    operator fun invoke(): Flow<Broker?> {
        return brokerRepository.observeCurrentBroker()
    }
}