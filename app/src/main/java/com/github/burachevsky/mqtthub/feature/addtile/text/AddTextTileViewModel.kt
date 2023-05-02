package com.github.burachevsky.mqtthub.feature.addtile.text

import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.widget.SwitchItem
import com.github.burachevsky.mqtthub.common.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.common.widget.ToggleOption
import com.github.burachevsky.mqtthub.data.entity.TextTileSizeId
import com.github.burachevsky.mqtthub.data.entity.TileStyleId
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

class AddTextTileViewModel @Inject constructor(
    @Named(DASHBOARD_ID) dashboardId: Long,
    @Named(TILE_ID) tileId: Long,
    @Named(DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    addTile: AddTile,
    updateTile: UpdateTile,
    getTile: GetTile,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, dashboardId, tileId, dashboardPosition) {

    override val title: Int = if (isEditMode()) R.string.edit_text_tile else R.string.new_text_tile

    private val enablePublishing = SwitchItem(
        text = Txt.of(R.string.enable_publishing),
        onCheckChanged = { showPublishingField() }
    )

    private val size = ToggleGroupItem(
        title = Txt.of(R.string.tile_size),
        options = listOf(
            ToggleOption(
                id = TextTileSizeId.SMALL,
                text = Txt.of(R.string.text_tile_size_small)
            ),
            ToggleOption(
                id = TextTileSizeId.MEDIUM,
                text = Txt.of(R.string.text_tile_size_medium)
            ),
            ToggleOption(
                id = TextTileSizeId.LARGE,
                text = Txt.of(R.string.text_tile_size_large)
            ),
        ),
        selectedValue = TextTileSizeId.SMALL
    )

    private val width = SwitchItem(
        text = Txt.of(R.string.tile_fills_screen_width)
    )

    private val style = ToggleGroupItem(
        title = Txt.of(R.string.tile_background_style),
        options = listOf(
            ToggleOption(
                id = TileStyleId.OUTLINED,
                text = Txt.of(R.string.tile_style_outlined),
            ),
            ToggleOption(
                id = TileStyleId.FILLED,
                text = Txt.of(R.string.tile_style_filled),
            ),
            ToggleOption(
                id = TileStyleId.EMPTY,
                text = Txt.of(R.string.tile_style_empty),
            ),
        ),
        selectedValue = TileStyleId.OUTLINED,
    )

    init {
        init()
    }

    private fun showPublishingField() {
        if (publishTopic.text.isEmpty()) {
            publishTopic.text = subscribeTopic.text
        }

        update()
    }

    override fun initFields(tile: Tile) {
        name.text = tile.name
        subscribeTopic.text = tile.subscribeTopic
        enablePublishing.isChecked = tile.publishTopic.isNotEmpty()
        publishTopic.text = tile.publishTopic
        retain.isChecked = tile.retained
        qos.selectedValue = tile.qos
        notifyPayloadUpdate.isChecked = tile.notifyPayloadUpdate
        size.selectedValue = tile.design.sizeId
        style.selectedValue = tile.design.styleId
        width.isChecked = tile.design.isFullSpan
    }

    override fun collectTile(): Tile {
        return (oldTile ?: Tile()).copy(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = if (enablePublishing.isChecked) publishTopic.text else "",
            qos = qos.selectedValue,
            retained = retain.isChecked,
            notifyPayloadUpdate = notifyPayloadUpdate.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.TEXT,
            stateList = emptyList(),
            dashboardPosition = dashboardPosition,
            design = Tile.Design(
                styleId = style.selectedValue,
                sizeId = size.selectedValue,
                isFullSpan = width.isChecked,
            ),
        )
    }

    override fun list(): List<ListItem> {
        return listOfNotNull(
            name,
            subscribeTopic,
            if (enablePublishing.isChecked) publishTopic else null,
            enablePublishing,
            qos,
            retain,
            notifyPayloadUpdate,
            size,
            width,
            style,
            save,
        )
    }
}