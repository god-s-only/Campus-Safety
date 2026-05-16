package com.caleb.campussafety.report.presentation.home

import com.caleb.campussafety.report.domain.model.Incident

data class StudentHomeState(
    val recentIncidents: List<Incident> = emptyList(),
    val isLoading: Boolean = false,
    val userName: String = "",
    val errorMessage: String? = null
)