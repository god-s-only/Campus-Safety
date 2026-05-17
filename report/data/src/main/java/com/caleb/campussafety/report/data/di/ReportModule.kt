package com.caleb.campussafety.report.data.di

import com.caleb.campussafety.report.data.repository.ReportRepositoryImpl
import com.caleb.campussafety.report.domain.repository.ReportRepository
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
}