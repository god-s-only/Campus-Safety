package com.caleb.campussafety.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caleb.campussafety.auth.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class SplashAction {
    object NavigateToLogin : SplashAction()
    data class NavigateToHome(val isSecurityOfficer: Boolean) : SplashAction()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _actions = Channel<SplashAction>()
    val actions = _actions.receiveAsFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                _actions.send(SplashAction.NavigateToLogin)
                return@launch
            }
            try {
                val doc = firestore
                    .collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()
                val role = doc.getString("role")
                val isSecurityOfficer = role == UserRole.SECURITY.name
                _actions.send(SplashAction.NavigateToHome(isSecurityOfficer))
            } catch (e: Exception) {
                _actions.send(SplashAction.NavigateToLogin)
            }
        }
    }
}