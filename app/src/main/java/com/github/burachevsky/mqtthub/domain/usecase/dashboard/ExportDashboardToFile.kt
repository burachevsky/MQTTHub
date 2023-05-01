package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import android.net.Uri
import com.github.burachevsky.mqtthub.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.data.repository.FileRepository
import javax.inject.Inject

class ExportDashboardToFile @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val fileRepository: FileRepository,
){

    suspend operator fun invoke(dashboardId: Long, contentUri: Uri) {
        return fileRepository.saveContent(
            content = dashboardRepository.packDashboardForExport(dashboardId),
            uri = contentUri
        )
    }
}