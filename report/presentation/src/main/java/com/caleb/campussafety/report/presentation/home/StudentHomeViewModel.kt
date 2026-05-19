package com.caleb.campussafety.report.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caleb.campussafety.auth.domain.usecase.LogoutUseCase
import com.caleb.campussafety.report.domain.usecase.GetMyIncidentsUseCase
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
class StudentHomeViewModel @Inject constructor(
    private val getMyIncidentsUseCase: GetMyIncidentsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(StudentHomeState())
    val state = _state.asStateFlow()

    private val _actions = Channel<StudentHomeAction>()
    val actions = _actions.receiveAsFlow()

    init {
        loadUserName()
        loadRecentIncidents()
    }

    private fun loadUserName() {
        val user = firebaseAuth.currentUser
        _state.update {
            it.copy(userName = user?.displayName ?: user?.email ?: "Student")
        }
    }

    private fun loadRecentIncidents() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getMyIncidentsUseCase(uid)
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
                            recentIncidents = incidents.take(5)
                        )
                    }
                }
        }
    }

    fun onEvent(event: StudentHomeEvent) {
        when (event) {
            is StudentHomeEvent.OnReportIncidentClick -> {
                viewModelScope.launch {
                    _actions.send(StudentHomeAction.NavigateToReport)
                }
            }
            is StudentHomeEvent.OnViewHistoryClick -> {
                viewModelScope.launch {
                    _actions.send(StudentHomeAction.NavigateToHistory)
                }
            }
            is StudentHomeEvent.OnLogoutClick -> {
                viewModelScope.launch {
                    logoutUseCase()
                    _actions.send(StudentHomeAction.NavigateToLogin)
                }
            }
            is StudentHomeEvent.OnIncidentClick -> {
                viewModelScope.launch {
                    _actions.send(
                        StudentHomeAction.NavigateToIncidentDetail(event.incidentId)
                    )
                }
            }
        }
    }
}