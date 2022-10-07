package com.github.burachevsky.mqtthub.feature.home.addtile.switch

import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.constant.SWITCH_OFF
import com.github.burachevsky.mqtthub.common.constant.SWITCH_ON
import com.github.burachevsky.mqtthub.common.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.ext.getPayload
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

class AddSwitchViewModel @Inject constructor(
    eventBus: EventBus,
    getTile: GetTile,
    updateTile: UpdateTile,
    addTile: AddTile,
    args: AddSwitchFragmentArgs,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, args.brokerId, args.tileId) {

    override val title = if (isEditMode()) R.string.edit_switch else R.string.new_switch

    private val name = InputFieldItem(
        label = Txt.of(R.string.tile_name)
    )

    private val subscribeTopic = InputFieldItem(
        label = Txt.of(R.string.subscribe_topic)
    )

    private val publishTopic = InputFieldItem(
        label = Txt.of(R.string.publish_topic)
    )

    private val onState = InputFieldItem(
        label = Txt.of(R.string.on_state),
        placeholder = Txt.of("1")
    )

    private val offState = InputFieldItem(
        label = Txt.of(R.string.off_state),
        placeholder = Txt.of("0")
    )

    init {
        init()
    }

    override fun initFields(tile: Tile) {
        name.text = tile.name
        subscribeTopic.text = tile.subscribeTopic
        publishTopic.text = tile.publishTopic
        onState.text = tile.stateList.getPayload(SWITCH_ON).orEmpty()
        offState.text = tile.stateList.getPayload(SWITCH_OFF).orEmpty()
    }

    override fun list(): List<ListItem> {
        return listOf(
            name,
            subscribeTopic,
            publishTopic,
            onState,
            offState,
            ButtonItem(Txt.of(R.string.save)),
        )
    }

    override fun collectTile(): Tile {
        return oldTile?.copy(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
        ) ?: Tile(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = publishTopic.text,
            qos = 0,
            retained = false,
            brokerId = brokerId,
            type = Tile.Type.SWITCH,
            stateList = listOf(
                Tile.State(SWITCH_ON, onState.text),
                Tile.State(SWITCH_OFF, offState.text)
            )
        )
    }
}