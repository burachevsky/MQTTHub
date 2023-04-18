package com.github.burachevsky.mqtthub.di

import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
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
    @Named(Name.MQTT_EVENT_BUS)
    fun provideMqttEventBus(): EventBus {
        return EventBus()
    }
}