package com.caleb.campussafety.report.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caleb.campussafety.report.domain.model.Incident
import com.caleb.campussafety.report.domain.usecase.GetCurrentLocationUseCase
import com.caleb.campussafety.report.domain.usecase.SubmitReportUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val submitReportUseCase: SubmitReportUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state = _state.asStateFlow()

    private val _actions = Channel<ReportAction>()
    val actions = _actions.receiveAsFlow()

    init {
        fetchLocation()
    }

    fun onEvent(event: ReportEvent) {
        when (event) {
            is ReportEvent.OnCategoryChange -> {
                _state.update { it.copy(category = event.category) }
            }
            is ReportEvent.OnDescriptionChange -> {
                _state.update { it.copy(description = event.description) }
            }
            is ReportEvent.OnFetchLocation -> fetchLocation()
            is ReportEvent.OnSubmitReport -> submitReport()
            is ReportEvent.OnLocationPermissionResult -> {
                _state.update {
                    it.copy(isLocationPermissionGranted = event.granted)
                }
                if (event.granted) fetchLocation()
            }
        }
    }

    private fun fetchLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingLocation = true) }
            getCurrentLocationUseCase().fold(
                onSuccess = { location ->
                    _state.update {
                        it.copy(
                            location = location,
                            isLoadingLocation = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoadingLocation = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    private fun submitReport() {
        val current = _state.value
        val location = current.location ?: run {
            _state.update { it.copy(errorMessage = "Location not detected yet") }
            return
        }
        val currentUser = firebaseAuth.currentUser ?: run {
            _state.update { it.copy(errorMessage = "You must be logged in") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null) }
            val incident = Incident(
                reporterId = currentUser.uid,
                reporterName = currentUser.displayName ?: "Unknown",
                category = current.category,
                description = current.description,
                location = location
            )
            submitReportUseCase(incident).fold(
                onSuccess = {
                    _state.update { it.copy(isSubmitting = false) }
                    _actions.send(ReportAction.ReportSubmittedSuccessfully)
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }
}