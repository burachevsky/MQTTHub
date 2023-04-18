package com.github.burachevsky.mqtthub.feature.dashboards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.event.AlertDialog
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.text.withArgs
import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.AddDashboard
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.DeleteDashboard
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.GetDashboards
import com.github.burachevsky.mqtthub.domain.usecase.dashboard.UpdateDashboard
import com.github.burachevsky.mqtthub.feature.dashboards.item.DashboardItem
import com.github.burachevsky.mqtthub.feature.dashboards.item.ItemConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DashboardsViewModel @Inject constructor(
    args: DashboardsFragmentArgs,
    private val getDashboards: GetDashboards,
    private val eventBus: EventBus,
    private val addDashboard: AddDashboard,
    private val updateDashboard: UpdateDashboard,
    private val deleteDashboard: DeleteDashboard,
) : ViewModel(), VM<DashboardsNavigator> {

    override val container = ViewModelContainer<DashboardsNavigator>(viewModelScope)

    private val _items: MutableStateFlow<List<ListItem>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    init {
        container.launch(Dispatchers.Main) {
            val dashboards = getDashboards()

            _items.value = listOf(
                DashboardItem(
                    config = ItemConfig.CreateNew,
                    initFocus = args.addNew
                )
            ) + dashboards.map {
                DashboardItem(
                    config = ItemConfig.Default,
                    initText = it.name,
                    dashboard = it,
                )
            }
        }
    }

    fun submit(position: Int) {
        val item = items.get<DashboardItem>(position)
        val itemText = item.text

        when (item.config) {
            is ItemConfig.CreateNew -> {
                if (itemText.isNotEmpty()) {
                    container.launch(Dispatchers.Main) {
                        val dashboard = addDashboard(
                            Dashboard(name = itemText)
                        )

                        _items.value = _items.value.toMutableList().apply {
                            add(
                                NEW_ITEM_POSITION,
                                DashboardItem(
                                    config = ItemConfig.Default,
                                    initText = dashboard.name,
                                    dashboard = dashboard,
                                )
                            )
                        }

                        eventBus.send(DashboardCreated(dashboard))
                    }
                }
            }

            is ItemConfig.Default -> {
                container.launch(Dispatchers.Main) {
                    item.dashboard?.let { dashboard ->
                        val editedDashboard = dashboard.copy(name = itemText)

                        updateDashboard(editedDashboard)

                        _items.value = _items.value.toMutableList().apply {
                            this[position] = item.copy(
                                initText = editedDashboard.name,
                                dashboard = editedDashboard
                            )
                        }

                        eventBus.send(DashboardEdited(editedDashboard))
                    }
                }
            }
        }
    }

    fun delete(position: Int) {
        if (items.value.size <= MIN_LIST_ITEMS) {
            showMinItemsDialog()
            return
        }

        val item = items.get<DashboardItem>(position)

        showDeleteDialog(
            dashboardName = item.initText,
            ifYes = {
                container.launch(Dispatchers.Main) {
                    item.dashboard?.id?.let { dashboardId ->
                        deleteDashboard(dashboardId)

                        _items.value = _items.value.toMutableList().apply {
                            removeAt(position)
                        }

                        eventBus.send(DashboardDeleted(dashboardId))
                    }
                }
            }
        )
    }

    private fun showMinItemsDialog() {
        container.raiseEffect {
            AlertDialog(
                message = Txt.of(R.string.dashboards_min_items_dialog_message),
                yes = AlertDialog.Button(
                    text = Txt.of(R.string.dashboards_min_items_dialog_button)
                )
            )
        }
    }

    private fun showDeleteDialog(dashboardName: String, ifYes: () -> Unit) {
        container.raiseEffect {
            AlertDialog(
                title = Txt.of(R.string.dashboards_delete_dialog_title)
                    .withArgs(dashboardName),
                message = Txt.of(R.string.dashboards_delete_dialog_message),
                yes = AlertDialog.Button(
                    text = Txt.of(R.string.remove_dialog_yes),
                    action = ifYes
                ),
                cancel = AlertDialog.Button(
                    text = Txt.of(R.string.remove_dialog_cancel)
                )
            )
        }
    }

    companion object {
        private const val NEW_ITEM_POSITION = 1
        private const val MIN_LIST_ITEMS = 2
    }
}