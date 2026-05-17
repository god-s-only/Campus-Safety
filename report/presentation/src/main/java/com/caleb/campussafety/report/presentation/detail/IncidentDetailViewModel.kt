package com.caleb.campussafety.report.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caleb.campussafety.auth.domain.model.UserRole
import com.caleb.campussafety.auth.domain.usecase.GetUserRoleUseCase
import com.caleb.campussafety.report.domain.model.IncidentStatus
import com.caleb.campussafety.report.domain.repository.ReportRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class IncidentDetailViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val getUserRoleUseCase: GetUserRoleUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val incidentId: String = checkNotNull(
        savedStateHandle["incidentId"]
    )

    private val _state = MutableStateFlow(IncidentDetailState())
    val state = _state.asStateFlow()

    private val _actions = Channel<IncidentDetailAction>()
    val actions = _actions.receiveAsFlow()

    init {
        loadIncident()
        checkUserRole()
    }

    private fun loadIncident() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            reportRepository.getIncidentById(incidentId)
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
                .collect { incident ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            incident = incident
                        )
                    }
                }
        }
    }

    private fun checkUserRole() {
        viewModelScope.launch {
            val role = getUserRoleUseCase()
            _state.update {
                it.copy(isSecurityOfficer = role == UserRole.SECURITY)
            }
        }
    }

    fun onEvent(event: IncidentDetailEvent) {
        when (event) {
            is IncidentDetailEvent.OnUpdateStatus -> {
                viewModelScope.launch {
                    reportRepository.updateIncidentStatus(
                        incidentId = incidentId,
                        status = event.status
                    )
                    if (event.status == IncidentStatus.RESOLVED) {
                        _actions.send(IncidentDetailAction.NavigateBack)
                    }
                }
            }
        }
    }
}