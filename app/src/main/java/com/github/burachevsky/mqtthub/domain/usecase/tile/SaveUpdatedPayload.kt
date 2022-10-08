package com.github.burachevsky.mqtthub.domain.usecase.tile

import com.github.burachevsky.mqtthub.data.repository.TileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(FlowPreview::class)
@Singleton
class SaveUpdatedPayload @Inject constructor(
    private val tileRepository: TileRepository
) {
    private val payloadQueues = ConcurrentHashMap<String, MutableSharedFlow<PayloadUpdate>>()

    suspend operator fun invoke(payloadUpdate: PayloadUpdate) {
        payloadQueues[payloadUpdate.subscribeTopic]
            ?.emit(payloadUpdate)
            ?: run {
                addNewTopicAndEmit(payloadUpdate)
            }
    }

    private suspend fun update(payloadUpdate: PayloadUpdate) {
        payloadUpdate.let {
            Timber.d("saving payload: ${it.payload}, ${it.subscribeTopic}")
            tileRepository.updatePayload(it.brokerId, it.subscribeTopic, it.payload)
        }
    }

    private suspend fun addNewTopicAndEmit(payloadUpdate: PayloadUpdate) {
        val flow = MutableSharedFlow<PayloadUpdate>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        CoroutineScope(Dispatchers.IO).launch {
            flow.debounce(100)
                .collect { update(it) }
        }

        payloadQueues[payloadUpdate.subscribeTopic] = flow

        flow.emit(payloadUpdate)
    }
}

data class PayloadUpdate(
    val brokerId: Long,
    val subscribeTopic: String,
    val payload: String
)