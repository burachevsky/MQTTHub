package com.github.burachevsky.mqtthub.core.db

import com.github.burachevsky.mqtthub.core.db.dao.BrokerDao
import com.github.burachevsky.mqtthub.core.db.dao.CurrentIdsDao
import com.github.burachevsky.mqtthub.core.db.dao.DashboardDao
import com.github.burachevsky.mqtthub.core.db.dao.TileDao
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