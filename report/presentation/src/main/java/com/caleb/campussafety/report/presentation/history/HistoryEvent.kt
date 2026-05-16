package com.caleb.campussafety.report.presentation.history

import com.caleb.campussafety.report.domain.model.IncidentStatus

sealed class HistoryEvent {
    data class OnFilterChange(val status: IncidentStatus?) : HistoryEvent()
    data class OnSearchQueryChange(val query: String) : HistoryEvent()
    data class OnIncidentClick(val incidentId: String) : HistoryEvent()
}