package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.core.model.Broker
import javax.inject.Inject

class GetBroker @Inject constructor(
    private val brokerRepository: BrokerRepository
) {
    suspend operator fun invoke(id: Long): Broker {
        return brokerRepository.getBroker(id)
    }
}