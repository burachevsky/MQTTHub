package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Dashboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DashboardManager(
    val container: ViewModelContainer<HomeNavigator>
) {

    private val _items = MutableStateFlow<List<ListItem>>(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    private val _editMode = MutableStateFlow(EditModeState())
    val editMode: StateFlow<EditModeState> = _editMode

    private var itemReleased = true

    private var dashboard: Dashboard? = null
}