package com.github.burachevsky.mqtthub.core.mqtt

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttConnectionPool @Inject constructor() {

    private val connections = ConcurrentHashMap<Long, MqttConnection>()

    fun getConnection(brokerId: Long): MqttConnection? {
        return connections[brokerId]
    }

    internal fun setConnection(brokerId: Long, connection: MqttConnection?) {
        if (connection != null) {
            connections[brokerId] = connection
        } else {
            connections.remove(brokerId)
        }
    }
}