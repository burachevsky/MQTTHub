package com.github.burachevsky.mqtthub.feature.addbroker

import android.os.Parcelable
import com.github.burachevsky.mqtthub.data.entity.DomainBroker
import kotlinx.parcelize.Parcelize

@Parcelize
data class BrokerInfo(
    val id: Long,
    val name: String,
    val address: String,
    val port: String,
    val clientId: String,
) : Parcelable {

    companion object {
        fun map(domain: DomainBroker): BrokerInfo {
            return BrokerInfo(
                id = domain.id,
                name = domain.name  ,
                address = domain.address,
                port = domain.port,
                clientId = domain.clientId
            )
        }
    }
}