package com.github.burachevsky.mqtthub.feature.entertext

import dagger.Subcomponent

@Subcomponent(modules = [EnterTextModule::class])
interface EnterTextComponent {
    fun inject(fragment: EnterTextDialogFragment)
}