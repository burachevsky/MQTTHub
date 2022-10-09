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
import com.github.burachevsky.mqtthub.feature.home.addtile.BROKER_ID
import com.github.burachevsky.mqtthub.feature.home.addtile.DASHBOARD_POSITION
import com.github.burachevsky.mqtthub.feature.home.addtile.TILE_ID
import javax.inject.Inject
import javax.inject.Named

class AddTextTileViewModel @Inject constructor(
    @Named(BROKER_ID) brokerId: Long,
    @Named(TILE_ID) tileId: Long,
    @Named(DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    addTile: AddTile,
    updateTile: UpdateTile,
    getTile: GetTile,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, brokerId, tileId, dashboardPosition) {

    override val title: Int = if (isEditMode()) R.string.edit_text_tile else R.string.new_text_tile

    private val name = InputFieldItem(
        label = Txt.of(R.string.tile_name)
    )

    init {
        init()
    }

    override fun initFields(tile: Tile) {
        name.text = tile.name
        subscribeTopic.text = tile.subscribeTopic
        retain.isChecked = tile.retained
        qos.selectedValue = tile.qos
    }

    override fun collectTile(): Tile {
        return oldTile?.copy(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = "",
            qos = qos.selectedValue,
            retained = retain.isChecked,
            brokerId = brokerId,
            type = Tile.Type.TEXT,
            stateList = emptyList()
        ) ?: Tile(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = "",
            qos = qos.selectedValue,
            retained = retain.isChecked,
            brokerId = brokerId,
            type = Tile.Type.TEXT,
            stateList = emptyList(),
            dashboardPosition = dashboardPosition,
        )
    }

    override fun list(): List<ListItem> {
        return listOf(
            name,
            subscribeTopic,
            qos,
            retain,
            ButtonItem(Txt.of(R.string.save)),
        )
    }
}