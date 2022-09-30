package com.github.burachevsky.mqtthub.feature.home

import android.os.Parcelable
import com.github.burachevsky.mqtthub.data.entity.Tile
import kotlinx.parcelize.Parcelize

@Parcelize
data class UITile(
    val id: Long,
    val name: String,
    val subscribeTopic: String,
    val qos: Int = 0,
    val retained: Boolean = false,
    val type: Type = Type.TEXT,
): Parcelable {

    enum class Type {
        TEXT
    }

    companion object {
        fun map(tile: Tile): UITile {
            return UITile(
                id = tile.id,
                name = tile.name,
                subscribeTopic = tile.subscribeTopic,
                qos = tile.qos,
                retained = tile.retained,
                type = Type.valueOf(tile.type)
            )
        }
    }
}
