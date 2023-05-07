package com.github.burachevsky.mqtthub.core.ui.dialog.entertext

import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import com.github.burachevsky.mqtthub.core.ui.ext.parcelable
import dagger.Module
import dagger.Provides

@Module
class EnterTextModule(fragment: EnterTextDialogFragment) {

    private val args: EnterTextConfig = fragment.requireArguments().parcelable(NavArg.CONFIG)

    @Provides
    fun provideArgs(): EnterTextConfig {
        return args
    }
}