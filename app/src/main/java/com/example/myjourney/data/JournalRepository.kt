package com.example.myjourney.data

import android.content.Context
import com.example.myjourney.model.JournalEntry
import com.example.myjourney.network.ApiService
import com.example.myjourney.network.PaginatedResponse
import com.example.myjourney.network.SingleResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

/**
 * JournalRepository
 * 
 * Implements the "Repository Pattern" serving as a single, clean interface for the
 * app components to request journal-related resources. Handles dynamic HTTP network
 * calls with automatic offline fallback caching using local JSON serialization.
 */
class JournalRepository(private val apiService: ApiService) {

    suspend fun getJournals(): Response<PaginatedResponse<JournalEntry>> {
        return apiService.getJournalEntries()
    }

    /**
     * Retrieves a paginated collection of journal entries.
     * 
     * [Offline-Ready Cache Feature]:
     * If the API call succeeds, the repository serializes the journals into a local
     * JSON string backup inside the app context. If the server is offline (XAMPP stopped),
     * it intercepts the connection failure and serves the local backup JSON.
     */
    suspend fun getJournalsWithCache(context: Context): Response<PaginatedResponse<JournalEntry>> {
        val prefs = context.getSharedPreferences("my_journey_offline_cache", Context.MODE_PRIVATE)
        val gson = Gson()

        return try {
            val response = apiService.getJournalEntries()
            if (response.isSuccessful && response.body() != null) {
                // Online success: Save a backup JSON copy locally in the phone
                val jsonBackup = gson.toJson(response.body())
                prefs.edit().putString("journals_backup_json", jsonBackup).apply()
            }
            response
        } catch (e: Exception) {
            // Server is Offline: Try loading the local backup JSON file!
            val cachedJson = prefs.getString("journals_backup_json", null)
            if (cachedJson != null) {
                val type = object : TypeToken<PaginatedResponse<JournalEntry>>() {}.type
                val cachedResponse: PaginatedResponse<JournalEntry> = gson.fromJson(cachedJson, type)
                // Return a mock successful response populated with the cached entries!
                Response.success(cachedResponse)
            } else {
                // If there's absolutely no cache and no network, throw original error
                throw e
            }
        }
    }

    /**
     * Retrieves complete details of a single unique journal model entry by its database primary ID.
     */
    suspend fun getJournalById(id: Int): Response<SingleResponse<JournalEntry>> {
        return apiService.getJournalById(id)
    }

    /**
     * Submits a fresh standard journal entry (text fields only) to the database.
     */
    suspend fun createJournal(entry: JournalEntry): Response<SingleResponse<JournalEntry>> {
        return apiService.createJournalEntry(entry)
    }

    /**
     * Submits a fresh journal entry containing an optional binary cover photo attachment.
     */
    suspend fun createJournalMultipart(
        title: RequestBody,
        content: RequestBody,
        entryDate: RequestBody,
        isFavorite: RequestBody,
        photo: MultipartBody.Part?
    ): Response<SingleResponse<JournalEntry>> {
        return apiService.createJournalMultipart(title, content, entryDate, isFavorite, photo)
    }

    /**
     * Contacts the server to toggle the starred/hearted state of an entry.
     */
    suspend fun toggleFavorite(id: Int): Response<Unit> {
        return apiService.toggleFavorite(id)
    }

    /**
     * Contacts the server to permanently delete a journal resource by its ID.
     */
    suspend fun deleteJournal(id: Int): Response<Unit> {
        return apiService.deleteJournal(id)
    }

    /**
     * Updates field details of an existing journal entry model on the server.
     */
    suspend fun updateJournal(id: Int, entry: JournalEntry): Response<SingleResponse<JournalEntry>> {
        return apiService.updateJournalEntry(id, entry)
    }
}
