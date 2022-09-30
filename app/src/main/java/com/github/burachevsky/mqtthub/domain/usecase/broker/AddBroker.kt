package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.repository.BrokerRepository
import javax.inject.Inject

class AddBroker @Inject constructor(
    private val brokerRepository: BrokerRepository
) {

    suspend operator fun invoke(broker: Broker): Broker {
        return brokerRepository.insertBroker(broker)
    }
}