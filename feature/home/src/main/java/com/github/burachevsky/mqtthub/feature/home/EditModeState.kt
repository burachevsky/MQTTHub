package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile

data class EditModeState(
    val isEditMode: Boolean = false,
    val selectedTiles: HashSet<Tile> = HashSet(),
    val selectedCount: Int = 0,
    val canMoveItem: Boolean = true,
    val isMovingMode: Boolean = false
)
