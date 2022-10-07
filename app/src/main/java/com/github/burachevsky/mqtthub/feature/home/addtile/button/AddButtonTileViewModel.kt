package com.github.burachevsky.mqtthub.feature.home.addtile.button

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

class AddButtonTileViewModel @Inject constructor(
    args: AddButtonTileFragmentArgs,
    eventBus: EventBus,
    getTile: GetTile,
    updateTile: UpdateTile,
    addTile: AddTile,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, args.brokerId, args.tileId){

    override val title: Int = if (isEditMode()) R.string.edit_button_tile else R.string.new_button_tile

    private val name = InputFieldItem(
        label = Txt.of(R.string.tile_name)
    )

    private val publishTopic = InputFieldItem(
        label = Txt.of(R.string.publish_topic)
    )

    private val payload = InputFieldItem(
        label = Txt.of(R.string.publish_payload)
    )

    init {
        init()
    }

    override fun initFields(tile: Tile) {
        name.text = tile.name
        publishTopic.text = tile.publishTopic
        payload.text = tile.payload
        _itemsChanged.tryEmit(Unit)
    }

    override fun collectTile(): Tile {
        return oldTile?.copy(
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
            stateList = emptyList()
        )
    }

    override fun list(): List<ListItem> {
        return listOf(
            name,
            publishTopic,
            payload,
            ButtonItem(Txt.of(R.string.save)),
        )
    }
}