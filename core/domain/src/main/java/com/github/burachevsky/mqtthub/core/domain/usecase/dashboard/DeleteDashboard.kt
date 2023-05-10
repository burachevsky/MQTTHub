package com.github.burachevsky.mqtthub.core.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import javax.inject.Inject

class DeleteDashboard @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(id: Long) {
        return dashboardRepository.deleteDashboard(id)
    }
}