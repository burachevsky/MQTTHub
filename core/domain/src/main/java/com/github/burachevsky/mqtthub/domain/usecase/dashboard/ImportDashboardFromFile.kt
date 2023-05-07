package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import android.net.Uri
import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.data.repository.FileRepository
import com.github.burachevsky.mqtthub.core.model.Dashboard
import javax.inject.Inject

class ImportDashboardFromFile @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val fileRepository: FileRepository,
) {

    suspend operator fun invoke(contentUri: Uri): Dashboard {
        return dashboardRepository.importDashboardFromJson(
            json = fileRepository.getContent(contentUri)
        )
    }
}