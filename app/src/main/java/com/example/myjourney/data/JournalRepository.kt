package com.example.myjourney.data

import com.example.myjourney.model.JournalEntry
import com.example.myjourney.network.ApiService
import retrofit2.Response

/**
 * Repository class that acts as the "Warehouse Manager" for Journal Entries.
 * It handles fetching data from the API.
 */
class JournalRepository(private val apiService: ApiService) {

    /**
     * Fetches the list of journals from the Laravel backend.
     */
    suspend fun getJournals(): Response<List<JournalEntry>> {
        return apiService.getJournalEntries()
    }

    /**
     * Creates a new journal entry on the Laravel backend.
     */
    suspend fun createJournal(entry: JournalEntry): Response<JournalEntry> {
        return apiService.createJournalEntry(entry)
    }
}
