package com.github.burachevsky.mqtthub.feature.addtile.button

import com.github.burachevsky.mqtthub.core.model.BUTTON
import com.github.burachevsky.mqtthub.core.model.StringPayload
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.model.TileStyleId
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import com.github.burachevsky.mqtthub.core.ui.ext.getPayload
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.widget.InputFieldItem
import com.github.burachevsky.mqtthub.core.ui.widget.SwitchItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleOption
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.GetTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.feature.addtile.AddTileViewModel
import javax.inject.Inject
import javax.inject.Named

class AddButtonTileViewModel @Inject constructor(
    @Named(NavArg.DASHBOARD_ID) dashboardId: Long,
    @Named(NavArg.TILE_ID) tileId: Long,
    @Named(NavArg.DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: com.github.burachevsky.mqtthub.core.eventbus.EventBus,
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
                id = TileStyleId.OUTLINED,
                text = Txt.of(R.string.tile_style_outlined)
            ),
            ToggleOption(
                id = TileStyleId.FILLED,
                text = Txt.of(R.string.tile_style_filled)
            ),
        ),
        selectedValue = TileStyleId.OUTLINED
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
        payload.text = tile.stateList.getPayload(BUTTON).orEmpty()
        retain.isChecked = tile.retained
        qos.selectedValue = tile.qos
        style.selectedValue = tile.design.styleId
        width.isChecked = tile.design.isFullSpan
    }

    override fun collectTile(): Tile {
        return (oldTile ?: Tile()).copy(
            name = name.text,
            subscribeTopic = "",
            publishTopic = publishTopic.text,
            payload = StringPayload(),
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.BUTTON,
            stateList = listOf(Tile.State(BUTTON, payload.text)),
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