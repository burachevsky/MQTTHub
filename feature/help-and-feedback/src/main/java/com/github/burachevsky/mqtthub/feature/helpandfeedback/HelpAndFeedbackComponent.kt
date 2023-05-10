package com.github.burachevsky.mqtthub.feature.helpandfeedback

import com.github.burachevsky.mqtthub.core.ui.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface HelpAndFeedbackComponent {

    fun inject(fragment: HelpAndFeedbackFragment)

    interface Provider {
        fun helpAndSupportComponent(): HelpAndFeedbackComponent
    }
}