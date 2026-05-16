package com.caleb.campussafety.report.presentation.report

sealed class ReportAction {
    object ReportSubmittedSuccessfully : ReportAction()
    data class ShowError(val message: String) : ReportAction()
}