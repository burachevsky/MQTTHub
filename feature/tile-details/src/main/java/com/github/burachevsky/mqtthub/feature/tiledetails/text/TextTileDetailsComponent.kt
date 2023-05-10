package com.github.burachevsky.mqtthub.feature.tiledetails.text

import com.github.burachevsky.mqtthub.core.ui.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [TextTileDetailsModule::class])
interface TextTileDetailsComponent {

    fun inject(fragment: TextTileDetailsFragment)

    interface Provider {
        fun textTileDetailsComponent(module: TextTileDetailsModule): TextTileDetailsComponent
    }
}