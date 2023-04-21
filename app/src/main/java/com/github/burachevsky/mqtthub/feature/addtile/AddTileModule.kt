package com.github.burachevsky.mqtthub.feature.addtile

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AddTileModule(fragment: AddTileFragment<*>) {

    private val args = fragment.requireArguments()

    @Provides
    @Named(DASHBOARD_ID)
    fun provideBrokerId(): Long {
        return args.getLong(DASHBOARD_ID)
    }

    @Provides
    @Named(TILE_ID)
    fun provideTileId(): Long {
        return args.getLong(TILE_ID)
    }

    @Provides
    @Named(DASHBOARD_POSITION)
    fun provideDashboardPosition(): Int {
        return args.getInt(DASHBOARD_POSITION)
    }
}

const val DASHBOARD_ID = "dashboardId"
const val TILE_ID = "tileId"
const val DASHBOARD_POSITION = "dashboardPosition"
