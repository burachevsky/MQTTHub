package com.github.burachevsky.mqtthub.core.model

data class StringPayload(override val stringValue: String = ""): Payload
