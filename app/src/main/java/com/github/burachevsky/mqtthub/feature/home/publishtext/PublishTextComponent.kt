package com.github.burachevsky.mqtthub.feature.home.publishtext

import dagger.Subcomponent

@Subcomponent(modules = [PublishTextModule::class])
interface PublishTextComponent {
    fun inject(fragment: PublishTextDialogFragment)
}