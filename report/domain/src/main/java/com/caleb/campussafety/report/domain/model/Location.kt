package com.caleb.campussafety.report.domain.model

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String = ""
)