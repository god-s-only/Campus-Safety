package com.caleb.campussafety.report.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.model.IncidentCategory
import com.caleb.campussafety.report.domain.model.IncidentStatus
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    state: StudentHomeState,
    actions: Flow<StudentHomeAction>,
    onEvent: (StudentHomeEvent) -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToIncidentDetail: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        actions.collect { action ->
            when (action) {
                is StudentHomeAction.NavigateToReport -> onNavigateToReport()
                is StudentHomeAction.NavigateToHistory -> onNavigateToHistory()
                is StudentHomeAction.NavigateToIncidentDetail ->
                    onNavigateToIncidentDetail(action.incidentId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Campus Safety",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Bingham University",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = state.userName.firstOrNull()
                                    ?.uppercase() ?: "S",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting
            item {
                Text(
                    text = "Hello, ${state.userName.split(" ").firstOrNull() ?: "Student"} 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Stay safe and report any incident immediately",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Emergency report button
            item {
                Button(
                    onClick = { onEvent(StudentHomeEvent.OnReportIncidentClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Report an Incident",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Tap to report immediately",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onError.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Quick stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AssignmentLate,
                        label = "Pending",
                        count = state.recentIncidents.count {
                            it.status == IncidentStatus.PENDING
                        }
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        label = "Resolved",
                        count = state.recentIncidents.count {
                            it.status == IncidentStatus.RESOLVED
                        }
                    )
                }
            }

            // Recent incidents header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent incidents",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(
                        onClick = { onEvent(StudentHomeEvent.OnViewHistoryClick) }
                    ) {
                        Text(
                            text = "View all",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Loading
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            // Empty state
            if (!state.isLoading && state.recentIncidents.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No incidents reported",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Incident list
            items(state.recentIncidents) { incident ->
                IncidentCard(
                    incident = incident,
                    onClick = { onEvent(StudentHomeEvent.OnIncidentClick(incident.id)) }
                )
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = count.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun IncidentCard(
    incident: Incident,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (incident.status) {
                    IncidentStatus.PENDING -> MaterialTheme.colorScheme.errorContainer
                    IncidentStatus.ACKNOWLEDGED -> MaterialTheme.colorScheme.tertiaryContainer
                    IncidentStatus.RESOLVED -> MaterialTheme.colorScheme.primaryContainer
                },
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (incident.category) {
                            IncidentCategory.THEFT -> Icons.Default.Lock
                            IncidentCategory.HARASSMENT -> Icons.Default.Warning
                            IncidentCategory.ASSAULT -> Icons.Default.PersonOff
                            IncidentCategory.MEDICAL_EMERGENCY -> Icons.Default.LocalHospital
                            IncidentCategory.FIRE -> Icons.Default.LocalFireDepartment
                            IncidentCategory.SUSPICIOUS_ACTIVITY -> Icons.Default.Visibility
                            IncidentCategory.VANDALISM -> Icons.Default.Build
                            IncidentCategory.OTHER -> Icons.Default.MoreHoriz
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = when (incident.status) {
                            IncidentStatus.PENDING -> MaterialTheme.colorScheme.onErrorContainer
                            IncidentStatus.ACKNOWLEDGED -> MaterialTheme.colorScheme.onTertiaryContainer
                            IncidentStatus.RESOLVED -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = incident.category.name
                        .replace("_", " ")
                        .lowercase()
                        .replaceFirstChar { it.uppercase() },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = incident.description.take(50) +
                            if (incident.description.length > 50) "..." else "",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(Date(incident.timestamp)),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status badge
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (incident.status) {
                    IncidentStatus.PENDING -> MaterialTheme.colorScheme.errorContainer
                    IncidentStatus.ACKNOWLEDGED -> MaterialTheme.colorScheme.tertiaryContainer
                    IncidentStatus.RESOLVED -> MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                Text(
                    text = incident.status.name.lowercase()
                        .replaceFirstChar { it.uppercase() },
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = when (incident.status) {
                        IncidentStatus.PENDING -> MaterialTheme.colorScheme.onErrorContainer
                        IncidentStatus.ACKNOWLEDGED -> MaterialTheme.colorScheme.onTertiaryContainer
                        IncidentStatus.RESOLVED -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
        }
    }
}