package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.core.model.Broker
import javax.inject.Inject

class AddBroker @Inject constructor(
    private val brokerRepository: BrokerRepository
) {

    suspend operator fun invoke(broker: Broker): Broker {
        return brokerRepository.insertBroker(broker)
    }
}