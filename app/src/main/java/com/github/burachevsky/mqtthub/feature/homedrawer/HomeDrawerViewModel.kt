package com.github.burachevsky.mqtthub.feature.homedrawer

import com.github.burachevsky.mqtthub.common.constant.Anim
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.common.widget.DividerItem
import com.github.burachevsky.mqtthub.data.entity.CurrentIds
import com.github.burachevsky.mqtthub.domain.usecase.broker.ObserveBrokers
import com.github.burachevsky.mqtthub.domain.usecase.currentids.ObserveCurrentIds
import com.github.burachevsky.mqtthub.domain.usecase.currentids.UpdateCurrentBroker
import com.github.burachevsky.mqtthub.domain.usecase.currentids.UpdateCurrentDashboard
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.ObserveDashboards
import com.github.burachevsky.mqtthub.feature.home.CloseHomeDrawer
import com.github.burachevsky.mqtthub.feature.home.HomeNavigator
import com.github.burachevsky.mqtthub.feature.homedrawer.item.DrawerHeaderItem
import com.github.burachevsky.mqtthub.feature.homedrawer.item.DrawerLabelItem
import com.github.burachevsky.mqtthub.feature.homedrawer.item.DrawerMenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

class HomeDrawerViewModel @Inject constructor(
    val container: ViewModelContainer<HomeNavigator>,
    private val updateCurrentDashboard: UpdateCurrentDashboard,
    private val updateCurrentBroker: UpdateCurrentBroker,
    observeCurrentIds: ObserveCurrentIds,
    observeDashboards: ObserveDashboards,
    observeBrokers: ObserveBrokers,
) {

    private val itemStore = HomeDrawerMenuItemStore()

    private val currentIds: Flow<CurrentIds> = observeCurrentIds()

    private val currentDashboardId: Flow<Long?> = currentIds
        .map { it.currentDashboardId }
        .distinctUntilChanged()

    private val currentBrokerId: Flow<Long?> = currentIds
        .map { it.currentBrokerId }
        .distinctUntilChanged()

    val items: StateFlow<List<ListItem>> =
        observeDashboards()
            .map { dashboards ->
                dashboards.map(DrawerMenuItem::map)
            }
            .selectCurrentItem(currentDashboardId)
            .combine(
                observeBrokers()
                    .map { brokers ->
                        brokers.map(DrawerMenuItem::map)
                    }
                    .selectCurrentItem(currentBrokerId),
                ::makeItemList
            )
            .stateIn(container.scope, SharingStarted.Eagerly, listOf(DrawerHeaderItem))

    private fun makeItemList(
        dashboards: List<DrawerMenuItem>,
        brokers: List<DrawerMenuItem>
    ): List<ListItem> {
        Timber.d("HomeDrawer: updating")
        return ArrayList<ListItem>().apply {
            add(DrawerHeaderItem)
            add(DividerItem)
            add(itemStore.dashboardsLabel)
            addAll(dashboards)
            add(itemStore.createDashboardButton)
            add(DividerItem)
            add(itemStore.brokersLabel)
            addAll(brokers)
            add(itemStore.addBrokerButton)
            add(DividerItem)
            add(itemStore.settingsButton)
        }
    }

    fun onLabelButtonClick(position: Int) {
        val item = items.get<DrawerLabelItem>(position)
        buttonClicked(item.id)
    }

    fun onMenuItemClick(position: Int) {
        val item = items.get<DrawerMenuItem>(position)

        when (item.type) {
            is DrawerMenuItem.Type.Dashboard -> {
                dashboardClicked(position, item.type.dashboard)
            }

            is DrawerMenuItem.Type.Button -> {
                buttonClicked(item.type.buttonId)
            }

            is DrawerMenuItem.Type.Broker -> {
                brokerClicked(position, item.type.broker)
            }
        }
    }

    private fun Flow<List<DrawerMenuItem>>.selectCurrentItem(
        currentIdFlow: Flow<Long?>
    ): Flow<List<DrawerMenuItem>> {
        return combine(currentIdFlow) { list, currentId ->
            list.map {
                it.copy(isSelected = it.type.id == currentId)
            }
        }
    }

    private fun buttonClicked(buttonId: Int) {
        when (buttonId) {
            BUTTON_CREATE_NEW_DASHBOARD -> closeDrawerAndNavigate {
                navigateEditDashboards(addNew = true)
            }

            BUTTON_ADD_NEW_BROKER -> closeDrawerAndNavigate {
                navigateAddBroker()
            }

            BUTTON_SETTINGS -> closeDrawerAndNavigate {
                navigateSettings()
            }

            BUTTON_HELP_AND_FEEDBACK -> {}

            BUTTON_EDIT_BROKERS -> closeDrawerAndNavigate {
                navigateEditBrokers()
            }

            BUTTON_EDIT_DASHBOARDS -> closeDrawerAndNavigate {
                navigateEditDashboards()
            }
        }
    }

    private fun dashboardClicked(position: Int, dashboard: Dashboard) {
        container.launch(Dispatchers.Default) {
            container.raiseEffect(CloseHomeDrawer)

            delay(Anim.DEFAULT_DURATION)

            if (!items.get<DrawerMenuItem>(position).isSelected) {
                updateCurrentDashboard(dashboard.id)
            }
        }
    }

    private fun brokerClicked(position: Int, broker: Broker) {
        container.launch(Dispatchers.Default) {
            if (!items.get<DrawerMenuItem>(position).isSelected) {
                updateCurrentBroker(broker.id)
            }

            container.raiseEffect(CloseHomeDrawer)
        }
    }

    private fun closeDrawerAndNavigate(navigate: HomeNavigator.() -> Unit) {
        container.launch(Dispatchers.Main) {
            container.raiseEffect(CloseHomeDrawer)
            delay(Anim.DEFAULT_DURATION)
            container.navigator { navigate() }
        }
    }

    companion object {
        const val BUTTON_CREATE_NEW_DASHBOARD = -1
        const val BUTTON_ADD_NEW_BROKER = -2
        const val BUTTON_SETTINGS = -3
        const val BUTTON_HELP_AND_FEEDBACK = -4
        const val BUTTON_EDIT_DASHBOARDS = -5
        const val BUTTON_EDIT_BROKERS = -6
    }
}