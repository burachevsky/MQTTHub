package com.github.burachevsky.mqtthub.feature.selector

import com.github.burachevsky.mqtthub.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [SelectorModule::class])
interface SelectorComponent {

    fun inject(fragment: SelectorDialogFragment)
}