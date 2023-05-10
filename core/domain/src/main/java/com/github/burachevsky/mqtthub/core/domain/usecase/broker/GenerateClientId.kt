package com.github.burachevsky.mqtthub.core.domain.usecase.broker

import javax.inject.Inject
import kotlin.random.Random

class GenerateClientId @Inject constructor() {

    operator fun invoke(): String {
        return Random(System.currentTimeMillis())
            .nextInt(1000000, 10000000)
            .toString()
    }
}