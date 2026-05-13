package com.example.myjourney.data

import com.example.myjourney.R
import com.example.myjourney.model.JournalEntry

/**
 * Data source for providing dummy journal entries.
 * Updated to use Strings to match the Laravel API format.
 */
class DataSource {
    fun loadJournalEntries(): List<JournalEntry> {
        return listOf(
            JournalEntry(
                id = 1,
                title = "Morning at the Lake",
                subtitle = "Peaceful start",
                content = "Today I went to the lake at 6 AM. The water was like glass and the air was crisp. I feel so refreshed and ready for the day.",
                entryDate = "2023-10-24",
                displayDate = "Oct 24, 2023",
                imageResId = R.drawable.morning_lake, // Assuming you have this drawable
                isFavorite = true
            ),
            JournalEntry(
                id = 2,
                title = "Hike up the Peak",
                subtitle = "Hard but worth it",
                content = "The climb was steeper than I expected, but the view from the top was incredible. I can see the whole valley from here.",
                entryDate = "2023-10-22",
                displayDate = "Oct 22, 2023",
                imageResId = R.drawable.mountain_hike
            ),
            JournalEntry(
                id = 3,
                title = "Rainy Day Reading",
                subtitle = "Cozy vibes",
                content = "It's been raining all day. I spent the afternoon reading my favorite book by the window. Perfect day for some introspection.",
                entryDate = "2023-10-20",
                displayDate = "Oct 20, 2023",
                imageResId = R.drawable.rainy_reading,
                isFavorite = true
            )
        )
    }
}
