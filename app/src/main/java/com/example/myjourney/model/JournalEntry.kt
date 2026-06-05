package com.example.myjourney.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a Journal Entry.
 * Matches the Laravel JournalEntryResource structure.
 */
data class JournalEntry(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("content") val content: String,
    @SerializedName("entry_date") val entryDate: String,
    @SerializedName("display_date") val displayDate: String? = null,
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
    @SerializedName("photos") val photos: List<JournalPhoto>? = null,
    val imageResId: Int? = null
) {
    val coverImageUrl: String? // Automatically gets the first photo's URL
        get() = photos?.firstOrNull()?.absoluteUrl //
}

data class JournalPhoto(
    @SerializedName("id") val id: Int, // Laravel's primary key
    @SerializedName("url") val url: String, // Laravel's URL field
    @SerializedName("caption") val caption: String? = null // Optional field
) {
    // Automatically prepends the base URL if the path is relative (starts with /storage)
    val absoluteUrl: String // Automatically gets the full URL
        get() = if (url.startsWith("/")) "http://10.0.2.2:8000$url" else url // Laravel's base URL
}
