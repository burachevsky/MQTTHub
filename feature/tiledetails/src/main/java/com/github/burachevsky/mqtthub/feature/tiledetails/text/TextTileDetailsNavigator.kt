package com.github.burachevsky.mqtthub.feature.tiledetails.text

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import com.github.burachevsky.mqtthub.core.ui.constant.NavDestination
import com.github.burachevsky.mqtthub.core.ui.container.NavDestinationMapper
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.EnterTextConfig
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import com.github.burachevsky.mqtthub.core.ui.text.ParcelableTxt

class TextTileDetailsNavigator(
    navController: NavController,
    action: NavDestinationMapper,
): Navigator(navController, action) {

    fun navigateEnterText(
        actionId: Int,
        title: ParcelableTxt,
    ) = navigate(
        NavDestination.EnterText,
        bundleOf(
            NavArg.CONFIG to EnterTextConfig(
                actionId = actionId,
                title = title,
                initText = null
            )
        )
    )
}