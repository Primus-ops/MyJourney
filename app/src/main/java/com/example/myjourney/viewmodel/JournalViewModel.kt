package com.example.myjourney.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjourney.data.JournalRepository
import com.example.myjourney.model.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home and Favorites screens.
 * Manages the state of journal entries fetched from the backend.
 */
class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _journalsState = MutableStateFlow<JournalsState>(JournalsState.Loading)
    val journalsState: StateFlow<JournalsState> = _journalsState

    init {
        fetchJournals()
    }

    fun fetchJournals() {
        viewModelScope.launch {
            _journalsState.value = JournalsState.Loading
            try {
                val response = repository.getJournals()
                if (response.isSuccessful && response.body() != null) {
                    _journalsState.value = JournalsState.Success(response.body()!!)
                } else {
                    _journalsState.value = JournalsState.Error("Failed to load journals: ${response.message()}")
                }
            } catch (e: Exception) {
                _journalsState.value = JournalsState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

sealed class JournalsState {
    object Loading : JournalsState()
    data class Success(val journals: List<JournalEntry>) : JournalsState()
    data class Error(val message: String) : JournalsState()
}
