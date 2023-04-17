package com.github.burachevsky.mqtthub.feature.home

import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerAdded
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerEdited
import com.github.burachevsky.mqtthub.feature.brokers.BrokerDeleted
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardCreated
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardDeleted
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardEdited
import com.github.burachevsky.mqtthub.feature.home.item.DividerItem
import com.github.burachevsky.mqtthub.feature.home.item.DrawerHeaderItem
import com.github.burachevsky.mqtthub.feature.home.item.DrawerLabelItem
import com.github.burachevsky.mqtthub.feature.home.item.DrawerMenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeDrawerManager(
    private val vm: HomeViewModel
) {
    private val _items = MutableStateFlow<List<ListItem>>(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    init {
        vm.eventBus.apply {
            subscribe<BrokerAdded>(vm.viewModelScope) {
                addBrokerToList(it.broker)
            }

            subscribe<BrokerEdited>(vm.viewModelScope) {
                editBrokerInList(it.broker)
            }

            subscribe<BrokerDeleted>(vm.viewModelScope) {
                deleteBrokerFromList(it.brokerId)
            }

            subscribe<DashboardCreated>(vm.viewModelScope) {
                addDashboardToList(it.dashboard)
            }

            subscribe<DashboardEdited>(vm.viewModelScope) {
                editDashboardInList(it.dashboard)
            }

            subscribe<DashboardDeleted>(vm.viewModelScope) {
                deleteDashboardFromList(it.dashboardId)
            }
        }
    }

    suspend fun fillDrawer() {
        val brokers = vm.getBrokers()
        val dashboards = vm.getDashboards()
        val currentIds = vm.getCurrentIds()

        vm.container.withContext(Dispatchers.Default) {

            val list = mutableListOf(
                DrawerHeaderItem,
                DividerItem,
                DrawerLabelItem(
                    id = BUTTON_EDIT_DASHBOARDS,
                    Txt.of(R.string.home_dashboards),
                    buttonText = Txt.of(R.string.edit),
                ),
            ).apply {

                addAll(
                    dashboards.map {
                        DrawerMenuItem(
                            Txt.of(it.name),
                            R.drawable.ic_dashboard,
                            type = DrawerMenuItem.Type.Dashboard(it),
                            isSelected = it.id == currentIds.currentDashboardId,
                        )
                    }
                )

                addAll(
                    listOf(
                        DrawerMenuItem(
                            Txt.of(R.string.home_create_new_dashboard),
                            R.drawable.ic_add,
                            type = DrawerMenuItem.Type.Button(BUTTON_CREATE_NEW_DASHBOARD),
                        ),
                        DividerItem,
                        DrawerLabelItem(
                            id = BUTTON_EDIT_BROKERS,
                            Txt.of(R.string.home_brokers),
                            buttonText = Txt.of(R.string.edit),
                        ),
                    )
                )

                addAll(
                    brokers.map {
                        DrawerMenuItem(
                            Txt.of(it.name),
                            R.drawable.ic_broker,
                            type = DrawerMenuItem.Type.Broker(it),
                            isSelected = it.id == currentIds.currentBrokerId,
                        )
                    }
                )

                addAll(
                    listOf(
                        DrawerMenuItem(
                            Txt.of(R.string.home_add_new_broker),
                            R.drawable.ic_add,
                            type = DrawerMenuItem.Type.Button(BUTTON_ADD_NEW_BROKER),
                        ),
                        DividerItem,
                        DrawerMenuItem(
                            Txt.of(R.string.home_settings),
                            R.drawable.ic_settings,
                            type = DrawerMenuItem.Type.Button(BUTTON_SETTINGS),
                        ),
                        DrawerMenuItem(
                            Txt.of(R.string.home_help_and_feedback),
                            R.drawable.ic_help,
                            type = DrawerMenuItem.Type.Button(BUTTON_HELP_AND_FEEDBACK),
                        ),
                    )
                )
            }

            _items.value = list
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

    private fun buttonClicked(buttonId: Int) {
        when (buttonId) {
            BUTTON_CREATE_NEW_DASHBOARD -> {
                vm.container.navigator { navigateEditDashboards(addNew = true) }
            }

            BUTTON_ADD_NEW_BROKER -> {
                vm.container.navigator { navigateAddBroker() }
            }

            BUTTON_SETTINGS -> {}

            BUTTON_HELP_AND_FEEDBACK -> {}

            BUTTON_EDIT_BROKERS -> {
                vm.container.navigator { navigateEditBrokers() }
            }

            BUTTON_EDIT_DASHBOARDS -> {
                vm.container.navigator { navigateEditDashboards() }
            }
        }
    }

    private fun addDashboardToList(dashboard: Dashboard) {
        val position = items.value.indexOfFirst {
            it is DrawerLabelItem && it.id == BUTTON_EDIT_DASHBOARDS
        } + 1

        _items.value = _items.value.toMutableList().apply {
            add(
                position,
                DrawerMenuItem(
                    text = Txt.of(dashboard.name),
                    icon = R.drawable.ic_dashboard,
                    type = DrawerMenuItem.Type.Dashboard(dashboard)
                )
            )
        }
    }

    private fun editDashboardInList(dashboard: Dashboard) {
        val position = items.value.indexOfFirst {
            it is DrawerMenuItem
                    && it.type is DrawerMenuItem.Type.Dashboard
                    && it.type.dashboard.id == dashboard.id
        }

        if (position < 0) return

        val item = items.get<DrawerMenuItem>(position)

        _items.value = _items.value.toMutableList().apply {
            this[position] = item.copy(
                text = Txt.of(dashboard.name),
                type = DrawerMenuItem.Type.Dashboard(dashboard),
            )
        }
    }

    private fun deleteDashboardFromList(dashboardId: Long) {
        val position = _items.value.indexOfFirst {
            it is DrawerMenuItem
                    && it.type is DrawerMenuItem.Type.Dashboard
                    && it.type.dashboard.id == dashboardId
        }

        val item = items.get<DrawerMenuItem>(position)

        _items.value = _items.value.toMutableList().apply {
            removeAt(position)
        }

        if (item.isSelected) {
            val newDashboardPosition = items.value.indexOfFirst {
                it is DrawerMenuItem
                        && it.type is DrawerMenuItem.Type.Dashboard
            }

            if (newDashboardPosition >= 0) {
                dashboardClicked(
                    newDashboardPosition,
                    (items.get<DrawerMenuItem>(position).type as DrawerMenuItem.Type.Dashboard)
                        .dashboard
                )
            }
        }
    }

    private fun dashboardClicked(position: Int, dashboard: Dashboard) {
        val alreadySelected = items.get<DrawerMenuItem>(position).isSelected

        if (!alreadySelected) {
            _items.value = _items.value.mapIndexed { i, it ->
                if (it is DrawerMenuItem && it.type is DrawerMenuItem.Type.Dashboard) {
                    it.copy(isSelected = i == position)
                } else it
            }

            vm.changeDashboard(dashboard)
        }

        vm.container.raiseEffect(CloseHomeDrawer)
    }

    private fun brokerClicked(position: Int, broker: Broker) {
        val alreadySelected = items.get<DrawerMenuItem>(position).isSelected

        if (!alreadySelected) {
            _items.value = _items.value.mapIndexed { i, it ->
                if (it is DrawerMenuItem && it.type is DrawerMenuItem.Type.Broker) {
                    it.copy(isSelected = i == position)
                } else it
            }

            vm.changeBroker(broker)
        }

        vm.container.raiseEffect(CloseHomeDrawer)
    }

    private fun addBrokerToList(broker: Broker) {
        val position = items.value.indexOfFirst {
            it is DrawerLabelItem && it.id == BUTTON_EDIT_BROKERS
        } + 1

        _items.value = _items.value.toMutableList().apply {
            add(
                position,
                DrawerMenuItem(
                    text = Txt.of(broker.name),
                    icon = R.drawable.ic_broker,
                    type = DrawerMenuItem.Type.Broker(broker)
                )
            )
        }

        val brokersCount = items.value.count {
            it is DrawerMenuItem && it.type is DrawerMenuItem.Type.Broker
        }

        if (brokersCount == 1) {
            vm.changeBroker(broker)
        }
    }

    private fun editBrokerInList(broker: Broker) {
        val position = items.value.indexOfLast {
            it is DrawerMenuItem
                    && it.type is DrawerMenuItem.Type.Broker
                    && it.type.broker.id == broker.id
        }

        if (position < 0) return

        val item = items.get<DrawerMenuItem>(position)

        _items.value = _items.value.toMutableList().apply {
            this[position] = item.copy(
                text = Txt.of(broker.name),
                type = DrawerMenuItem.Type.Broker(broker)
            )
        }
    }

    private fun deleteBrokerFromList(brokerId: Long) {
        _items.value = _items.value.filter {
            !(it is DrawerMenuItem
                    && it.type is DrawerMenuItem.Type.Broker
                    && it.type.broker.id == brokerId)
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