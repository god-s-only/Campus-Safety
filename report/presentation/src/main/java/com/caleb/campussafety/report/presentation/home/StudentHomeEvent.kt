package com.caleb.campussafety.report.presentation.home

sealed class StudentHomeEvent {
    object OnReportIncidentClick : StudentHomeEvent()
    object OnViewHistoryClick : StudentHomeEvent()
    object OnLogoutClick : StudentHomeEvent()
    data class OnIncidentClick(val incidentId: String) : StudentHomeEvent()
}