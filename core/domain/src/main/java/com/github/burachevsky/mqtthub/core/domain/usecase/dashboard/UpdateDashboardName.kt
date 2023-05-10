package com.github.burachevsky.mqtthub.core.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import javax.inject.Inject

class UpdateDashboardName @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(dashboardId: Long, name: String) {
        dashboardRepository.updateDashboardName(dashboardId, name)
    }
}