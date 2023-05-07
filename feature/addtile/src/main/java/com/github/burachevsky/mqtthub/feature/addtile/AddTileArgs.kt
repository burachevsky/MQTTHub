package com.github.burachevsky.mqtthub.feature.addtile

import android.os.Bundle
import androidx.core.os.bundleOf
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg

data class AddTileArgs(
    val dashboardId: Long,
    val tileId: Long,
    val dashboardPosition: Int,
) {

    fun toBundle(): Bundle {
        return bundleOf(
            NavArg.DASHBOARD_ID to dashboardId,
            NavArg.TILE_ID to tileId,
            NavArg.DASHBOARD_POSITION to dashboardPosition,
        )
    }

    companion object {
        fun fromBundle(bundle: Bundle): AddTileArgs {
            return AddTileArgs(
                dashboardId = bundle.getLong(NavArg.DASHBOARD_ID),
                tileId = bundle.getLong(NavArg.TILE_ID),
                dashboardPosition = bundle.getInt(NavArg.DASHBOARD_POSITION),
            )
        }
    }
}
