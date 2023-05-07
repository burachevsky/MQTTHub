package com.github.burachevsky.mqtthub.core.ui.dialog.entertext

import dagger.Subcomponent

@Subcomponent(modules = [EnterTextModule::class])
interface EnterTextComponent {

    fun inject(fragment: EnterTextDialogFragment)

    interface Provider {
        fun enterTextComponent(module: EnterTextModule): EnterTextComponent
    }
}