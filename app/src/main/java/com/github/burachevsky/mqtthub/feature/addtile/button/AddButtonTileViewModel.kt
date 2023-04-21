package com.github.burachevsky.mqtthub.feature.addtile.button

import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.widget.InputFieldItem
import com.github.burachevsky.mqtthub.common.widget.SwitchItem
import com.github.burachevsky.mqtthub.common.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.common.widget.ToggleOption
import com.github.burachevsky.mqtthub.data.entity.ButtonTileStyledId
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

class AddButtonTileViewModel @Inject constructor(
    @Named(DASHBOARD_ID) dashboardId: Long,
    @Named(TILE_ID) tileId: Long,
    @Named(DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    getTile: GetTile,
    updateTile: UpdateTile,
    addTile: AddTile,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, dashboardId, tileId, dashboardPosition) {

    override val title: Int = if (isEditMode()) R.string.edit_button_tile else R.string.new_button_tile

    private val payload = InputFieldItem(
        label = Txt.of(R.string.publish_payload)
    )

    private val style = ToggleGroupItem(
        title = Txt.of(R.string.button_tile_style),
        options = listOf(
            ToggleOption(
                id = ButtonTileStyledId.OUTLINED,
                text = Txt.of(R.string.tile_style_outlined)
            ),
            ToggleOption(
                id = ButtonTileStyledId.FILLED,
                text = Txt.of(R.string.tile_style_filled)
            ),
        ),
        selectedValue = ButtonTileStyledId.OUTLINED
    )

    private val width = SwitchItem(
        text = Txt.of(R.string.tile_fills_screen_width)
    )

    init {
        init()
    }

    override fun initFields(tile: Tile) {
        name.text = tile.name
        publishTopic.text = tile.publishTopic
        payload.text = tile.payload
        retain.isChecked = tile.retained
        qos.selectedValue = tile.qos
        style.selectedValue = tile.design.styleId
        width.isChecked = tile.design.isFullSpan
    }

    override fun collectTile(): Tile {
        return oldTile?.copy(
            name = name.text,
            subscribeTopic = "",
            publishTopic = publishTopic.text,
            payload = payload.text,
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.BUTTON,
            stateList = emptyList(),
            design = Tile.Design(
                styleId = style.selectedValue,
                isFullSpan = width.isChecked,
            ),
        ) ?: Tile(
            name = name.text,
            subscribeTopic = "",
            publishTopic = publishTopic.text,
            payload = payload.text,
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.BUTTON,
            stateList = emptyList(),
            dashboardPosition = dashboardPosition,
            design = Tile.Design(
                styleId = style.selectedValue,
                isFullSpan = width.isChecked,
            ),
        )
    }

    override fun list(): List<ListItem> {
        return listOf(
            name,
            publishTopic,
            payload,
            qos,
            retain,
            style,
            width,
            save,
        )
    }
}