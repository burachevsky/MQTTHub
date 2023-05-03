package com.github.burachevsky.mqtthub.domain.connection

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrokerConnectionPool @Inject constructor() {

    private val connections = ConcurrentHashMap<Long, BrokerConnection>()

    fun getConnection(brokerId: Long): BrokerConnection? {
        return connections[brokerId]
    }

    fun setConnection(brokerId: Long, connection: BrokerConnection?) {
        if (connection != null) {
            connections[brokerId] = connection
        } else {
            connections.remove(brokerId)
        }
    }
}