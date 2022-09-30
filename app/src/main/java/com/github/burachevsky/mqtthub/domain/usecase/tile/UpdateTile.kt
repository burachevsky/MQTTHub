package com.github.burachevsky.mqtthub.domain.usecase.tile

import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.data.repository.TileRepository

class UpdateTile(
    private val tileRepository: TileRepository
) {
    suspend operator fun invoke(tile: Tile) {
        return tileRepository.updateTile(tile)
    }
}