package com.github.burachevsky.mqtthub.feature.tiledetails.text

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.text.ParcelableTxt
import com.github.burachevsky.mqtthub.feature.entertext.EnterTextDialogFragmentArgs

class TextTileDetailsNavigator(
    navController: NavController
) : Navigator(navController) {

    fun navigateEnterText(
        actionId: Int,
        title: ParcelableTxt,
    ) = navController.navigate(
        R.id.navigateEnterText,
        EnterTextDialogFragmentArgs(actionId, title).toBundle()
    )
}