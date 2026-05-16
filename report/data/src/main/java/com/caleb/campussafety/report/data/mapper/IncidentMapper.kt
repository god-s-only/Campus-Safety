package com.caleb.campussafety.report.data.mapper

import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.model.IncidentCategory
import com.caleb.campussafety.report.domain.model.IncidentStatus
import com.caleb.campussafety.report.domain.model.Location

fun Map<String, Any?>.toIncident(id: String): Incident {
    val locationMap = this["location"] as? Map<*, *>
    return Incident(
        id = id,
        reporterId = this["reporterId"] as? String ?: "",
        reporterName = this["reporterName"] as? String ?: "",
        category = try {
            IncidentCategory.valueOf(this["category"] as? String ?: "OTHER")
        } catch (e: Exception) {
            IncidentCategory.OTHER
        },
        description = this["description"] as? String ?: "",
        location = Location(
            latitude = (locationMap?.get("latitude") as? Double) ?: 0.0,
            longitude = (locationMap?.get("longitude") as? Double) ?: 0.0,
            address = (locationMap?.get("address") as? String) ?: ""
        ),
        status = try {
            IncidentStatus.valueOf(this["status"] as? String ?: "PENDING")
        } catch (e: Exception) {
            IncidentStatus.PENDING
        },
        timestamp = (this["timestamp"] as? Long) ?: System.currentTimeMillis(),
        assignedSecurityId = this["assignedSecurityId"] as? String
    )
}

fun Incident.toMap(): Map<String, Any?> {
    return mapOf(
        "reporterId" to reporterId,
        "reporterName" to reporterName,
        "category" to category.name,
        "description" to description,
        "location" to mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "address" to location.address
        ),
        "status" to status.name,
        "timestamp" to timestamp,
        "assignedSecurityId" to assignedSecurityId
    )
}