package com.github.burachevsky.mqtthub.domain.usecase.broker

import com.github.burachevsky.mqtthub.data.entity.BrokerWithTiles
import com.github.burachevsky.mqtthub.data.repository.BrokerRepository
import javax.inject.Inject

class GetBrokerWithTiles @Inject constructor(
    private val brokerRepository: BrokerRepository
) {
    suspend operator fun invoke(id: Long): BrokerWithTiles {
        return brokerRepository.getBrokerWithTiles(id)
    }
}