package com.github.burachevsky.mqtthub.feature.publishtext

import dagger.Subcomponent

@Subcomponent(modules = [PublishTextModule::class])
interface PublishTextComponent {
    fun inject(fragment: PublishTextDialogFragment)
}