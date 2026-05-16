package com.caleb.campussafety.report.presentation.report

import com.caleb.campussafety.report.domain.model.IncidentCategory

sealed class ReportEvent {
    data class OnCategoryChange(val category: IncidentCategory) : ReportEvent()
    data class OnDescriptionChange(val description: String) : ReportEvent()
    object OnFetchLocation : ReportEvent()
    object OnSubmitReport : ReportEvent()
    data class OnLocationPermissionResult(val granted: Boolean) : ReportEvent()
}