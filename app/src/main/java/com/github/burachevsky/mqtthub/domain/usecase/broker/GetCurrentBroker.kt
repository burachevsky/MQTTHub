package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.repository.BrokerRepository
import javax.inject.Inject

class GetCurrentBroker @Inject constructor(
    private val brokerRepository: BrokerRepository,
) {

    suspend operator fun invoke(): Broker? {
        return brokerRepository.getCurrentBroker()
    }
}