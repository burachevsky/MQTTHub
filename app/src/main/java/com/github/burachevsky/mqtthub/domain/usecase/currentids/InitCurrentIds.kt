package com.github.burachevsky.mqtthub.domain.usecase.currentids

import com.github.burachevsky.mqtthub.data.repository.CurrentIdsRepository
import javax.inject.Inject

class InitCurrentIds @Inject constructor(
    private val currentIdsRepository: CurrentIdsRepository
) {

    suspend operator fun invoke() {
        return currentIdsRepository.init()
    }
}