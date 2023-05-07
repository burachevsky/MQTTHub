package com.github.burachevsky.mqtthub.domain.usecase.tile

import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import com.github.burachevsky.mqtthub.core.database.entity.tile.TopicUpdate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTopicUpdates @Inject constructor(
    private val tileRepository: TileRepository
) {

    operator fun invoke(): Flow<TopicUpdate> {
        return tileRepository.observeTopicUpdates()
    }
}