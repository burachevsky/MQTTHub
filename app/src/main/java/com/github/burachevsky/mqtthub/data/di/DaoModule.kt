package com.github.burachevsky.mqtthub.data.di

import com.github.burachevsky.mqtthub.data.AppDatabase
import com.github.burachevsky.mqtthub.data.dao.BrokerDao
import com.github.burachevsky.mqtthub.data.dao.DashboardDao
import com.github.burachevsky.mqtthub.data.dao.TileDao
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
}