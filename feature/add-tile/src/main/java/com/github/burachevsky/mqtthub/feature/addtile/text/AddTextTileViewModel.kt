package com.github.burachevsky.mqtthub.feature.addtile.text

import com.github.burachevsky.mqtthub.core.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.ObserveTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.model.TextTileSizeId
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.model.TileStyleId
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.widget.SwitchItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleOption
import com.github.burachevsky.mqtthub.feature.addtile.AddTileViewModel
import com.github.burachevsky.mqtthub.feature.addtile.QosId
import javax.inject.Inject
import javax.inject.Named

class AddTextTileViewModel @Inject constructor(
    @Named(NavArg.DASHBOARD_ID) dashboardId: Long,
    @Named(NavArg.TILE_ID) tileId: Long,
    @Named(NavArg.DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    addTile: AddTile,
    updateTile: UpdateTile,
    observeTile: ObserveTile,
) : AddTileViewModel(
    eventBus, observeTile, updateTile, addTile,
    dashboardId, tileId, dashboardPosition
) {

    override val title: Int = if (isEditMode) R.string.edit_text_tile else R.string.new_text_tile

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

    private fun showPublishingField() {
        itemStore.run {
            if (publishTopic.text.isEmpty()) {
                publishTopic.text = subscribeTopic.text
            }
        }

        update()
    }

    override fun collectTile(): Tile {
        return itemStore.run {
            (tile.value ?: Tile()).copy(
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
    }

    override fun makeItemsListFromTile(tile: Tile?): List<ListItem> {
        return itemStore.run {
            name.text = tile?.name.orEmpty()
            subscribeTopic.text = tile?.subscribeTopic.orEmpty()
            enablePublishing.isChecked = tile?.publishTopic?.isNotEmpty() ?: false
            publishTopic.text = tile?.publishTopic.orEmpty()
            retain.isChecked = tile?.retained ?: false
            qos.selectedValue = tile?.qos ?: QosId.Qos0
            notifyPayloadUpdate.isChecked = tile?.notifyPayloadUpdate ?: false
            size.selectedValue = tile?.design?.sizeId ?: TextTileSizeId.SMALL
            style.selectedValue = tile?.design?.styleId ?: TileStyleId.OUTLINED
            width.isChecked = tile?.design?.isFullSpan ?: false

            makeItemsList()
        }
    }

    override fun makeItemsList(): List<ListItem> {
        return itemStore.run {
            listOfNotNull(
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
}