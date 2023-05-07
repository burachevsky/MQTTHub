package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.core.database.entity.broker.Broker
import javax.inject.Inject

class UpdateBroker @Inject constructor(
    private val brokerRepository: BrokerRepository
) {

    suspend operator fun invoke(broker: Broker) {
        return brokerRepository.updateBroker(broker)
    }
}