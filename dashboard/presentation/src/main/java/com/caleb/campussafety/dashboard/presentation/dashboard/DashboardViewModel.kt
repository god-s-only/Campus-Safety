package com.caleb.campussafety.dashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caleb.campussafety.auth.domain.usecase.LogoutUseCase
import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.model.IncidentStatus
import com.caleb.campussafety.report.domain.repository.ReportRepository
import com.caleb.campussafety.report.domain.usecase.GetIncidentsUseCase
import com.google.firebase.auth.FirebaseAuth
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
class DashboardViewModel @Inject constructor(
    private val getIncidentsUseCase: GetIncidentsUseCase,
    private val reportRepository: ReportRepository,
    private val logoutUseCase: LogoutUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _actions = Channel<DashboardAction>()
    val actions = _actions.receiveAsFlow()

    init {
        loadOfficerName()
        loadIncidents()
    }

    private fun loadOfficerName() {
        val user = firebaseAuth.currentUser
        _state.update {
            it.copy(officerName = user?.displayName ?: user?.email ?: "Officer")
        }
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
                            filteredIncidents = applyFilter(
                                incidents = incidents,
                                filter = it.selectedFilter
                            )
                        )
                    }
                }
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.OnFilterChange -> {
                val newFilter = if (_state.value.selectedFilter == event.status)
                    null
                else
                    event.status
                _state.update {
                    it.copy(
                        selectedFilter = newFilter,
                        filteredIncidents = applyFilter(
                            incidents = it.incidents,
                            filter = newFilter
                        )
                    )
                }
            }
            is DashboardEvent.OnIncidentClick -> {
                viewModelScope.launch {
                    _actions.send(
                        DashboardAction.NavigateToIncidentDetail(event.incidentId)
                    )
                }
            }
            is DashboardEvent.OnLogoutClick -> {
                viewModelScope.launch {
                    logoutUseCase()
                    _actions.send(DashboardAction.NavigateToLogin)
                }
            }
            is DashboardEvent.OnUpdateStatus -> {
                viewModelScope.launch {
                    reportRepository.updateIncidentStatus(
                        incidentId = event.incidentId,
                        status = event.status
                    )
                }
            }
        }
    }

    private fun applyFilter(
        incidents: List<Incident>,
        filter: IncidentStatus?
    ) = if (filter == null) incidents
    else incidents.filter { it.status == filter }
}