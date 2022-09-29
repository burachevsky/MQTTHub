package com.github.burachevsky.mqtthub.data.entity

data class DomainBroker(
    val id: Long = 0,
    val name: String,
    val address: String,
    val port: String,
    val clientId: String,
)
