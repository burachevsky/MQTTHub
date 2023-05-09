package com.github.burachevsky.mqtthub.core.domain.usecase.currentids

import com.github.burachevsky.mqtthub.core.data.repository.CurrentIdsRepository
import com.github.burachevsky.mqtthub.core.model.CurrentIds
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentIds @Inject constructor(
    private val currentIdsRepository: CurrentIdsRepository
) {

    operator fun invoke(): Flow<CurrentIds> {
        return currentIdsRepository.observeCurrentIds()
    }
}
