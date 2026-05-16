package com.caleb.campussafety.report.presentation.history

import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.model.IncidentStatus

data class HistoryState(
    val incidents: List<Incident> = emptyList(),
    val filteredIncidents: List<Incident> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedFilter: IncidentStatus? = null,
    val searchQuery: String = ""
)