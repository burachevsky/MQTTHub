package com.github.burachevsky.mqtthub.data.di

import com.github.burachevsky.mqtthub.data.local.DatabaseModule
import com.github.burachevsky.mqtthub.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.data.repository.BrokerRepositoryImpl
import dagger.Binds
import dagger.Module

@Module(includes = [DatabaseModule::class])
abstract class LocalDataModule {

    @Binds
    abstract fun bindBrokerRepository(impl: BrokerRepositoryImpl): BrokerRepository
}