package com.github.burachevsky.mqtthub.feature.home.addtile.button

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
import com.github.burachevsky.mqtthub.feature.home.addtile.text.TileAdded
import com.github.burachevsky.mqtthub.feature.home.addtile.text.TileEdited
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AddButtonTileViewModel @Inject constructor(
    private val eventBus: EventBus,
    args: AddButtonTileFragmentArgs,
    private val getTile: GetTile,
    private val updateTile: UpdateTile,
    private val addTile: AddTile,
) : ViewModel() {

    private val brokerId = args.brokerId
    private val tileId = args.tileId

    val title: Int = if (isEditMode()) R.string.edit_tile else R.string.new_tile

    val container = ViewModelContainer<Navigator>(viewModelScope)

    private val name = InputFieldItem(
        label = Txt.of(R.string.tile_name)
    )

    private val publishTopic = InputFieldItem(
        label = Txt.of(R.string.publish_topic)
    )

    private val payload = InputFieldItem(
        label = Txt.of(R.string.publish_payload)
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
                publishTopic.text = tile.publishTopic
                payload.text = tile.payload
                _itemsChanged.emit(Unit)
            }
        }
    }

    fun saveResult() {
        container.launch(Dispatchers.Main) {
            val tile = oldTile?.copy(
                name = name.text,
                publishTopic = publishTopic.text
            ) ?: Tile(
                name = name.text,
                subscribeTopic = "",
                publishTopic = publishTopic.text,
                payload = payload.text,
                qos = 0,
                retained = false,
                brokerId = brokerId,
                type = Tile.Type.BUTTON,
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

    private fun list(): List<ListItem> {
        return listOf(
            name,
            publishTopic,
            payload,
            ButtonItem(Txt.of(R.string.save)),
        )
    }

    private fun isEditMode() = tileId > 0
}