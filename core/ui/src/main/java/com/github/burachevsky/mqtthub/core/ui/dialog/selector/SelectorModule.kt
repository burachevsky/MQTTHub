package com.github.burachevsky.mqtthub.core.ui.dialog.selector

import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import com.github.burachevsky.mqtthub.core.ui.ext.parcelable
import dagger.Module
import dagger.Provides

@Module
class SelectorModule(fragment: SelectorDialogFragment) {

    private val config: SelectorConfig = fragment.requireArguments()
        .parcelable(NavArg.CONFIG)

    @Provides
    fun provideConfig(): SelectorConfig {
        return config
    }
}