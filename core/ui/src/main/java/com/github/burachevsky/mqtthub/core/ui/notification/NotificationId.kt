package com.github.burachevsky.mqtthub.core.ui.notification

object NotificationId {
    @Volatile
    private var id = 0

    fun next(): Int {
        return ++id
    }
}
