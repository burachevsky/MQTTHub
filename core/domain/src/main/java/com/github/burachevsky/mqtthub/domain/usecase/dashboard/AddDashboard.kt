package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.database.entity.dashboard.Dashboard
import javax.inject.Inject

class AddDashboard @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(dashboard: Dashboard): Dashboard {
        return dashboardRepository.insertDashboard(dashboard)
    }
}