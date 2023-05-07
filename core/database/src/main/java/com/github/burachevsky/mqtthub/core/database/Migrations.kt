package com.github.burachevsky.mqtthub.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE tiles ADD COLUMN notify_payload_update INTEGER DEFAULT 0 NOT NULL"
        )
    }
}