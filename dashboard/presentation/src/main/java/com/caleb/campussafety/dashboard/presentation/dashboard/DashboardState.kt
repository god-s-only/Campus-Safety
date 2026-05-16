package com.caleb.campussafety.dashboard.presentation.dashboard

import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.model.IncidentStatus

data class DashboardState(
    val incidents: List<Incident> = emptyList(),
    val filteredIncidents: List<Incident> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedFilter: IncidentStatus? = IncidentStatus.PENDING,
    val officerName: String = ""
)