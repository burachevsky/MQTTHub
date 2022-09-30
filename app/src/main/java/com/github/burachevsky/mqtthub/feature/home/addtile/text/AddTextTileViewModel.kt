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
import com.github.burachevsky.mqtthub.feature.home.UITile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AddTextTileViewModel @Inject constructor(
    private val eventBus: EventBus,
    private val addTile: AddTile,
    args: AddTextTileFragmentArgs,
) : ViewModel() {

    private val brokerId = args.brokerId

    val container = ViewModelContainer<Navigator>(viewModelScope)

    private val name = InputFieldItem(
        label = Txt.of(R.string.tile_name)
    )

    private val subscribeTopic = InputFieldItem(
        label = Txt.of(R.string.subscribe_topic)
    )

    private val _items: MutableStateFlow<List<ListItem>> = MutableStateFlow(list())
    val items: StateFlow<List<ListItem>> = _items

    fun saveResult() {
        container.launch(Dispatchers.Main) {
            val tile = addTile(
                Tile(
                    name = name.text,
                    subscribeTopic = subscribeTopic.text,
                    qos = 0,
                    retained = false,
                    brokerId = brokerId,
                    type = UITile.Type.TEXT.toString(),
                )
            )

            eventBus.send(
                TileAdded(UITile.map(tile))
            )

            container.navigator {
                back()
            }
        }
    }

    private fun list(): List<ListItem> {
        return listOf(
            name,
            subscribeTopic,
            ButtonItem(Txt.of(R.string.save)),
        )
    }
}