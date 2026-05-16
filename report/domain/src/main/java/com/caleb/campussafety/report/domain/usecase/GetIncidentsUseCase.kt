package com.caleb.campussafety.report.domain.usecase

import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow

class GetIncidentsUseCase(
    private val repository: ReportRepository
) {
    operator fun invoke(): Flow<List<Incident>> = repository.getIncidents()
}