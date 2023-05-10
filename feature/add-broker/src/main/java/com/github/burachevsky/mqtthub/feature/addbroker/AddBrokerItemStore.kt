package com.github.burachevsky.mqtthub.feature.addbroker

import com.github.burachevsky.mqtthub.core.domain.usecase.broker.GenerateClientId
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.text.EmptyTxt
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import com.github.burachevsky.mqtthub.core.ui.text.withArgs
import com.github.burachevsky.mqtthub.core.ui.widget.ButtonItem
import com.github.burachevsky.mqtthub.core.ui.widget.FieldType
import com.github.burachevsky.mqtthub.core.ui.widget.InputFieldItem
import javax.inject.Inject


class AddBrokerItemStore @Inject constructor(
    brokerId: Long,
    generateClientId: GenerateClientId,
) {
    private val isEditMode = brokerId > 0

    val name = InputFieldItem(
        label = Txt.of(R.string.broker_name),
    )

    val address = InputFieldItem(
        label = Txt.of(R.string.broker_address),
        placeholder = Txt.of(R.string.broker_address_hint),
        type = FieldType.URI,
    )

    val port = InputFieldItem(
        initText = run {
            if (isEditMode) EmptyTxt else Txt.of(R.string.broker_default_port)
        },
        label = Txt.of(R.string.broker_port),
        placeholder = Txt.of(R.string.broker_default_port),
        type = FieldType.NUMBER,
    )

    val clientId = InputFieldItem(
        initText = run {
            if (isEditMode) EmptyTxt
            else Txt.of(R.string.broker_default_client_id)
                .withArgs(generateClientId())
        },
        label = Txt.of(R.string.broker_client_id),
    )

    val save = ButtonItem(Txt.of(R.string.save))
}