package com.github.burachevsky.mqtthub.feature.mqttservice

import com.github.burachevsky.mqtthub.core.ui.di.ServiceScope
import dagger.Subcomponent


@ServiceScope
@Subcomponent
interface MqttServiceComponent {

    fun inject(service: MqttService)

    interface Provider {
        fun mqttServiceComponent(): MqttServiceComponent
    }
}