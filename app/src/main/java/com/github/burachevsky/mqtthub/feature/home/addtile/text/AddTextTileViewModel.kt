package com.github.burachevsky.mqtthub.feature.home.addtile.text

import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.widget.ButtonItem
import com.github.burachevsky.mqtthub.common.widget.InputFieldItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.GetTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileViewModel
import javax.inject.Inject

class AddTextTileViewModel @Inject constructor(
    eventBus: EventBus,
    addTile: AddTile,
    updateTile: UpdateTile,
    getTile: GetTile,
    args: AddTextTileFragmentArgs,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, args.brokerId, args.tileId) {

    override val title: Int = if (isEditMode()) R.string.edit_text_tile else R.string.new_text_tile

    private val name = InputFieldItem(
        label = Txt.of(R.string.tile_name)
    )

    private val subscribeTopic = InputFieldItem(
        label = Txt.of(R.string.subscribe_topic)
    )

    init {
        init()
    }

    override fun initFields(tile: Tile) {
        name.text = tile.name
        subscribeTopic.text = tile.subscribeTopic
        _itemsChanged.tryEmit(Unit)
    }

    override fun collectTile(): Tile {
        return oldTile?.copy(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
        ) ?: Tile(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = "",
            qos = 0,
            retained = false,
            brokerId = brokerId,
            type = Tile.Type.TEXT,
            stateList = emptyList()
        )
    }

    override fun list(): List<ListItem> {
        return listOf(
            name,
            subscribeTopic,
            ButtonItem(Txt.of(R.string.save)),
        )
    }
}