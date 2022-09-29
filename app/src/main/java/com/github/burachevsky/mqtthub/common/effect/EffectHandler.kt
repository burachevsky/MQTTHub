package com.github.burachevsky.mqtthub.common.effect

interface EffectHandler {
    fun handleEffect(effect: UIEffect): Boolean
}