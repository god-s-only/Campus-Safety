package com.caleb.campussafety.dashboard.presentation.dashboard

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.model.IncidentCategory
import com.caleb.campussafety.report.domain.model.IncidentStatus
import kotlinx.coroutines.flow.Flow
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    actions: Flow<DashboardAction>,
    onEvent: (DashboardEvent) -> Unit,
    onNavigateToIncidentDetail: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        actions.collect { action ->
            when (action) {
                is DashboardAction.NavigateToIncidentDetail ->
                    onNavigateToIncidentDetail(action.incidentId)
                is DashboardAction.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Security Dashboard",
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
                                text = state.officerName.firstOrNull()
                                    ?.uppercase() ?: "O",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { onEvent(DashboardEvent.OnLogoutClick) }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
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
                    text = "Hello, ${state.officerName.split(" ").firstOrNull() ?: "Officer"} 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Monitor and respond to campus incidents",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AssignmentLate,
                        label = "Pending",
                        count = state.incidents.count {
                            it.status == IncidentStatus.PENDING
                        },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                    DashboardStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AssignmentTurnedIn,
                        label = "Acknowledged",
                        count = state.incidents.count {
                            it.status == IncidentStatus.ACKNOWLEDGED
                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        label = "Resolved",
                        count = state.incidents.count {
                            it.status == IncidentStatus.RESOLVED
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    DashboardStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.List,
                        label = "Total",
                        count = state.incidents.size,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Filter chips
            item {
                Text(
                    text = "Filter by status",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedFilter == null,
                            onClick = {
                                onEvent(DashboardEvent.OnFilterChange(null))
                            },
                            label = { Text("All", fontSize = 12.sp) }
                        )
                    }
                    items(IncidentStatus.entries) { status ->
                        FilterChip(
                            selected = state.selectedFilter == status,
                            onClick = {
                                onEvent(DashboardEvent.OnFilterChange(status))
                            },
                            label = {
                                Text(
                                    text = status.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }

            // Count
            item {
                Text(
                    text = "${state.filteredIncidents.size} incident(s)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
            if (!state.isLoading && state.filteredIncidents.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
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
                            text = "No incidents found",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Incident list
            items(state.filteredIncidents) { incident ->
                DashboardIncidentCard(
                    incident = incident,
                    onCardClick = {
                        onEvent(DashboardEvent.OnIncidentClick(incident.id))
                    },
                    onAcknowledge = {
                        onEvent(
                            DashboardEvent.OnUpdateStatus(
                                incidentId = incident.id,
                                status = IncidentStatus.ACKNOWLEDGED
                            )
                        )
                    },
                    onResolve = {
                        onEvent(
                            DashboardEvent.OnUpdateStatus(
                                incidentId = incident.id,
                                status = IncidentStatus.RESOLVED
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = count.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun DashboardIncidentCard(
    incident: Incident,
    onCardClick: () -> Unit,
    onAcknowledge: () -> Unit,
    onResolve: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        text = "By ${incident.reporterName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

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
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = incident.description.take(80) +
                        if (incident.description.length > 80) "..." else "",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.4f, %.4f".format(
                        incident.location.latitude,
                        incident.location.longitude
                    ),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = dateFormat.format(Date(incident.timestamp)),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            IncidentMapView(
                latitude = incident.location.latitude,
                longitude = incident.location.longitude,
                title = incident.category.name
                    .replace("_", " ")
                    .lowercase()
                    .replaceFirstChar { it.uppercase() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            // Action buttons
            if (incident.status != IncidentStatus.RESOLVED) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (incident.status == IncidentStatus.PENDING) {
                        OutlinedButton(
                            onClick = onAcknowledge,
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Acknowledge", fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = onResolve,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Resolve", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun IncidentMapView(
    latitude: Double,
    longitude: Double,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
            controller.setCenter(GeoPoint(latitude, longitude))
            val marker = Marker(this)
            marker.position = GeoPoint(latitude, longitude)
            marker.title = title
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            overlays.add(marker)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}