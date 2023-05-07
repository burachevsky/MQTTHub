package com.github.burachevsky.mqtthub.core.database

import android.content.Context
import androidx.room.*
import com.github.burachevsky.mqtthub.core.database.dao.BrokerDao
import com.github.burachevsky.mqtthub.core.database.dao.CurrentIdsDao
import com.github.burachevsky.mqtthub.core.database.dao.DashboardDao
import com.github.burachevsky.mqtthub.core.database.dao.TileDao
import com.github.burachevsky.mqtthub.core.database.entity.broker.Broker
import com.github.burachevsky.mqtthub.core.database.entity.current.CurrentIds
import com.github.burachevsky.mqtthub.core.database.entity.dashboard.Dashboard
import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile

@Database(
    version = 12,
    entities = [
        Broker::class,
        Tile::class,
        Dashboard::class,
        CurrentIds::class,
    ],
)
@TypeConverters(Converters::class)
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
                    ?: buildDatabase(
                        context
                    )
                        .also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java,
                    APP_DB_NAME
                )
                .addMigrations(MIGRATION_11_12)
                .build()
        }
    }
}