package week11.st6135.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import week11.st6135.finalproject.data.AuthRepository
import week11.st6135.finalproject.util.AuthState

class AuthViewModel(private val repo: AuthRepository = AuthRepository()) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun currentUser() = repo.getCurrentUser()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (!validateEmail(email) || password.isBlank()) {
                _state.value = AuthState.Error("Please enter valid email and password.")
                return@launch
            }

            _state.value = AuthState.Loading
            val result = repo.signIn(email, password)
            _state.value = result.fold(
                onSuccess = { AuthState.Success(it) },
                onFailure = { AuthState.Error(it.message ?: "Login failed") }
            )
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            if (!validateEmail(email) || password.length < 6) {
                _state.value = AuthState.Error("Password must be at least 6 characters.")
                return@launch
            }

            _state.value = AuthState.Loading
            val result = repo.signUp(email, password)
            _state.value = result.fold(
                onSuccess = { AuthState.Success(it) },
                onFailure = { AuthState.Error(it.message ?: "Signup failed") }
            )
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            if (!validateEmail(email)) {
                _state.value = AuthState.Error("Please enter a valid email.")
                return@launch
            }

            _state.value = AuthState.Loading
            val result = repo.sendPasswordReset(email)
            _state.value = result.fold(
                onSuccess = { AuthState.PasswordResetSent(email) },
                onFailure = { AuthState.Error(it.message ?: "Unable to send reset email") }
            )
        }
    }

    fun logout() {
        repo.signOut()
        _state.value = AuthState.SignedOut
    }

    private fun validateEmail(email: String): Boolean =
        email.contains("@") && email.contains(".")
}
