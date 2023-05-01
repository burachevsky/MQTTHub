package com.github.burachevsky.mqtthub.feature.entertext

import dagger.Module
import dagger.Provides

@Module
class EnterTextModule(fragment: EnterTextDialogFragment) {

    private val args = EnterTextDialogFragmentArgs.fromBundle(fragment.requireArguments())

    @Provides
    fun provideArgs(): EnterTextDialogFragmentArgs {
        return args
    }
}