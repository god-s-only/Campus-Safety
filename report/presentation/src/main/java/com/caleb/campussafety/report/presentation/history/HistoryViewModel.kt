package com.caleb.campussafety.report.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caleb.campussafety.report.domain.model.IncidentStatus
import com.caleb.campussafety.report.domain.usecase.GetIncidentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getIncidentsUseCase: GetIncidentsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    private val _actions = Channel<HistoryAction>()
    val actions = _actions.receiveAsFlow()

    init {
        loadIncidents()
    }

    private fun loadIncidents() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getIncidentsUseCase()
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
                .collect { incidents ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            incidents = incidents,
                            filteredIncidents = applyFilters(
                                incidents = incidents,
                                filter = it.selectedFilter,
                                query = it.searchQuery
                            )
                        )
                    }
                }
        }
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.OnFilterChange -> {
                val newFilter = if (_state.value.selectedFilter == event.status)
                    null
                else
                    event.status
                _state.update {
                    it.copy(
                        selectedFilter = newFilter,
                        filteredIncidents = applyFilters(
                            incidents = it.incidents,
                            filter = newFilter,
                            query = it.searchQuery
                        )
                    )
                }
            }
            is HistoryEvent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query,
                        filteredIncidents = applyFilters(
                            incidents = it.incidents,
                            filter = it.selectedFilter,
                            query = event.query
                        )
                    )
                }
            }
            is HistoryEvent.OnIncidentClick -> {
                viewModelScope.launch {
                    _actions.send(
                        HistoryAction.NavigateToIncidentDetail(event.incidentId)
                    )
                }
            }
        }
    }

    private fun applyFilters(
        incidents: List<com.caleb.campussafety.report.domain.model.Incident>,
        filter: IncidentStatus?,
        query: String
    ) = incidents
        .filter { incident ->
            filter == null || incident.status == filter
        }
        .filter { incident ->
            query.isBlank() ||
                    incident.description.contains(query, ignoreCase = true) ||
                    incident.category.name.contains(query, ignoreCase = true)
        }
}