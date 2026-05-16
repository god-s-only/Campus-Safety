package com.caleb.campussafety.report.domain.usecase

import com.caleb.campussafety.report.domain.model.Location
import com.caleb.campussafety.report.domain.repository.ReportRepository

class GetCurrentLocationUseCase(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(): Result<Location> = repository.getCurrentLocation()
}