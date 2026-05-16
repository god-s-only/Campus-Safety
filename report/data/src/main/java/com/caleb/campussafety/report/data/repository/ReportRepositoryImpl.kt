package com.caleb.campussafety.report.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.caleb.campussafety.report.data.mapper.toIncident
import com.caleb.campussafety.report.data.mapper.toMap
import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.model.IncidentStatus
import com.caleb.campussafety.report.domain.model.Location
import com.caleb.campussafety.report.domain.repository.ReportRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) : ReportRepository {

    private val incidentsCollection = firestore.collection("incidents")
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    override suspend fun submitReport(incident: Incident): Result<Unit> {
        return try {
            val docRef = incidentsCollection.document()
            val incidentWithId = incident.copy(id = docRef.id)
            docRef.set(incidentWithId.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getIncidents(): Flow<List<Incident>> = callbackFlow {
        val listener = incidentsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val incidents = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.toIncident(doc.id)
                } ?: emptyList()
                trySend(incidents)
            }
        awaitClose { listener.remove() }
    }

    override fun getIncidentById(id: String): Flow<Incident?> = callbackFlow {
        val listener = incidentsCollection
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val incident = snapshot?.data?.toIncident(snapshot.id)
                trySend(incident)
            }
        awaitClose { listener.remove() }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<Location> {
        return try {
            val cancellationToken = CancellationTokenSource()
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()
            if (location != null) {
                Result.success(
                    Location(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                )
            } else {
                Result.failure(Exception("Unable to get location"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateIncidentStatus(
        incidentId: String,
        status: IncidentStatus
    ): Result<Unit> {
        return try {
            incidentsCollection
                .document(incidentId)
                .update("status", status.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}