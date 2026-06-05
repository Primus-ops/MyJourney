package com.example.myjourney.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjourney.data.JournalRepository
import com.example.myjourney.model.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for the Create Journal screen.
 * Handles sending new entries to the Laravel backend (supporting multipart image upload).
 */
class CreateJournalViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _createState = MutableStateFlow<CreateJournalState>(CreateJournalState.Idle)
    val createState: StateFlow<CreateJournalState> = _createState

    fun createJournal(title: String, content: String, photoFile: File?) {
        if (title.isBlank() || content.isBlank()) {
            _createState.value = CreateJournalState.Error("Title and Content cannot be empty")
            return
        }

        viewModelScope.launch {
            _createState.value = CreateJournalState.Loading
            try {
                // Formatting today's date for Laravel (Y-m-d)
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                
                // Convert parameters to RequestBody
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
                val entryDateBody = currentDate.toRequestBody("text/plain".toMediaTypeOrNull())
                val isFavoriteBody = "0".toRequestBody("text/plain".toMediaTypeOrNull()) // 0/false by default

                // Convert file to MultipartBody.Part
                val photoPart = photoFile?.let { file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                }

                val response = repository.createJournalMultipart(
                    title = titleBody,
                    content = contentBody,
                    entryDate = entryDateBody,
                    isFavorite = isFavoriteBody,
                    photo = photoPart
                )

                if (response.isSuccessful) {
                    _createState.value = CreateJournalState.Success
                } else {
                    _createState.value = CreateJournalState.Error("Failed to save: ${response.message()}")
                }
            } catch (e: Exception) {
                _createState.value = CreateJournalState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
    
    fun resetState() {
        _createState.value = CreateJournalState.Idle
    }
}

sealed class CreateJournalState {
    object Idle : CreateJournalState()
    object Loading : CreateJournalState()
    object Success : CreateJournalState()
    data class Error(val message: String) : CreateJournalState()
}
