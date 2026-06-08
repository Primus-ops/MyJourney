package com.example.myjourney.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjourney.network.LoginRequest
import com.example.myjourney.network.RetrofitClient
import com.example.myjourney.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Login screen.
 * Handles the authentication request and updates the UI state.
 */
class LoginViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    tokenManager.saveToken(token)
                    _loginState.value = LoginState.Success
                } else {
                    // Extract detailed error message from JSON body (e.g., "The provided credentials do not match our records.")
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = extractMessageFromJson(errorBody) ?: "Login failed: ${response.message()}"
                    _loginState.value = LoginState.Error(errorMessage)
                }
            } catch (e: java.io.IOException) {
                _loginState.value = LoginState.Error("No internet connection. Please verify your Wi-Fi or data.")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Unable to reach the server. Please try again later.")
            }
        }
    }

    /**
     * Simple parser to extract the "message" field from Laravel's error JSON.
     */
    private fun extractMessageFromJson(json: String?): String? {
        if (json == null) return null
        return try {
            val jsonObject = org.json.JSONObject(json)
            jsonObject.optString("message", null)
        } catch (e: Exception) {
            null
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
