package com.github.burachevsky.mqtthub.feature.home.addtile

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AddTileModule(fragment: AddTileFragment<*>) {

    private val args = fragment.requireArguments()

    @Provides
    @Named(BROKER_ID)
    fun provideBrokerId(): Long {
        return args.getLong(BROKER_ID)
    }

    @Provides
    @Named(TILE_ID)
    fun provideTileId(): Long {
        return args.getLong(TILE_ID)
    }
}

const val BROKER_ID = "brokerId"
const val TILE_ID = "tileId"
