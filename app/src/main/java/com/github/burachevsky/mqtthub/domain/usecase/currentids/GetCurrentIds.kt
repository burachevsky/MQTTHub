package com.github.burachevsky.mqtthub.domain.usecase.currentids

import com.github.burachevsky.mqtthub.data.entity.CurrentIds
import com.github.burachevsky.mqtthub.data.repository.CurrentIdsRepository
import javax.inject.Inject

class GetCurrentIds @Inject constructor(
    private val currentIdsRepository: CurrentIdsRepository,
) {

    suspend operator fun invoke(): CurrentIds {
        return currentIdsRepository.getCurrentIds()
    }
}