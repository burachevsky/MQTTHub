package com.github.burachevsky.mqtthub.domain.usecase.tile

import com.github.burachevsky.mqtthub.data.entity.TopicUpdate
import com.github.burachevsky.mqtthub.data.repository.TileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTopicUpdates @Inject constructor(
    private val tileRepository: TileRepository
) {

    operator fun invoke(): Flow<TopicUpdate> {
        return tileRepository.observeTopicUpdates()
    }
}