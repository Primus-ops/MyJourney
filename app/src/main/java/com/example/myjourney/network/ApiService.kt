package com.example.myjourney.network

import com.example.myjourney.model.JournalEntry
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit API Service interface for Laravel backend.
 */
interface ApiService {

    // --- Authentication ---
    
    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>


    // --- Journal Entries (Protected by Sanctum) ---

    @GET("api/journal")
    suspend fun getJournalEntries(): Response<List<JournalEntry>>

    @POST("api/journal")
    suspend fun createJournalEntry(
        @Body entry: JournalEntry
    ): Response<JournalEntry>
}

// Data models for Authentication
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class LoginResponse(
    val token: String,
    val message: String? = null,
    val user: UserData? = null
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String
)