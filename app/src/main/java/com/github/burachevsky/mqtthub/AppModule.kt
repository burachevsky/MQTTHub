package com.github.burachevsky.mqtthub

import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.eventbus.MQTT_EVENT_BUS
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return EventBus()
    }

    @Provides
    @Singleton
    @Named(MQTT_EVENT_BUS)
    fun provideMqttEventBus(): EventBus {
        return EventBus()
    }
}