package com.github.burachevsky.mqtthub.core.eventbus

interface AppEventHandler {
    fun handleEvent(effect: AppEvent): Boolean
}