package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import javax.inject.Inject

class DeleteBroker @Inject constructor(
    private val brokerRepository: BrokerRepository
) {

    suspend operator fun invoke(id: Long) {
        return brokerRepository.deleteBroker(id)
    }
}