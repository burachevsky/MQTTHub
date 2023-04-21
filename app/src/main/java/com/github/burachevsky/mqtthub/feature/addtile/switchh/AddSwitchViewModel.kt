package com.github.burachevsky.mqtthub.feature.addtile.switchh

import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.constant.SWITCH_OFF
import com.github.burachevsky.mqtthub.common.constant.SWITCH_ON
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.ext.getPayload
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.widget.InputFieldItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.GetTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.feature.addtile.AddTileViewModel
import com.github.burachevsky.mqtthub.feature.addtile.DASHBOARD_ID
import com.github.burachevsky.mqtthub.feature.addtile.DASHBOARD_POSITION
import com.github.burachevsky.mqtthub.feature.addtile.TILE_ID
import javax.inject.Inject
import javax.inject.Named

class AddSwitchViewModel @Inject constructor(
    @Named(DASHBOARD_ID) dashboardId: Long,
    @Named(TILE_ID) tileId: Long,
    @Named(DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    getTile: GetTile,
    updateTile: UpdateTile,
    addTile: AddTile,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, dashboardId, tileId, dashboardPosition) {

    override val title = if (isEditMode()) R.string.edit_switch else R.string.new_switch

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
        retain.isChecked = tile.retained
        qos.selectedValue = tile.qos
    }

    override fun list(): List<ListItem> {
        return listOf(
            name,
            subscribeTopic,
            publishTopic,
            onState,
            offState,
            qos,
            retain,
            save,
        )
    }

    override fun collectTile(): Tile {
        return oldTile?.copy(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = publishTopic.text,
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.SWITCH,
            stateList = listOf(
                Tile.State(SWITCH_ON, onState.text),
                Tile.State(SWITCH_OFF, offState.text)
            )
        ) ?: Tile(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = publishTopic.text,
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.SWITCH,
            stateList = listOf(
                Tile.State(SWITCH_ON, onState.text),
                Tile.State(SWITCH_OFF, offState.text)
            ),
            dashboardPosition = dashboardPosition,
        )
    }
}