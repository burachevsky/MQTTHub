package com.github.burachevsky.mqtthub.feature.home.addtile.button

import dagger.Module
import dagger.Provides

@Module
class AddButtonTileModule(fragment: AddButtonTileFragment) {

    private val args = AddButtonTileFragmentArgs.fromBundle(fragment.requireArguments())

    @Provides
    fun provideArgs(): AddButtonTileFragmentArgs {
        return args
    }
}