package com.github.burachevsky.mqtthub.domain.usecase.currentids

import com.github.burachevsky.mqtthub.data.repository.CurrentIdsRepository
import javax.inject.Inject

class UpdateCurrentBroker @Inject constructor(
    private val currentIdsRepository: CurrentIdsRepository
) {

    suspend operator fun invoke(brokerId: Long?) {
        return currentIdsRepository.updateCurrentBroker(brokerId)
    }
}