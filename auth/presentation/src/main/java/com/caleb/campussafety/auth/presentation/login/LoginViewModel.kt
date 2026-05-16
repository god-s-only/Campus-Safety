package com.caleb.campussafety.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caleb.campussafety.auth.domain.model.AuthResult
import com.caleb.campussafety.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _actions = Channel<LoginAction>()
    val actions = _actions.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email) }
            }
            is LoginEvent.OnPasswordChange -> {
                _state.update { it.copy(password = event.password) }
            }
            is LoginEvent.OnTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is LoginEvent.OnLoginClick -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = loginUseCase(
                email = _state.value.email,
                password = _state.value.password
            )) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _actions.send(
                        LoginAction.NavigateToHome(
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