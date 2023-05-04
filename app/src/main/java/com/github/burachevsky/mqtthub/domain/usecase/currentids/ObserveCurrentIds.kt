package com.github.burachevsky.mqtthub.domain.usecase.currentids

import com.github.burachevsky.mqtthub.data.entity.CurrentIds
import com.github.burachevsky.mqtthub.data.repository.CurrentIdsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentIds @Inject constructor(
    private val currentIdsRepository: CurrentIdsRepository
) {

    operator fun invoke(): Flow<CurrentIds> {
        return currentIdsRepository.observeCurrentIds()
    }
}
