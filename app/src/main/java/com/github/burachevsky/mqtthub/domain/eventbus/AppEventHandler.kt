package com.github.burachevsky.mqtthub.domain.eventbus

interface AppEventHandler {
    fun handleEffect(effect: AppEvent): Boolean
}