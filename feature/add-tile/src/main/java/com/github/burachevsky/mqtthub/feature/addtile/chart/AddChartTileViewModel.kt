package com.github.burachevsky.mqtthub.feature.addtile.chart

import com.github.burachevsky.mqtthub.core.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.ObserveTile
import com.github.burachevsky.mqtthub.core.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.model.TileStyleId
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg.DASHBOARD_ID
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg.DASHBOARD_POSITION
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg.TILE_ID
import com.github.burachevsky.mqtthub.core.ui.event.AlertDialog
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleOption
import com.github.burachevsky.mqtthub.feature.addtile.AddTileViewModel
import com.github.burachevsky.mqtthub.feature.addtile.QosId
import javax.inject.Inject
import javax.inject.Named

class AddChartTileViewModel@Inject constructor(
    @Named(DASHBOARD_ID) dashboardId: Long,
    @Named(TILE_ID) tileId: Long,
    @Named(DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    observeTile: ObserveTile,
    updateTile: UpdateTile,
    addTile: AddTile,
) : AddTileViewModel(
    eventBus, observeTile, updateTile, addTile,
    dashboardId, tileId, dashboardPosition
) {

    override val title: Int = if (isEditMode) R.string.edit_chart else R.string.new_chart

    override val showHelpButton = true

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

    override fun showHelp() {
        container.raiseEffect {
            AlertDialog(
                title = Txt.of(R.string.chart_help_title),
                message = Txt.of(R.string.chart_help_message),
                yes = AlertDialog.Button(Txt.of(R.string.button_ok)),
            )
        }
    }

    override fun collectTile(): Tile {
        return itemStore.run {
            (tile.value ?: Tile()).copy(
                name = name.text,
                subscribeTopic = subscribeTopic.text,
                qos = qos.selectedValue,
                retained = retain.isChecked,
                dashboardId = dashboardId,
                type = Tile.Type.CHART,
                stateList = emptyList(),
                dashboardPosition = dashboardPosition,
                design = Tile.Design(
                    styleId = style.selectedValue,
                    isFullSpan = true,
                ),
            )
        }
    }

    override fun makeItemsListFromTile(tile: Tile?): List<ListItem> {
        return itemStore.run {
            name.text = tile?.name.orEmpty()
            subscribeTopic.text = tile?.subscribeTopic.orEmpty()
            retain.isChecked = tile?.retained ?: false
            qos.selectedValue = tile?.qos ?: QosId.Qos0
            style.selectedValue = tile?.design?.styleId ?: TileStyleId.OUTLINED

            makeItemsList()
        }
    }

    override fun makeItemsList(): List<ListItem> {
        return itemStore.run {
            listOf(name, subscribeTopic, retain, qos, style, save)
        }
    }
}