package com.github.burachevsky.mqtthub.core.mqtt

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

typealias MqttConnectionEventGenerator = (MqttConnection, Throwable?) -> MqttConnectionEvent

internal inline fun MqttConnection.ifNotCanceled(block: () -> Unit) {
    if (!isCanceled) {
        block()
    }
}

internal inline fun MqttConnection.reportIfNotCanceled(
    mqttEvent: () -> MqttEvent
) {
    val event = mqttEvent()
    launchIfNotCanceled {
        eventBus.send(event)
    }
}

internal inline fun MqttConnection.launchIfNotCanceled(
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    ifNotCanceled {
        connectionScope.launch {
            execSafely {
                block()
            }
        }
    }
}

internal suspend inline fun MqttConnection.execSafely(
    errorEvent: MqttConnectionEventGenerator = MqttConnectionEvent::LostConnection,
    crossinline block: suspend MqttConnection.() -> Unit
) {
    try {
        block()
    } catch (e: Throwable) {
        Timber.e(e)
        ifNotCanceled {
            try {
                val event = errorEvent(this, e)
                connectionScope.launch {
                    eventBus.send(event)
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
    }
}