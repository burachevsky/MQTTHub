package com.github.burachevsky.mqtthub.feature.addbroker

import android.os.Parcelable
import com.github.burachevsky.mqtthub.data.entity.Broker
import kotlinx.parcelize.Parcelize

@Parcelize
data class BrokerInfo(
    val id: Long,
    val name: String,
    val address: String,
    val port: String,
    val clientId: String,
) : Parcelable {

    fun getServerAddress(): String {
        return "tcp://$address:$port"
    }

    companion object {
        fun map(broker: Broker): BrokerInfo {
            return BrokerInfo(
                id = broker.id,
                name = broker.name  ,
                address = broker.address,
                port = broker.port,
                clientId = broker.clientId
            )
        }
    }
}