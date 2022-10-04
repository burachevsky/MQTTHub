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
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(FlowPreview::class)
@Singleton
class SaveUpdatedPayload @Inject constructor(
    private val tileRepository: TileRepository
) {
    private val payloadQueue = MutableSharedFlow<PayloadUpdate>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            payloadQueue
                .collect {
                    Timber.d("saving payload(topic: ${it.subscribeTopic}, payload: ${it.payload})")
                    tileRepository.updatePayload(it.brokerId, it.subscribeTopic, it.payload)
                }
        }
    }

    suspend operator fun invoke(payloadUpdate: PayloadUpdate) {
        payloadQueue.emit(payloadUpdate)
    }
}

data class PayloadUpdate(
    val brokerId: Long,
    val subscribeTopic: String,
    val payload: String
)