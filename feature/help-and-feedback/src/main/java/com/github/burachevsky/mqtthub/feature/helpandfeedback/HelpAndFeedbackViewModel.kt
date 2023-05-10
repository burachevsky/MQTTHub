package com.github.burachevsky.mqtthub.feature.helpandfeedback

import androidx.lifecycle.ViewModel
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import javax.inject.Inject

class HelpAndFeedbackViewModel @Inject constructor() : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()
}