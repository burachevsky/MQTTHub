package com.github.burachevsky.mqtthub.core.domain.usecase.tile

import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import com.github.burachevsky.mqtthub.core.model.Tile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTile @Inject constructor(
    private val tileRepository: TileRepository,
) {

    operator fun invoke(tileId: Long): Flow<Tile> {
        return tileRepository.observeTile(tileId)
    }
}