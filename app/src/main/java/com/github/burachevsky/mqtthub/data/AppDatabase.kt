package com.github.burachevsky.mqtthub.data

import android.content.Context
import androidx.room.*
import com.github.burachevsky.mqtthub.data.dao.BrokerDao
import com.github.burachevsky.mqtthub.data.dao.TileDao
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.entity.Tile

@Database(
    version = 7,
    entities = [
        Broker::class,
        Tile::class,
    ],
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun brokerDao(): BrokerDao
    abstract fun tileDao(): TileDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, APP_DB_NAME)
                .addMigrations(MIGRATION_6_7)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}