package com.caleb.campussafety.report.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caleb.campussafety.report.domain.model.IncidentCategory
import com.caleb.campussafety.report.domain.model.IncidentStatus
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(
    state: IncidentDetailState,
    actions: Flow<IncidentDetailAction>,
    onEvent: (IncidentDetailEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        actions.collect { action ->
            when (action) {
                is IncidentDetailAction.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Incident Detail",
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val incident = state.incident
        if (incident == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Incident not found",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status banner
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = when (incident.status) {
                    IncidentStatus.PENDING -> MaterialTheme.colorScheme.errorContainer
                    IncidentStatus.ACKNOWLEDGED -> MaterialTheme.colorScheme.tertiaryContainer
                    IncidentStatus.RESOLVED -> MaterialTheme.colorScheme.primaryContainer
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (incident.status) {
                            IncidentStatus.PENDING -> Icons.Default.HourglassEmpty
                            IncidentStatus.ACKNOWLEDGED -> Icons.Default.ThumbUp
                            IncidentStatus.RESOLVED -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = when (incident.status) {
                            IncidentStatus.PENDING ->
                                MaterialTheme.colorScheme.onErrorContainer
                            IncidentStatus.ACKNOWLEDGED ->
                                MaterialTheme.colorScheme.onTertiaryContainer
                            IncidentStatus.RESOLVED ->
                                MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = incident.status.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (incident.status) {
                                IncidentStatus.PENDING ->
                                    MaterialTheme.colorScheme.onErrorContainer
                                IncidentStatus.ACKNOWLEDGED ->
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                IncidentStatus.RESOLVED ->
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                        Text(
                            text = when (incident.status) {
                                IncidentStatus.PENDING ->
                                    "Awaiting security response"
                                IncidentStatus.ACKNOWLEDGED ->
                                    "Security officer is responding"
                                IncidentStatus.RESOLVED ->
                                    "Incident has been resolved"
                            },
                            fontSize = 12.sp,
                            color = when (incident.status) {
                                IncidentStatus.PENDING ->
                                    MaterialTheme.colorScheme.onErrorContainer
                                IncidentStatus.ACKNOWLEDGED ->
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                IncidentStatus.RESOLVED ->
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    }
                }
            }

            // Incident info card
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(
                        icon = Icons.Default.Warning,
                        label = "Category",
                        value = incident.category.name
                            .replace("_", " ")
                            .lowercase()
                            .replaceFirstChar { it.uppercase() }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    DetailRow(
                        icon = Icons.Default.Person,
                        label = "Reported by",
                        value = incident.reporterName
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    DetailRow(
                        icon = Icons.Default.Schedule,
                        label = "Date & time",
                        value = dateFormat.format(Date(incident.timestamp))
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "Location",
                        value = "%.6f, %.6f".format(
                            incident.location.latitude,
                            incident.location.longitude
                        )
                    )
                }
            }

            // Description card
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Description",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = incident.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }

            // Action buttons for security officers
            if (state.isSecurityOfficer &&
                incident.status != IncidentStatus.RESOLVED
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (incident.status == IncidentStatus.PENDING) {
                        OutlinedButton(
                            onClick = {
                                onEvent(
                                    IncidentDetailEvent.OnUpdateStatus(
                                        IncidentStatus.ACKNOWLEDGED
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Acknowledge Incident",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Button(
                        onClick = {
                            onEvent(
                                IncidentDetailEvent.OnUpdateStatus(
                                    IncidentStatus.RESOLVED
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mark as Resolved",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}