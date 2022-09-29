package com.github.burachevsky.mqtthub.di

import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return EventBus()
    }
}