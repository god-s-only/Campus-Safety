package com.caleb.campussafety.report.presentation.report

import com.caleb.campussafety.report.domain.model.IncidentCategory
import com.caleb.campussafety.report.domain.model.Location

data class ReportState(
    val category: IncidentCategory = IncidentCategory.OTHER,
    val description: String = "",
    val location: Location? = null,
    val isLoadingLocation: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isLocationPermissionGranted: Boolean = false
)