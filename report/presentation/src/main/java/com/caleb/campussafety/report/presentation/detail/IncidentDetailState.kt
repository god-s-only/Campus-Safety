package com.caleb.campussafety.report.presentation.detail

import com.caleb.campussafety.report.domain.model.Incident

data class IncidentDetailState(
    val incident: Incident? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSecurityOfficer: Boolean = false
)