package com.github.burachevsky.mqtthub.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.burachevsky.mqtthub.core.database.dao.BrokerDao
import com.github.burachevsky.mqtthub.core.database.dao.CurrentIdsDao
import com.github.burachevsky.mqtthub.core.database.dao.DashboardDao
import com.github.burachevsky.mqtthub.core.database.dao.TileDao
import com.github.burachevsky.mqtthub.core.database.entity.BrokerEntity
import com.github.burachevsky.mqtthub.core.database.entity.CurrentIdsEntity
import com.github.burachevsky.mqtthub.core.database.entity.DashboardEntity
import com.github.burachevsky.mqtthub.core.database.entity.TileEntity

@Database(
    version = 1,
    entities = [
        BrokerEntity::class,
        TileEntity::class,
        DashboardEntity::class,
        CurrentIdsEntity::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun brokerDao(): BrokerDao
    abstract fun tileDao(): TileDao
    abstract fun dashboardDao(): DashboardDao
    abstract fun currentIdsDao(): CurrentIdsDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context)
                        .also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, APP_DB_NAME)
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
        }
    }
}