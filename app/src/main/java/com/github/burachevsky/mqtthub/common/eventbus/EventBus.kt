package com.github.burachevsky.mqtthub.common.eventbus

import com.github.burachevsky.mqtthub.common.effect.UIEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class EventBus() {

    private val events = MutableSharedFlow<UIEffect>()

    suspend fun send(event: UIEffect) {
        events.emit(event)
    }

    fun <T : UIEffect> subscribe(
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

    inline fun <reified T : UIEffect> subscribe(
        scope: CoroutineScope,
        noinline handler: suspend (T) -> Unit
    ) {
        subscribe(scope, T::class, handler)
    }
}