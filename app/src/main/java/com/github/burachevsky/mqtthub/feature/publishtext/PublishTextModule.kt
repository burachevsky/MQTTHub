package com.github.burachevsky.mqtthub.feature.publishtext

import com.github.burachevsky.mqtthub.feature.publishtext.PublishTextDialogFragmentArgs
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