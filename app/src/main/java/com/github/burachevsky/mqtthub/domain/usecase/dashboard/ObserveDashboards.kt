package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.data.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDashboards @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    operator fun invoke(): Flow<List<Dashboard>> {
        return dashboardRepository.observeDashboards()
    }
}