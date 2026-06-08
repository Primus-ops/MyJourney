package com.example.myjourney.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjourney.network.RegisterRequest
import com.example.myjourney.network.RetrofitClient
import com.example.myjourney.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * ViewModel for the Sign Up screen.
 * Handles user registration and token storage.
 */
class SignUpViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(name: String, email: String, password: String, passwordConfirmation: String) {
        // 1. Local Validation
        if (name.isBlank() || email.isBlank() || password.isBlank() || passwordConfirmation.isBlank()) {
            _signUpState.value = SignUpState.Error("Please fill in all fields.")
            return
        }
        if (password != passwordConfirmation) {
            _signUpState.value = SignUpState.Error("Passwords do not match.")
            return
        }
        if (password.length < 6) {
            _signUpState.value = SignUpState.Error("Password must be at least 6 characters.")
            return
        }

        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                val request = RegisterRequest(name, email, password, passwordConfirmation)
                val response = RetrofitClient.apiService.register(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    tokenManager.saveToken(token)
                    _signUpState.value = SignUpState.Success
                } else {
                    // 2. Extract and parse Laravel validation JSON errors
                    val rawError = response.errorBody()?.string()
                    val friendlyMsg = try {
                        rawError?.let {
                            val json = JSONObject(it)
                            // If there is a standard 'message' field, extract it
                            if (json.has("message")) {
                                json.getString("message")
                            } else {
                                "Registration failed."
                            }
                        } ?: "Registration failed."
                    } catch (e: Exception) {
                        response.message() ?: "Registration failed."
                    }
                    _signUpState.value = SignUpState.Error(friendlyMsg)
                }
            } catch (e: java.io.IOException) {
                _signUpState.value = SignUpState.Error("No internet connection. Please check your network.")
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error("The server is currently unreachable. Please try again later.")
            }
        }
    }
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}
