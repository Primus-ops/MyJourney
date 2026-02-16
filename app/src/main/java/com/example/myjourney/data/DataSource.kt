package com.example.myjourney.data

import android.annotation.SuppressLint
import com.example.myjourney.R
import com.example.myjourney.R.string
import com.example.myjourney.model.JournalEntry



class DataSource {
    @SuppressLint("ResourceType")
    fun loadJournalEntries(): List<JournalEntry> {
        return listOf<JournalEntry>(
            JournalEntry(R.string.title, R.string.subtitle, R.string.description, R.string.year, R.drawable.image1),
            JournalEntry(R.string.title2, R.string.subtitle2, R.string.description2, R.string.year2, R.drawable.image2),
            JournalEntry(R.string.title3, R.string.subtitle3, R.string.description3, R.string.year3, R.drawable.image3),
            JournalEntry(R.string.title4, R.string.subtitle4, R.string.description4, R.string.year4, R.drawable.image4)
        )
    }
}
