package com.caleb.campussafety.report.presentation.history

sealed class HistoryAction {
    data class NavigateToIncidentDetail(val incidentId: String) : HistoryAction()
}