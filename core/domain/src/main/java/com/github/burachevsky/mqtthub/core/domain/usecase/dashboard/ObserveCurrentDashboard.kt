package com.github.burachevsky.mqtthub.core.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.model.Dashboard
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentDashboard @Inject constructor(
    private val dashboardRepository: DashboardRepository,
) {

    operator fun invoke(): Flow<Dashboard> {
        return dashboardRepository.observeCurrentDashboard()
    }
}