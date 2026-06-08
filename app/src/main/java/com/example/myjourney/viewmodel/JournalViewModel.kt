package com.example.myjourney.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjourney.data.JournalRepository
import com.example.myjourney.model.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * JournalViewModel
 * 
 * Part of the MVVM structure. It handles loading and mutation operations for the user's
 * journal memories. It exposes states as reactive StateFlow objects, enabling Jetpack Compose UI
 * elements to seamlessly observe live state changes and redraw without boilerplate.
 */
class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    // Backing field to manage full journal collection updates internally
    private val _journalsState = MutableStateFlow<JournalsState>(JournalsState.Loading)
    // Read-only StateFlow exposed to the UI screens
    val journalsState: StateFlow<JournalsState> = _journalsState

    // Search logic: query and filtered results
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Backing field to manage individual journal updates (e.g. Details View)
    private val _singleJournalState = MutableStateFlow<JournalsState>(JournalsState.Loading)
    // Read-only StateFlow exposed to details viewer
    val singleJournalState: StateFlow<JournalsState> = _singleJournalState

    /**
     * Updates the search filter and triggers a UI refresh
     */
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    init {
        // Automatically request fresh listing data on initialization
        fetchJournals()
    }

    /**
     * Executes an asynchronous network call via Kotlin Coroutines on a background pool
     * to fetch all live journals and update the StateFlow value. Automatically utilizes
     * local JSON caching if an Android context is provided and connection fails.
     */
    fun fetchJournals(context: android.content.Context? = null) {
        viewModelScope.launch {
            _journalsState.value = JournalsState.Loading
            try {
                val response = if (context != null) {
                    repository.getJournalsWithCache(context)
                } else {
                    repository.getJournals()
                }
                if (response.isSuccessful && response.body() != null) {
                    var journals = response.body()!!.data
                    
                    // NEW: Filter out items that are currently in the "Recently Deleted" local trash
                    if (context != null) {
                        val localLibraryManager = com.example.myjourney.data.local.LocalLibraryManager(context)
                        val deletedIds = localLibraryManager.getRecentlyDeleted().map { it.id }
                        journals = journals.filter { it.id !in deletedIds }
                    }
                    
                    _journalsState.value = JournalsState.Success(journals)
                } else {
                    _journalsState.value = JournalsState.Error("Failed to load: ${response.message()}")
                }
            } catch (e: Exception) {
                _journalsState.value = JournalsState.Error(e.message ?: "Network error")
            }
        }
    }

    /**
     * Dispatches a request to star/favorite a memory. Upon API response success,
     * it refreshes the dynamic UI listings to reflect the updated favorite state.
     */
    fun toggleFavorite(journalId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.toggleFavorite(journalId)
                if (response.isSuccessful) {
                    fetchJournals() // Refresh the list
                }
            } catch (e: Exception) {
                // Handle error quietly or display message
            }
        }
    }

    /**
     * Handles memory deletion. Matches the 'Soft Delete' pattern:
     * It saves the entry to local trash and filters it from the UI immediately.
     * The actual server-side deletion only happens when 'Delete Forever' is clicked.
     */
    fun deleteJournal(journalId: Int, context: android.content.Context) {
        viewModelScope.launch {
            try {
                val entryToCache = (journalsState.value as? JournalsState.Success)?.journals?.find { it.id == journalId }
                if (entryToCache != null) {
                    val localLibraryManager = com.example.myjourney.data.local.LocalLibraryManager(context)
                    localLibraryManager.saveRecentlyDeleted(entryToCache)
                    
                    // Refresh the list immediately to "hide" the journal from the Home screen
                    fetchJournals(context)
                }
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }

    /**
     * PERMANENTLY deletes from the backend.
     * Use this only when purging from the "Recently Deleted" trash.
     */
    fun permanentDelete(journalId: Int, context: android.content.Context) {
        viewModelScope.launch {
            try {
                val response = repository.deleteJournal(journalId)
                if (response.isSuccessful) {
                    val localLibraryManager = com.example.myjourney.data.local.LocalLibraryManager(context)
                    localLibraryManager.removeRecentlyDeleted(journalId)
                    fetchJournals(context)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Fetches detailed data for a specific entry ID. Emits SingleSuccess upon a correct response.
     */
    fun fetchJournalById(id: Int) {
        viewModelScope.launch {
            _singleJournalState.value = JournalsState.Loading
            try {
                val response = repository.getJournalById(id)
                if (response.isSuccessful && response.body() != null) {
                    _singleJournalState.value = JournalsState.SingleSuccess(response.body()!!.data)
                } else {
                    _singleJournalState.value = JournalsState.Error("Failed to load journal")
                }
            } catch (e: Exception) {
                _singleJournalState.value = JournalsState.Error("Network error")
            }
        }
    }
}

/**
 * Sealed class representing possible state flows for the UI.
 * This guarantees type-safe states during compile and runtime.
 */
sealed class JournalsState {
    object Loading : JournalsState()
    data class Success(val journals: List<JournalEntry>) : JournalsState()
    data class SingleSuccess(val journal: JournalEntry) : JournalsState()
    data class Error(val message: String) : JournalsState()
}
