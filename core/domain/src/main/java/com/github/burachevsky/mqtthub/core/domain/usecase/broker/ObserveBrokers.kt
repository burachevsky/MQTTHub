package com.github.burachevsky.mqtthub.core.domain.usecase.broker

import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.core.model.Broker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBrokers @Inject constructor(
    private val brokerRepository: BrokerRepository
) {

    operator fun invoke(): Flow<List<Broker>> {
        return brokerRepository.observeBrokers()
    }
}