package com.github.burachevsky.mqtthub.feature.home.addtile.text

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.widget.ButtonItem
import com.github.burachevsky.mqtthub.common.widget.InputFieldItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.GetTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.UpdateTile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class AddTextTileViewModel @Inject constructor(
    private val eventBus: EventBus,
    private val addTile: AddTile,
    private val updateTile: UpdateTile,
    private val getTile: GetTile,
    args: AddTextTileFragmentArgs,
) : ViewModel() {

    private val brokerId = args.brokerId
    private val tileId = args.tileId

    val title: Int = if (isEditMode()) R.string.edit_tile else R.string.new_tile

    val container = ViewModelContainer<Navigator>(viewModelScope)

    private val name = InputFieldItem(
        label = Txt.of(R.string.tile_name)
    )

    private val subscribeTopic = InputFieldItem(
        label = Txt.of(R.string.subscribe_topic)
    )

    private val _items: MutableStateFlow<List<ListItem>> = MutableStateFlow(list())
    val items: StateFlow<List<ListItem>> = _items

    private val _itemsChanged = MutableSharedFlow<Unit>()
    val itemsChanged: SharedFlow<Unit> = _itemsChanged

    private var oldTile: Tile? = null

    init {
        if (isEditMode()) {
            container.launch(Dispatchers.Main) {
                val tile = getTile(tileId)
                oldTile = tile
                name.text = tile.name
                subscribeTopic.text = tile.subscribeTopic
                _itemsChanged.emit(Unit)
            }
        }
    }

    fun saveResult() {
        container.launch(Dispatchers.Main) {
            val tile = oldTile?.copy(
                name = name.text,
                subscribeTopic = subscribeTopic.text,
            ) ?: Tile(
                name = name.text,
                subscribeTopic = subscribeTopic.text,
                qos = 0,
                retained = false,
                brokerId = brokerId,
                type = Tile.Type.TEXT,
            )

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

    private fun isEditMode() = tileId > 0

    private fun list(): List<ListItem> {
        return listOf(
            name,
            subscribeTopic,
            ButtonItem(Txt.of(R.string.save)),
        )
    }
}