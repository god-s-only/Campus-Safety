package com.caleb.campussafety.report.domain.usecase

import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.model.IncidentCategory
import com.caleb.campussafety.report.domain.repository.ReportRepository

class SubmitReportUseCase(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(incident: Incident): Result<Unit> {
        if (incident.description.isBlank())
            return Result.failure(Exception("Description cannot be empty"))
        if (incident.description.length < 10)
            return Result.failure(Exception("Description is too short"))
        if (incident.location.latitude == 0.0 && incident.location.longitude == 0.0)
            return Result.failure(Exception("Location not detected yet"))
        return repository.submitReport(incident)
    }
}