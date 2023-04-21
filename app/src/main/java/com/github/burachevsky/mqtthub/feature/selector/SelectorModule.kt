package com.github.burachevsky.mqtthub.feature.selector

import dagger.Module
import dagger.Provides

@Module
class SelectorModule(fragment: SelectorDialogFragment) {

    private val config = SelectorDialogFragmentArgs
        .fromBundle(fragment.requireArguments())
        .config

    @Provides
    fun provideConfig(): SelectorConfig {
        return config
    }
}