package com.github.burachevsky.mqtthub.feature.addtile

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AddTileModule(fragment: AddTileFragment<*>) {

    private val args = AddTileArgs.fromBundle(fragment.requireArguments())

    @Provides
    @Named(DASHBOARD_ID)
    fun provideDashboardId(): Long {
        return args.dashboardId
    }

    @Provides
    @Named(TILE_ID)
    fun provideTileId(): Long {
        return args.tileId
    }

    @Provides
    @Named(DASHBOARD_POSITION)
    fun provideDashboardPosition(): Int {
        return args.dashboardPosition
    }
}
