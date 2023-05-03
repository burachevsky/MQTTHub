package com.github.burachevsky.mqtthub.domain.usecase.tile

import com.github.burachevsky.mqtthub.data.entity.SimpleTile
import com.github.burachevsky.mqtthub.data.repository.TileRepository
import javax.inject.Inject

class UpdatePayloadAndGetTilesToNotify @Inject constructor(
    private val tileRepository: TileRepository
) {

    suspend operator fun invoke(subscribeTopic: String, payload: String): List<SimpleTile> {
        return tileRepository.updatePayloadAndGetTilesToNotify(subscribeTopic, payload)
    }
}