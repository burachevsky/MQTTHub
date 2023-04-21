package com.github.burachevsky.mqtthub.feature.tiledetails.text

import com.github.burachevsky.mqtthub.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [TextTileDetailsModule::class])
interface TextTileDetailsComponent {

    fun inject(fragment: TextTileDetailsFragment)
}