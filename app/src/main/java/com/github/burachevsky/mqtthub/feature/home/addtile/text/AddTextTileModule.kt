package com.github.burachevsky.mqtthub.feature.home.addtile.text

import dagger.Module
import dagger.Provides

@Module
class AddTextTileModule(fragment: AddTextTileFragment) {

    private val args = AddTextTileFragmentArgs.fromBundle(fragment.requireArguments())

    @Provides
    fun provideArgs(): AddTextTileFragmentArgs {
        return args
    }
}