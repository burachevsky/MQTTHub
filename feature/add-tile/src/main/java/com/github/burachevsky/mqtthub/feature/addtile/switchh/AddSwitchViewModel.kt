package com.github.burachevsky.mqtthub.feature.addtile.switchh

import com.github.burachevsky.mqtthub.core.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.ObserveTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.model.SWITCH_OFF
import com.github.burachevsky.mqtthub.core.model.SWITCH_ON
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
import com.github.burachevsky.mqtthub.feature.addtile.AddTileViewModel
import com.github.burachevsky.mqtthub.feature.addtile.QosId
import javax.inject.Inject
import javax.inject.Named

class AddSwitchViewModel @Inject constructor(
    @Named(NavArg.DASHBOARD_ID) dashboardId: Long,
    @Named(NavArg.TILE_ID) tileId: Long,
    @Named(NavArg.DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    observeTile: ObserveTile,
    updateTile: UpdateTile,
    addTile: AddTile,
) : AddTileViewModel(
    eventBus, observeTile, updateTile, addTile,
    dashboardId, tileId, dashboardPosition
) {

    override val title = if (isEditMode) R.string.edit_switch else R.string.new_switch

    private val enablePublishing = SwitchItem(
        text = Txt.of(R.string.enable_publishing),
        onCheckChanged = { showPublishingField() }
    )

    private val onState = InputFieldItem(
        label = Txt.of(R.string.on_state),
    )

    private val offState = InputFieldItem(
        label = Txt.of(R.string.off_state),
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
                dashboardId = dashboardId,
                type = Tile.Type.SWITCH,
                stateList = listOf(
                    Tile.State(SWITCH_ON, onState.text),
                    Tile.State(SWITCH_OFF, offState.text)
                ),
                dashboardPosition = dashboardPosition,
                design = Tile.Design(
                    styleId = style.selectedValue,
                    isFullSpan = width.isChecked,
                ),
            )
        }
    }

    override fun makeItemsListFromTile(tile: Tile?): List<ListItem> {
        return itemStore.run {
            name.text = tile?.name.orEmpty()
            subscribeTopic.text = tile?.subscribeTopic.orEmpty()
            publishTopic.text = tile?.publishTopic.orEmpty()
            enablePublishing.isChecked = tile?.publishTopic?.isNotEmpty() ?: false
            onState.text = tile?.stateList?.getPayload(SWITCH_ON).orEmpty()
            offState.text = tile?.stateList?.getPayload(SWITCH_OFF).orEmpty()
            retain.isChecked = tile?.retained ?: false
            qos.selectedValue = tile?.qos ?: QosId.Qos0
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
                onState,
                offState,
                qos,
                retain,
                style,
                width,
                save,
            )
        }
    }
}