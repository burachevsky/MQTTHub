package com.github.burachevsky.mqtthub.feature.home.addtile.switch

import dagger.Module
import dagger.Provides

@Module
class AddSwitchModule(fragment: AddSwitchFragment) {

    private val args = AddSwitchFragmentArgs.fromBundle(fragment.requireArguments())

    @Provides
    fun provideArgs(): AddSwitchFragmentArgs {
        return args
    }
}