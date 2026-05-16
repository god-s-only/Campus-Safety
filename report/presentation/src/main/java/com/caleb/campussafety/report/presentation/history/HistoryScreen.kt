package com.caleb.campussafety.report.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caleb.campussafety.report.domain.model.IncidentStatus
import com.caleb.campussafety.report.presentation.home.IncidentCard
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    state: HistoryState,
    actions: Flow<HistoryAction>,
    onEvent: (HistoryEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToIncidentDetail: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        actions.collect { action ->
            when (action) {
                is HistoryAction.NavigateToIncidentDetail ->
                    onNavigateToIncidentDetail(action.incidentId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Incident History",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search bar
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = {
                        onEvent(HistoryEvent.OnSearchQueryChange(it))
                    },
                    placeholder = {
                        Text(
                            "Search incidents...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    onEvent(HistoryEvent.OnSearchQueryChange(""))
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }

            // Filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedFilter == null,
                            onClick = {
                                onEvent(HistoryEvent.OnFilterChange(null))
                            },
                            label = { Text("All", fontSize = 12.sp) }
                        )
                    }
                    items(IncidentStatus.entries) { status ->
                        FilterChip(
                            selected = state.selectedFilter == status,
                            onClick = {
                                onEvent(HistoryEvent.OnFilterChange(status))
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
                    text = "${state.filteredIncidents.size} incident(s) found",
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
                            Icons.Default.History,
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
                        if (state.searchQuery.isNotBlank() ||
                            state.selectedFilter != null
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try adjusting your filters",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Incident list
            items(state.filteredIncidents) { incident ->
                IncidentCard(
                    incident = incident,
                    onClick = {
                        onEvent(HistoryEvent.OnIncidentClick(incident.id))
                    }
                )
            }
        }
    }
}