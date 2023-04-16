package com.github.burachevsky.mqtthub.feature.home

import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerAdded
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerEdited
import com.github.burachevsky.mqtthub.feature.brokers.BrokerDeleted
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
        }
    }

    suspend fun fillDrawer() {
        val brokers = vm.getBrokers()

        vm.container.withContext(Dispatchers.Default) {

            val list = mutableListOf(
                DrawerHeaderItem,
                DividerItem,
                DrawerLabelItem(
                    Txt.of(R.string.home_dashboards),
                ),
            ).apply {

                addAll(
                    listOf(
                        DrawerMenuItem(
                            Txt.of("Dashboard 1"),
                            R.drawable.ic_dashboard,
                            type = DrawerMenuItem.Type.Dashboard(),
                            isSelected = true,
                        ),
                        DrawerMenuItem(
                            Txt.of("Dashboard 2"),
                            R.drawable.ic_dashboard,
                            type = DrawerMenuItem.Type.Dashboard(),
                        ),
                        DrawerMenuItem(
                            Txt.of("Dashboard 3"),
                            R.drawable.ic_dashboard,
                            type = DrawerMenuItem.Type.Dashboard(),
                        ),
                        DrawerMenuItem(
                            Txt.of("Dashboard 4"),
                            R.drawable.ic_dashboard,
                            type = DrawerMenuItem.Type.Dashboard(),
                        ),
                    )
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
                            Txt.of(R.string.home_brokers),
                            buttonText = Txt.of(R.string.edit),
                            onClick = {
                                vm.container.navigator { navigateEditBrokers() }
                            }
                        ),
                    )
                )

                addAll(
                    brokers.map {
                        DrawerMenuItem(
                            Txt.of(it.name),
                            R.drawable.ic_broker,
                            type = DrawerMenuItem.Type.Broker(it),
                            isSelected = it.id == vm.brokerId, // todo
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

    fun onMenuItemClick(position: Int) {
        val item = items.get<DrawerMenuItem>(position)

        when (item.type) {
            is DrawerMenuItem.Type.Dashboard -> {

            }

            is DrawerMenuItem.Type.Button -> {
                buttonClicked(item.type.buttonId)
            }

            is DrawerMenuItem.Type.Broker -> {
                brokerClicked(position, item, item.type.broker)
            }
        }
    }

    private fun buttonClicked(buttonId: Int) {
        when (buttonId) {
            BUTTON_CREATE_NEW_DASHBOARD -> {}
            BUTTON_ADD_NEW_BROKER -> {
                vm.container.navigator { navigateAddBroker() }
            }
            BUTTON_SETTINGS -> {}
            BUTTON_HELP_AND_FEEDBACK -> {}
        }
    }

    private fun brokerClicked(position: Int, menuItem: DrawerMenuItem, broker: Broker) {
        _items.value = _items.value.mapIndexed { i, it ->
            if (it is DrawerMenuItem && it.type is DrawerMenuItem.Type.Broker) {
                it.copy(isSelected = i == position)
            } else it
        }

        vm.changeBroker(broker.id)
        vm.container.raiseEffect(CloseHomeDrawer)
    }

    private fun addBrokerToList(broker: Broker) {
        val position = items.value.indexOfLast {
            it is DrawerMenuItem && it.type is DrawerMenuItem.Type.Broker
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
    }
}