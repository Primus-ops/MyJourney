package com.example.myjourney.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjourney.network.RetrofitClient
import com.example.myjourney.network.TokenManager
import com.example.myjourney.network.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: UserDto?) : ProfileState()
    data class Error(val message: String) : ProfileState()
    object LogoutSuccess : ProfileState()
}

class ProfileViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val uiState: StateFlow<ProfileState> = _uiState

    fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            try {
                // Direct access for stability
                val response = RetrofitClient.apiService.getUser()
                val responseBody = response.body()
                
                if (response.isSuccessful && responseBody != null) {
                    _uiState.value = ProfileState.Success(responseBody)
                } else {
                    _uiState.value = ProfileState.Error("Server error code: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error(e.message ?: "Connection error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            try {
                RetrofitClient.apiService.logout()
            } catch (e: Exception) {
                // Ignore failed network logout
            } finally {
                tokenManager.deleteToken()
                _uiState.value = ProfileState.LogoutSuccess
            }
        }
    }
}
