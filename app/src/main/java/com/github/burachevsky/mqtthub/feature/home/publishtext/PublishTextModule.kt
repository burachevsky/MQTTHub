package com.github.burachevsky.mqtthub.feature.home.publishtext

import dagger.Module
import dagger.Provides

@Module
class PublishTextModule(fragment: PublishTextDialogFragment) {

    private val args = PublishTextDialogFragmentArgs.fromBundle(fragment.requireArguments())

    @Provides
    fun provideArgs(): PublishTextDialogFragmentArgs {
        return args
    }
}