package com.github.burachevsky.mqtthub.feature.addbroker

import dagger.Module
import dagger.Provides

@Module
class AddBrokerModule(fragment: AddBrokerFragment) {
    private val args = AddBrokerFragmentArgs.fromBundle(fragment.requireArguments())

    @Provides
    fun provideArgs(): AddBrokerFragmentArgs {
        return args
    }
}