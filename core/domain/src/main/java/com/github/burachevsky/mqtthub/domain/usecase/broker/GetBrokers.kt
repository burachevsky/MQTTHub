package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.core.database.entity.broker.Broker
import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import javax.inject.Inject

class GetBrokers @Inject constructor(
    private val brokerRepository: BrokerRepository
) {

    suspend operator fun invoke(): List<Broker> {
        return brokerRepository.getBrokers()
    }
}