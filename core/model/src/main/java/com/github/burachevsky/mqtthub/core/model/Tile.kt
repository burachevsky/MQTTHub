package com.github.burachevsky.mqtthub.core.model

data class Tile(
    val id: Long = 0,
    val name: String = "",
    val subscribeTopic: String = "",
    val publishTopic: String = "",
    val qos: Int = 0,
    val retained: Boolean = false,
    val type: Type = Type.TEXT,
    val payload: Payload = StringPayload(),
    val notifyPayloadUpdate: Boolean = false,
    val stateList: List<State> = listOf(),
    val dashboardId: Long = 0,
    val dashboardPosition: Int = 0,
    val design: Design = Design()
) {

    enum class Type {
        TEXT, BUTTON, SWITCH, CHART, SLIDER
    }

    data class State(
        val id: Int,
        val payload: String
    )

    data class Design(
        val styleId: Int = 0,

        val sizeId: Int = 0,

        val isFullSpan: Boolean = false,
    )
}
