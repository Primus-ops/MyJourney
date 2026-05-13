package com.example.myjourney.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a Journal Entry.
 * Matches the Laravel JournalEntryResource structure.
 */
data class JournalEntry(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String, // Changed from description
    @SerializedName("entry_date") val entryDate: String, // Changed from year
    @SerializedName("display_date") val displayDate: String? = null,
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
    @SerializedName("photos") val photos: List<JournalPhoto>? = null, // Support for multiple photos
    val imageResId: Int? = null // For local preview
) {
    // Helper to get the first photo URL for the UI
    val coverImageUrl: String?
        get() = photos?.firstOrNull()?.url
}

data class JournalPhoto(
    @SerializedName("id") val id: Int,
    @SerializedName("url") val url: String,
    @SerializedName("caption") val caption: String? = null
)
