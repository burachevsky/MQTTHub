package com.github.burachevsky.mqtthub.feature.home.addtile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.GetTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.feature.home.addtile.text.TileAdded
import com.github.burachevsky.mqtthub.feature.home.addtile.text.TileEdited
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class AddTileViewModel (
    protected val eventBus: EventBus,
    protected val getTile: GetTile,
    protected val updateTile: UpdateTile,
    protected val addTile: AddTile,
    protected val brokerId: Long,
    protected val tileId: Long,
) : ViewModel() {
    abstract val title: Int

    val container = ViewModelContainer<Navigator>(viewModelScope)

    protected val _items: MutableStateFlow<List<ListItem>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<ListItem>> = _items

    protected val _itemsChanged = MutableSharedFlow<Unit>()
    val itemsChanged: SharedFlow<Unit> = _itemsChanged

    protected var oldTile: Tile? = null

    fun init() {
        if (isEditMode()) {
            container.launch(Dispatchers.Main) {
                oldTile = getTile(tileId)
            }
        }

        _items.value = list()
    }

    abstract fun initFields(tile: Tile)

    abstract fun list(): List<ListItem>

    fun isEditMode() = tileId > 0

    abstract fun collectTile(): Tile

    fun saveResult() {
        container.launch(Dispatchers.Main) {
            val tile = collectTile()

            if (isEditMode()) {
                updateTile(tile)
                eventBus.send(TileEdited(tile))
            } else {
                eventBus.send(TileAdded(addTile(tile)))
            }

            container.navigator {
                back()
            }
        }
    }
}