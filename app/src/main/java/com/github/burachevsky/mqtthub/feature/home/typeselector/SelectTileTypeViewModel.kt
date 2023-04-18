package com.github.burachevsky.mqtthub.feature.home.typeselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.data.entity.Tile
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SelectTileTypeViewModel @Inject constructor(
    private val eventBus: EventBus,
) : ViewModel(), VM<Navigator> {

    override val container = ViewModelContainer<Navigator>(viewModelScope)

    val items: List<TileTypeItem> = listOf(
        TileTypeItem(
            text = Txt.of(R.string.tile_type_text),
            type = Tile.Type.TEXT
        ),
        TileTypeItem(
            text = Txt.of(R.string.tile_type_button),
            type = Tile.Type.BUTTON
        ),
        TileTypeItem(
            text = Txt.of(R.string.tile_type_switch),
            type = Tile.Type.SWITCH
        )
    )

    fun tileTypeClicked(position: Int) {
        container.launch(Dispatchers.Main) {
            eventBus.send(TileTypeSelected(items[position].type))
        }

        container.navigator {
            back()
        }
    }
}