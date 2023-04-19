package com.github.burachevsky.mqtthub.feature.tiledetails.text

import dagger.Module
import dagger.Provides

@Module
class TextTileDetailsModule(fragment: TextTileDetailsFragment) {

    private val args = TextTileDetailsFragmentArgs.fromBundle(fragment.requireArguments())

    @Provides
    fun provideTileId(): Long {
        return args.tileId
    }
}