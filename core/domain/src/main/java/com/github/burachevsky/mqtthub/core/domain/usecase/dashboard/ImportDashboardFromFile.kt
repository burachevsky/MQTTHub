package com.github.burachevsky.mqtthub.core.domain.usecase.dashboard

import android.net.Uri
import com.github.burachevsky.mqtthub.core.data.repository.CurrentIdsRepository
import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.data.repository.FileRepository
import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImportDashboardFromFile @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val fileRepository: FileRepository,
    private val currentIdsRepository: CurrentIdsRepository,
    private val tileRepository: TileRepository,
) {

    suspend operator fun invoke(contentUri: Uri) {
        withContext(Dispatchers.IO) {
            val (dashboardId, importedTiles) = dashboardRepository.importDashboardFromJson(
                json = fileRepository.getContent(contentUri),
            )

            tileRepository.insertTiles(importedTiles)
            currentIdsRepository.updateCurrentDashboard(dashboardId)
        }
    }
}