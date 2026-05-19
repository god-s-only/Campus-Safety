package com.caleb.campussafety.report.domain.usecase

import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMyIncidentsUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    operator fun invoke(userId: String): Flow<List<Incident>> =
        repository.getIncidents().map { incidents ->
            incidents.filter { it.reporterId == userId }
        }
}