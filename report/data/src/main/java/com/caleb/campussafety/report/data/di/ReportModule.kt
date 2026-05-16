package com.caleb.campussafety.report.data.di

import com.caleb.campussafety.report.data.repository.ReportRepositoryImpl
import com.caleb.campussafety.report.domain.repository.ReportRepository
import com.caleb.campussafety.report.domain.usecase.GetCurrentLocationUseCase
import com.caleb.campussafety.report.domain.usecase.GetIncidentsUseCase
import com.caleb.campussafety.report.domain.usecase.SubmitReportUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportModule {

    @Provides
    @Singleton
    fun provideReportRepository(
        impl: ReportRepositoryImpl
    ): ReportRepository = impl

    @Provides
    @Singleton
    fun provideSubmitReportUseCase(
        repository: ReportRepository
    ): SubmitReportUseCase = SubmitReportUseCase(repository)

    @Provides
    @Singleton
    fun provideGetIncidentsUseCase(
        repository: ReportRepository
    ): GetIncidentsUseCase = GetIncidentsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentLocationUseCase(
        repository: ReportRepository
    ): GetCurrentLocationUseCase = GetCurrentLocationUseCase(repository)
}