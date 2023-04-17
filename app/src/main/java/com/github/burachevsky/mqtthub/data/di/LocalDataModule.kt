package com.github.burachevsky.mqtthub.data.di

import com.github.burachevsky.mqtthub.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.data.repository.BrokerRepositoryImpl
import com.github.burachevsky.mqtthub.data.repository.CurrentIdsRepository
import com.github.burachevsky.mqtthub.data.repository.CurrentIdsRepositoryImpl
import com.github.burachevsky.mqtthub.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.data.repository.DashboardRepositoryImpl
import com.github.burachevsky.mqtthub.data.repository.TileRepository
import com.github.burachevsky.mqtthub.data.repository.TileRepositoryImpl
import dagger.Binds
import dagger.Module

@Module(
    includes = [
        DatabaseModule::class,
        DaoModule::class,
    ]
)
abstract class LocalDataModule {

    @Binds
    abstract fun bindBrokerRepository(impl: BrokerRepositoryImpl): BrokerRepository

    @Binds
    abstract fun bindTileRepository(impl: TileRepositoryImpl): TileRepository

    @Binds
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository

    @Binds
    abstract fun bindCurrentIdsRepository(impl: CurrentIdsRepositoryImpl): CurrentIdsRepository
}