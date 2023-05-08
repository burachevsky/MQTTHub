package com.github.burachevsky.mqtthub.core.database

import com.github.burachevsky.mqtthub.core.database.dao.BrokerDao
import com.github.burachevsky.mqtthub.core.database.dao.CurrentIdsDao
import com.github.burachevsky.mqtthub.core.database.dao.DashboardDao
import com.github.burachevsky.mqtthub.core.database.dao.TileDao
import dagger.Module
import dagger.Provides

@Module
class DaoModule {

    @Provides
    fun provideBrokerDao(db: AppDatabase): BrokerDao = db.brokerDao()

    @Provides
    fun provideTileDao(db: AppDatabase): TileDao = db.tileDao()

    @Provides
    fun provideDashboardDao(db: AppDatabase): DashboardDao = db.dashboardDao()

    @Provides
    fun provideCurrentIdsDao(db: AppDatabase): CurrentIdsDao = db.currentIdsDao()
}