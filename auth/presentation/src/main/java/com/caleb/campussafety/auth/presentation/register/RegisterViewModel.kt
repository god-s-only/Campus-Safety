package com.caleb.campussafety.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caleb.campussafety.auth.domain.model.AuthResult
import com.caleb.campussafety.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _actions = Channel<RegisterAction>()
    val actions = _actions.receiveAsFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnFullNameChange -> {
                _state.update { it.copy(fullName = event.fullName) }
            }
            is RegisterEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email) }
            }
            is RegisterEvent.OnPasswordChange -> {
                _state.update { it.copy(password = event.password) }
            }
            is RegisterEvent.OnConfirmPasswordChange -> {
                _state.update { it.copy(confirmPassword = event.confirmPassword) }
            }
            is RegisterEvent.OnRoleChange -> {
                _state.update { it.copy(role = event.role) }
            }
            is RegisterEvent.OnMatricNumberChange -> {
                _state.update { it.copy(matricNumber = event.matricNumber) }
            }
            is RegisterEvent.OnBadgeNumberChange -> {
                _state.update { it.copy(badgeNumber = event.badgeNumber) }
            }
            is RegisterEvent.OnTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is RegisterEvent.OnToggleConfirmPasswordVisibility -> {
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }
            is RegisterEvent.OnRegisterClick -> register()
        }
    }

    private fun register() {
        val current = _state.value
        if (current.password != current.confirmPassword) {
            _state.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = registerUseCase(
                email = current.email,
                password = current.password,
                fullName = current.fullName,
                role = current.role,
                matricNumber = current.matricNumber.ifBlank { null },
                badgeNumber = current.badgeNumber.ifBlank { null }
            )) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _actions.send(
                        RegisterAction.NavigateToHome(
                            isSecurityOfficer = result.data.role ==
                                    com.caleb.campussafety.auth.domain.model.UserRole.SECURITY
                        )
                    )
                }
                is AuthResult.Error -> {
                    _state.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
                is AuthResult.Loading -> Unit
            }
        }
    }
}