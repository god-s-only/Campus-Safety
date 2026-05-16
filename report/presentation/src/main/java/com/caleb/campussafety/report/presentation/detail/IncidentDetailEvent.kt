package com.caleb.campussafety.report.presentation.detail

import com.caleb.campussafety.report.domain.model.IncidentStatus

sealed class IncidentDetailEvent {
    data class OnUpdateStatus(val status: IncidentStatus) : IncidentDetailEvent()
}