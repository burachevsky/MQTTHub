package com.github.burachevsky.mqtthub.feature.addtile.slider

import com.github.burachevsky.mqtthub.core.database.entity.tile.SLIDER_MAX
import com.github.burachevsky.mqtthub.core.database.entity.tile.SLIDER_MIN
import com.github.burachevsky.mqtthub.core.database.entity.tile.SLIDER_STEP
import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile
import com.github.burachevsky.mqtthub.core.database.entity.tile.TileStyleId
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import com.github.burachevsky.mqtthub.core.ui.ext.getPayload
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.widget.FieldType
import com.github.burachevsky.mqtthub.core.ui.widget.InputFieldItem
import com.github.burachevsky.mqtthub.core.ui.widget.SwitchItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleOption
import com.github.burachevsky.mqtthub.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.GetTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.feature.addtile.AddTileViewModel
import javax.inject.Inject
import javax.inject.Named

class AddSliderTileViewModel@Inject constructor(
    @Named(NavArg.DASHBOARD_ID) dashboardId: Long,
    @Named(NavArg.TILE_ID) tileId: Long,
    @Named(NavArg.DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    getTile: GetTile,
    updateTile: UpdateTile,
    addTile: AddTile,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, dashboardId, tileId, dashboardPosition) {

    override val title = if (isEditMode()) R.string.edit_slider else R.string.new_slider

    private val enablePublishing = SwitchItem(
        text = Txt.of(R.string.enable_publishing),
        onCheckChanged = { showPublishingField() }
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

    private val minValue = InputFieldItem(
        label = Txt.of(R.string.slider_tile_min_value),
        type = FieldType.NUMBER,
    )

    private val maxValue = InputFieldItem(
        label = Txt.of(R.string.slider_tile_max_value),
        type = FieldType.NUMBER,
    )

    private val sliderStepsEnabled = SwitchItem(
        text = Txt.of(R.string.slider_tile_steps_enabled),
        onCheckChanged = { showStepField() }
    )

    private val step = InputFieldItem(
        label = Txt.of(R.string.slider_tile_step),
        type = FieldType.NUMBER,
    )

    private val width = SwitchItem(
        text = Txt.of(R.string.tile_fills_screen_width)
    )

    init {
        init()
    }

    override fun initFields(tile: Tile) {
        name.text = tile.name
        subscribeTopic.text = tile.subscribeTopic
        publishTopic.text = tile.publishTopic
        enablePublishing.isChecked = tile.publishTopic.isNotEmpty()
        minValue.text = tile.stateList.getPayload(SLIDER_MIN) ?: ""
        maxValue.text = tile.stateList.getPayload(SLIDER_MAX) ?: ""
        step.text = tile.stateList.getPayload(SLIDER_STEP) ?: ""
        sliderStepsEnabled.isChecked = step.text.isNotEmpty()
        retain.isChecked = tile.retained
        qos.selectedValue = tile.qos
        width.isChecked = tile.design.isFullSpan
        style.selectedValue = tile.design.styleId
    }

    override fun list(): List<ListItem> {
        return listOfNotNull(
            name,
            subscribeTopic,
            if (enablePublishing.isChecked) publishTopic else null,
            enablePublishing,
            minValue,
            maxValue,
            sliderStepsEnabled,
            if (sliderStepsEnabled.isChecked) step else null,
            retain,
            qos,
            style,
            width,
            save,
        )
    }

    override fun collectTile(): Tile { Tile()
        return (oldTile ?: Tile()).copy(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = if (enablePublishing.isChecked) publishTopic.text else "",
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.SLIDER,
            stateList = listOf(
                Tile.State(SLIDER_MIN, minValue.text),
                Tile.State(SLIDER_MAX, maxValue.text),
                Tile.State(SLIDER_STEP, step.text)
            ),
            dashboardPosition = dashboardPosition,
            design = Tile.Design(
                styleId = style.selectedValue,
                isFullSpan = width.isChecked,
            ),
        )
    }

    private fun showPublishingField() {
        if (publishTopic.text.isEmpty()) {
            publishTopic.text = subscribeTopic.text
        }

        update()
    }

    private fun showStepField() {
        update()
    }
}