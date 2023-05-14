package com.github.burachevsky.mqtthub.core.ui.notification

object NotificationId {
    @Volatile
    private var id = 0

    val payloadUpdatesSummary = next()

    fun next(): Int {
        return ++id
    }
}
