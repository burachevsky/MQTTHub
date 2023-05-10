package com.github.burachevsky.mqtthub.core.domain.usecase.currentids

import com.github.burachevsky.mqtthub.core.data.repository.CurrentIdsRepository
import javax.inject.Inject

class UpdateCurrentBroker @Inject constructor(
    private val currentIdsRepository: CurrentIdsRepository
) {

    suspend operator fun invoke(brokerId: Long?) {
        return currentIdsRepository.updateCurrentBroker(brokerId)
    }
}