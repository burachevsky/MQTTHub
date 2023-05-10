package com.github.burachevsky.mqtthub.feature.addtile

import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.widget.ButtonItem
import com.github.burachevsky.mqtthub.core.ui.widget.FieldType
import com.github.burachevsky.mqtthub.core.ui.widget.InputFieldItem
import com.github.burachevsky.mqtthub.core.ui.widget.SwitchItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleOption

class AddTileItemStore {

    val name = InputFieldItem(
        label = Txt.of(R.string.tile_name)
    )

    val subscribeTopic = InputFieldItem(
        label = Txt.of(R.string.subscribe_topic),
        type = FieldType.URI,
    )

    val publishTopic = InputFieldItem(
        label = Txt.of(R.string.publish_topic),
        type = FieldType.URI,
    )

    val retain = SwitchItem(
        text = Txt.of(R.string.retain)
    )

    val qos = ToggleGroupItem(
        title = Txt.of(R.string.qos),
        options = listOf(
            ToggleOption(
                id = QosId.Qos0,
                text = Txt.of(R.string.qos_0)
            ),
            ToggleOption(
                id = QosId.Qos1,
                text = Txt.of(R.string.qos_1)
            ),
            ToggleOption(
                id = QosId.Qos2,
                text = Txt.of(R.string.qos_2)
            ),
        ),
        selectedValue = QosId.Qos0
    )

    val save = ButtonItem(Txt.of(R.string.save))
}