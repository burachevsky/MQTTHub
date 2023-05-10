package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerLabelItem
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerMenuItem

class HomeDrawerMenuItemStore {
    val dashboardsLabel = DrawerLabelItem(
        id = DrawerMenuId.DRAWER_BUTTON_EDIT_DASHBOARDS,
        Txt.of(R.string.home_dashboards),
        buttonText = Txt.of(R.string.edit),
    )

    val createDashboardButton = DrawerMenuItem(
        Txt.of(R.string.home_create_new_dashboard),
        R.drawable.ic_add,
        type = DrawerMenuItem.Type.Button(DrawerMenuId.DRAWER_BUTTON_CREATE_NEW_DASHBOARD),
    )

    val brokersLabel = DrawerLabelItem(
        id = DrawerMenuId.DRAWER_BUTTON_EDIT_BROKERS,
        Txt.of(R.string.home_brokers),
        buttonText = Txt.of(R.string.edit),
    )

    val addBrokerButton = DrawerMenuItem(
        Txt.of(R.string.home_add_new_broker),
        R.drawable.ic_add,
        type = DrawerMenuItem.Type.Button(DrawerMenuId.DRAWER_BUTTON_ADD_NEW_BROKER),
    )

    val settingsButton = DrawerMenuItem(
        Txt.of(R.string.home_settings),
        R.drawable.ic_settings,
        type = DrawerMenuItem.Type.Button(DrawerMenuId.DRAWER_BUTTON_SETTINGS),
    )

    val helpAndFeedback = DrawerMenuItem(
        Txt.of(R.string.help_and_feedback),
        R.drawable.ic_help,
        type = DrawerMenuItem.Type.Button(DrawerMenuId.DRAWER_BUTTON_HELP_AND_FEEDBACK)
    )
}