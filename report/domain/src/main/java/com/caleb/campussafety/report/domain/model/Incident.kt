package com.caleb.campussafety.report.domain.model

data class Incident(
    val id: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val category: IncidentCategory = IncidentCategory.OTHER,
    val description: String = "",
    val location: Location = Location(0.0, 0.0),
    val status: IncidentStatus = IncidentStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val assignedSecurityId: String? = null
)