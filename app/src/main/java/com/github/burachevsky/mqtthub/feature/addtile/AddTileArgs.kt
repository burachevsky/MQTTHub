package com.github.burachevsky.mqtthub.feature.addtile

import android.os.Bundle
import androidx.core.os.bundleOf

const val DASHBOARD_ID = "dashboardId"
const val TILE_ID = "tileId"
const val DASHBOARD_POSITION = "dashboardPosition"

data class AddTileArgs(
    val dashboardId: Long,
    val tileId: Long,
    val dashboardPosition: Int,
) {

    fun toBundle(): Bundle {
        return bundleOf(
            DASHBOARD_ID to dashboardId,
            TILE_ID to tileId,
            DASHBOARD_POSITION to dashboardPosition,
        )
    }

    companion object {
        fun fromBundle(bundle: Bundle): AddTileArgs {
            return AddTileArgs(
                dashboardId = bundle.getLong(DASHBOARD_ID),
                tileId = bundle.getLong(TILE_ID),
                dashboardPosition = bundle.getInt(DASHBOARD_POSITION),
            )
        }
    }
}
