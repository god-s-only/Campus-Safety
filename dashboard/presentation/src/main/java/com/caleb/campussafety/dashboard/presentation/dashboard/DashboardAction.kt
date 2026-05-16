package com.caleb.campussafety.dashboard.presentation.dashboard

sealed class DashboardAction {
    data class NavigateToIncidentDetail(val incidentId: String) : DashboardAction()
}