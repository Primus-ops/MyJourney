package com.example.myjourney.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myjourney.data.JournalRepository
import com.example.myjourney.network.RetrofitClient
import com.example.myjourney.network.TokenManager

/**
 * Factory class to provide ViewModels with their required dependencies.
 */
class ViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(tokenManager) as T
            }
            modelClass.isAssignableFrom(JournalViewModel::class.java) -> {
                val repository = JournalRepository(RetrofitClient.apiService)
                JournalViewModel(repository) as T
            }
            // Add other ViewModels here as we create them
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
