package com.example.myunievents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myunievents.data.remote.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: FirebaseRepository): ViewModel() {
    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            try {
                _state.value = AuthUiState.Loading
                repo.signIn(email, password)
                _state.value = AuthUiState.Success
            } catch (e: Exception) {
                _state.value = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        val valid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!valid) {
            _state.value = AuthUiState.Error("Invalid email format")
            return
        }
        viewModelScope.launch {
            try {
                _state.value = AuthUiState.Loading
                repo.signUp(email, password)
                _state.value = AuthUiState.Success
            } catch (e: Exception) {
                _state.value = AuthUiState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun signOut() {
        repo.signOut()
    }
}

sealed class AuthUiState {
    object Idle: AuthUiState()
    object Loading: AuthUiState()
    object Success: AuthUiState()
    data class Error(val msg: String): AuthUiState()
}
