package com.github.burachevsky.mqtthub.core.ui.dialog.selector

import com.github.burachevsky.mqtthub.core.ui.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [SelectorModule::class])
interface SelectorComponent {

    fun inject(fragment: SelectorDialogFragment)

    interface Provider {
        fun selectorComponent(module: SelectorModule): SelectorComponent
    }
}