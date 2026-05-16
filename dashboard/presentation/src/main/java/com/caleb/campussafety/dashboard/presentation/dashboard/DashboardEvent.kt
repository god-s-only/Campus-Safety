package com.caleb.campussafety.dashboard.presentation.dashboard

import com.caleb.campussafety.report.domain.model.IncidentStatus

sealed class DashboardEvent {
    data class OnFilterChange(val status: IncidentStatus?) : DashboardEvent()
    data class OnIncidentClick(val incidentId: String) : DashboardEvent()
    data class OnUpdateStatus(
        val incidentId: String,
        val status: IncidentStatus
    ) : DashboardEvent()
}