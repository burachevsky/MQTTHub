package com.github.burachevsky.mqtthub.feature.addtile

import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AddTileModule(fragment: AddTileFragment<*>) {

    private val args = AddTileArgs.fromBundle(fragment.requireArguments())

    @Provides
    @Named(NavArg.DASHBOARD_ID)
    fun provideDashboardId(): Long {
        return args.dashboardId
    }

    @Provides
    @Named(NavArg.TILE_ID)
    fun provideTileId(): Long {
        return args.tileId
    }

    @Provides
    @Named(NavArg.DASHBOARD_POSITION)
    fun provideDashboardPosition(): Int {
        return args.dashboardPosition
    }
}
