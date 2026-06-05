package com.example.myjourney.data.local

import android.content.Context
import com.example.myjourney.model.JournalEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

/**
 * Handles independent local storage for Drafts and Recently Deleted items
 * inside the mobile app using SharedPreferences and Gson.
 */
class LocalLibraryManager(context: Context) {
    private val prefs = context.getSharedPreferences("my_journey_library_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // --- Drafts Management ---
    fun getDrafts(): List<JournalEntry> {
        val json = prefs.getString("local_drafts", null) ?: return emptyList()
        val type = object : TypeToken<List<JournalEntry>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveDraft(title: String, content: String) {
        val drafts = getDrafts().toMutableList()
        val nextId = if (drafts.isEmpty()) -1 else drafts.minOf { it.id ?: 0 } - 1
        
        val newDraft = JournalEntry(
            id = nextId,
            title = title,
            content = content,
            isFavorite = false,
            entryDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            displayDate = "Draft"
        )
        drafts.add(newDraft)
        prefs.edit { putString("local_drafts", gson.toJson(drafts)) }
    }

    fun deleteDraft(id: Int) {
        val drafts = getDrafts().filter { it.id != id }
        prefs.edit { putString("local_drafts", gson.toJson(drafts)) }
    }

    // --- Recently Deleted Management ---
    fun getRecentlyDeleted(): List<JournalEntry> {
        val json = prefs.getString("recently_deleted", null) ?: return emptyList()
        val type = object : TypeToken<List<JournalEntry>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveRecentlyDeleted(entry: JournalEntry) {
        val deletedList = getRecentlyDeleted().toMutableList()
        // Prevent duplicate local entries
        if (deletedList.none { it.id == entry.id }) {
            deletedList.add(entry)
            prefs.edit { putString("recently_deleted", gson.toJson(deletedList)) }
        }
    }

    fun removeRecentlyDeleted(id: Int) {
        val deletedList = getRecentlyDeleted().filter { it.id != id }
        prefs.edit { putString("recently_deleted", gson.toJson(deletedList)) }
    }
}
