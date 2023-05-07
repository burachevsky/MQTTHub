package com.github.burachevsky.mqtthub.core.data

import com.github.burachevsky.mqtthub.core.data.repository.BrokerRepository
import com.github.burachevsky.mqtthub.core.data.repository.CurrentIdsRepository
import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.data.repository.FileRepository
import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import com.github.burachevsky.mqtthub.core.data.repository.impl.BrokerRepositoryImpl
import com.github.burachevsky.mqtthub.core.data.repository.impl.CurrentIdsRepositoryImpl
import com.github.burachevsky.mqtthub.core.data.repository.impl.DashboardRepositoryImpl
import com.github.burachevsky.mqtthub.core.data.repository.impl.FileRepositoryImpl
import com.github.burachevsky.mqtthub.core.data.repository.impl.TileRepositoryImpl
import com.github.burachevsky.mqtthub.core.database.DaoModule
import com.github.burachevsky.mqtthub.core.database.DatabaseModule
import dagger.Binds
import dagger.Module

@Module(
    includes = [
        DatabaseModule::class,
        DaoModule::class,
    ]
)
abstract class DataModule {

    @Binds
    abstract fun bindBrokerRepository(impl: BrokerRepositoryImpl): BrokerRepository

    @Binds
    abstract fun bindTileRepository(impl: TileRepositoryImpl): TileRepository

    @Binds
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository

    @Binds
    abstract fun bindCurrentIdsRepository(impl: CurrentIdsRepositoryImpl): CurrentIdsRepository

    @Binds
    abstract fun bindFileRepository(impl: FileRepositoryImpl): FileRepository
}