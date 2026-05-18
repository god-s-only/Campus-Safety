package com.caleb.campussafety.report.presentation.home

sealed class StudentHomeAction {
    object NavigateToReport : StudentHomeAction()
    object NavigateToHistory : StudentHomeAction()
    object NavigateToLogin : StudentHomeAction()
    data class NavigateToIncidentDetail(val incidentId: String) : StudentHomeAction()
}