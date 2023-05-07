package com.github.burachevsky.mqtthub.feature.addbroker

import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import dagger.Module
import dagger.Provides

@Module
class AddBrokerModule(fragment: AddBrokerFragment) {
    private val brokerId = fragment.requireArguments().getLong(NavArg.BROKER_ID)

    @Provides
    fun provideBrokerId(): Long {
        return brokerId
    }
}