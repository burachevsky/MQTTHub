package com.github.burachevsky.mqtthub.domain.usecase.tile

import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.data.repository.TileRepository
import javax.inject.Inject

class GetDashboardTiles @Inject constructor(
    private val tileRepository: TileRepository,
) {

    suspend operator fun invoke(dashboardId: Long): List<Tile> {
        return tileRepository.getDashboardTiles(dashboardId)
    }
}