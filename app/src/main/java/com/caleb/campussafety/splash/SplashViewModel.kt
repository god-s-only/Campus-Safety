package com.caleb.campussafety.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caleb.campussafety.auth.domain.model.UserRole
import com.caleb.campussafety.auth.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashAction {
    object NavigateToLogin : SplashAction()
    data class NavigateToHome(val isSecurityOfficer: Boolean) : SplashAction()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _actions = Channel<SplashAction>()
    val actions = _actions.receiveAsFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            if (user == null) {
                _actions.send(SplashAction.NavigateToLogin)
            } else {
                _actions.send(
                    SplashAction.NavigateToHome(
                        isSecurityOfficer = user.role == UserRole.SECURITY
                    )
                )
            }
        }
    }
}