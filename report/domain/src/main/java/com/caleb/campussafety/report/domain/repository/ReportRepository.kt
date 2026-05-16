package com.caleb.campussafety.report.domain.repository

import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.model.Location
import kotlinx.coroutines.flow.Flow
import com.caleb.campussafety.report.domain.model.IncidentStatus

interface ReportRepository {
    suspend fun submitReport(incident: Incident): Result<Unit>
    fun getIncidents(): Flow<List<Incident>>
    fun getIncidentById(id: String): Flow<Incident?>
    suspend fun getCurrentLocation(): Result<Location>
    suspend fun updateIncidentStatus(
        incidentId: String,
        status: IncidentStatus
    ): Result<Unit>
}