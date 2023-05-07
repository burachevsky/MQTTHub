package com.github.burachevsky.mqtthub.feature.connection

import com.github.burachevsky.mqtthub.core.ui.di.ServiceScope
import dagger.Subcomponent


@ServiceScope
@Subcomponent
interface BrokerConnectionServiceComponent {

    fun inject(service: BrokerConnectionService)

    interface Provider {
        fun brokerConnectionServiceComponent(): BrokerConnectionServiceComponent
    }
}