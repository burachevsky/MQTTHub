package com.github.burachevsky.mqtthub.core.model

data class Broker(
    val id: Long = 0,
    val name: String = "",
    val address: String = "",
    val port: String = "",
    val clientId: String = "",
) {

    fun getServerAddress(): String {
        return "tcp://$address:$port"
    }
}
