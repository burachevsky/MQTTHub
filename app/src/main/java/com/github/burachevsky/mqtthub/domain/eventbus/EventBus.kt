package com.github.burachevsky.mqtthub.domain.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class EventBus() {

    private val events = MutableSharedFlow<AppEvent>()

    suspend fun send(event: AppEvent) {
        events.emit(event)
    }

    fun <T : AppEvent> subscribe(
        scope: CoroutineScope,
        eventType: KClass<T>,
        handler: suspend (T) -> Unit
    ) {
        scope.launch {
            events.collect { event ->
                @Suppress("UNCHECKED_CAST")
                if (eventType.java.isAssignableFrom(event::class.java)) {
                    handler(event as T)
                }
            }
        }
    }

    inline fun <reified T : AppEvent> subscribe(
        scope: CoroutineScope,
        noinline handler: suspend (T) -> Unit
    ) {
        subscribe(scope, T::class, handler)
    }
}

