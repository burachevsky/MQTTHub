package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.data.repository.DashboardRepository
import javax.inject.Inject

class DeleteDashboard @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(id: Long) {
        return dashboardRepository.deleteDashboard(id)
    }
}