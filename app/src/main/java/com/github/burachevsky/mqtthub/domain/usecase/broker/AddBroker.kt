package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.data.entity.DomainBroker
import com.github.burachevsky.mqtthub.data.repository.BrokerRepository
import javax.inject.Inject

class AddBroker @Inject constructor(
    private val brokerRepository: BrokerRepository
) {

    suspend operator fun invoke(domainBroker: DomainBroker): DomainBroker {
        return brokerRepository.insertBroker(domainBroker)
    }
}