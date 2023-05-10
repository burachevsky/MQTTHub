package com.github.burachevsky.mqtthub.feature.dashboards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.core.domain.usecase.dashboard.AddDashboard
import com.github.burachevsky.mqtthub.core.domain.usecase.dashboard.DeleteDashboard
import com.github.burachevsky.mqtthub.core.domain.usecase.dashboard.ObserveDashboards
import com.github.burachevsky.mqtthub.core.domain.usecase.dashboard.UpdateDashboard
import com.github.burachevsky.mqtthub.core.model.Dashboard
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.event.AlertDialog
import com.github.burachevsky.mqtthub.core.ui.ext.get
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.text.withArgs
import com.github.burachevsky.mqtthub.feature.dashboards.item.DashboardItem
import com.github.burachevsky.mqtthub.feature.dashboards.item.ItemConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class DashboardsViewModel @Inject constructor(
    private val addDashboard: AddDashboard,
    private val updateDashboard: UpdateDashboard,
    private val deleteDashboard: DeleteDashboard,
    private val addNew: Boolean,
    observeDashboards: ObserveDashboards,
) : ViewModel(), VM<DashboardsNavigator> {

    override val container = viewModelContainer()

    val items: StateFlow<List<ListItem>> = observeDashboards()
        .map(::makeItemsList)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private var isInitialized = false

    fun submit(position: Int) {
        val item = items.get<DashboardItem>(position)
        val itemText = item.text

        when (item.config) {
            is ItemConfig.CreateNew -> {
                if (itemText.isNotEmpty()) {
                    container.launch(Dispatchers.Main) {
                       addDashboard(Dashboard(name = itemText))
                    }
                }
            }

            is ItemConfig.Default -> {
                container.launch(Dispatchers.Main) {
                    item.dashboard?.let { dashboard ->
                        updateDashboard(dashboard.copy(name = itemText))
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

    private fun makeItemsList(dashboards: List<Dashboard>): List<ListItem> {
        val initFocusOnAddField = addNew && !isInitialized
        isInitialized = true

        return listOf(
            DashboardItem(
                config = ItemConfig.CreateNew,
                initFocus = initFocusOnAddField
            )
        ) + dashboards.map {
            DashboardItem(
                config = ItemConfig.Default,
                initText = it.name,
                dashboard = it,
            )
        }
    }

    companion object {
        private const val MIN_LIST_ITEMS = 2
    }
}