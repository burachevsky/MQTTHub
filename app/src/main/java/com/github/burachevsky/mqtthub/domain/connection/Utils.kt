package com.github.burachevsky.mqtthub.domain.connection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

typealias BrokerConnectionEventGenerator = (BrokerConnection, Throwable) -> BrokerConnectionEvent

internal inline fun BrokerConnection.ifNotCanceled(block: () -> Unit) {
    if (!isCanceled) {
        block()
    }
}

internal inline fun BrokerConnection.reportIfNotCanceled(
    brokerEvent: () -> BrokerEvent
) {
    ifNotCanceled {
        val event = brokerEvent()
        connectionScope.launch {
            eventBus.send(event)
        }
    }
}

internal inline fun BrokerConnection.launchIfNotCanceled(
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    ifNotCanceled {
        connectionScope.launch {
            block()
        }
    }
}

internal suspend inline fun BrokerConnection.execSafely(
    errorEvent: BrokerConnectionEventGenerator = BrokerConnectionEvent::LostConnection,
    crossinline block: suspend BrokerConnection.() -> Unit
) {
    try {
        block()
    } catch (e: Throwable) {
        Timber.e(e)
        reportIfNotCanceled {
            errorEvent(this, e)
        }
    }
}