package com.example.myjourney.model

import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes

data class JournalEntry(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    @StringRes val description: Int,
    @IntegerRes val year: Int,
    @DrawableRes val imageResId: Int
)

