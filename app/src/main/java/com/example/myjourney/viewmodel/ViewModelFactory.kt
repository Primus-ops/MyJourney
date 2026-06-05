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
            modelClass.isAssignableFrom(EditJournalViewModel::class.java) -> {
                val repository = JournalRepository(RetrofitClient.apiService)
                EditJournalViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CreateJournalViewModel::class.java) -> {
                val repository = JournalRepository(RetrofitClient.apiService)
                CreateJournalViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(tokenManager) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(tokenManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
