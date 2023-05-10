package com.github.burachevsky.mqtthub.core.domain.usecase.tile

import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import com.github.burachevsky.mqtthub.core.model.TopicUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveTopicUpdates @Inject constructor(
    private val tileRepository: TileRepository
) {

    operator fun invoke(): Flow<TopicUpdate> {
        val subscriptionTopicsSet = tileRepository.observeTopicUpdates()

        return flow {
            var previousSet = emptySet<String>()

            subscriptionTopicsSet.collect { set ->
                val addedTopics = set.minus(previousSet)
                if (addedTopics.isNotEmpty()) {
                    emit(TopicUpdate.TopicsAdded(addedTopics))
                }

                val removedTopics = previousSet.minus(set)
                if (removedTopics.isNotEmpty()) {
                    emit(TopicUpdate.TopicsRemoved(removedTopics))
                }

                previousSet = set
            }
        }
    }
}