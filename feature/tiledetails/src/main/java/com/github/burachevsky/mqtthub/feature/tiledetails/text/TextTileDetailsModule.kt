package com.github.burachevsky.mqtthub.feature.tiledetails.text

import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import dagger.Module
import dagger.Provides

@Module
class TextTileDetailsModule(fragment: TextTileDetailsFragment) {

    private val tileId: Long = fragment.requireArguments().getLong(NavArg.TILE_ID)

    @Provides
    fun provideTileId(): Long {
        return tileId
    }
}