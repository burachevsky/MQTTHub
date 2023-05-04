package com.github.burachevsky.mqtthub.common.notification

object NotificationId {
    @Volatile
    private var id = 0

    fun next(): Int {
        return ++id
    }
}
