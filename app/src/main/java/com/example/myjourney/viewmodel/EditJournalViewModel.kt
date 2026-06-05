package com.example.myjourney.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjourney.data.JournalRepository
import com.example.myjourney.model.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Edit Journal screen.
 */
class EditJournalViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _editState = MutableStateFlow<EditJournalState>(EditJournalState.Idle)
    val editState: StateFlow<EditJournalState> = _editState

    private val _journalToEdit = MutableStateFlow<JournalEntry?>(null)
    val journalToEdit: StateFlow<JournalEntry?> = _journalToEdit

    fun loadJournal(id: Int) {
        viewModelScope.launch {
            _editState.value = EditJournalState.Loading
            try {
                val response = repository.getJournalById(id)
                if (response.isSuccessful && response.body() != null) {
                    _journalToEdit.value = response.body()!!.data
                    _editState.value = EditJournalState.Idle
                } else {
                    _editState.value = EditJournalState.Error("Failed to load journal")
                }
            } catch (e: Exception) {
                _editState.value = EditJournalState.Error(e.message ?: "Network error")
            }
        }
    }

    fun updateJournal(id: Int, title: String, content: String) {
        val currentJournal = _journalToEdit.value ?: return
        
        viewModelScope.launch {
            _editState.value = EditJournalState.Loading
            try {
                val updatedEntry = currentJournal.copy(
                    title = title,
                    content = content
                )
                val response = repository.updateJournal(id, updatedEntry)
                if (response.isSuccessful) {
                    _editState.value = EditJournalState.Success
                } else {
                    _editState.value = EditJournalState.Error("Failed to update: ${response.message()}")
                }
            } catch (e: Exception) {
                _editState.value = EditJournalState.Error(e.message ?: "Network error")
            }
        }
    }
}

sealed class EditJournalState {
    object Idle : EditJournalState()
    object Loading : EditJournalState()
    object Success : EditJournalState()
    data class Error(val message: String) : EditJournalState()
}
