package com.example.myjourney.network

import com.example.myjourney.model.JournalEntry
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * ApiService
 * 
 * Defines the HTTP connection contract for the mobile application to communicate
 * with the Laravel REST API backend. It lists the endpoints, HTTP verbs, path variables,
 * and body payloads required to interact with Laravel controllers.
 */
interface ApiService {

    // ==========================================
    // --- 1. Authentication Endpoints ---
    // ==========================================
    
    /**
     * Authenticates an existing user credentials block.
     * Returns a JSON envelope with user details and a secure Sanctum token.
     */
    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    /**
     * Registers a brand new account inside the MySQL database via Laravel.
     * Returns the user details and their first Sanctum token.
     */
    @POST("api/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>

    @POST("api/logout")
    suspend fun logout(): Response<Unit>

    @GET("api/user")
    suspend fun getUser(): Response<UserDto>


    // ==========================================
    // --- 2. Protected Journal CRUD Endpoints ---
    // ==========================================

    /**
     * Fetches all live journal entries created by the currently authenticated user.
     * Returns a paginated list wrapped in a PaginatedResponse envelope.
     */
    @GET("api/journal")
    suspend fun getJournalEntries(): Response<PaginatedResponse<JournalEntry>>

    /**
     * Loads full detailed information for a single specific journal entry by its primary key ID.
     */
    @GET("api/journal/{id}")
    suspend fun getJournalById(
        @Path("id") id: Int
    ): Response<SingleResponse<JournalEntry>>

    /**
     * Saves a brand new journal entry to the Laravel database without attachments.
     */
    @POST("api/journal")
    suspend fun createJournalEntry(
        @Body entry: JournalEntry
    ): Response<SingleResponse<JournalEntry>>

    /**
     * Saves a brand new journal entry to the database using Multipart Form Data.
     * Required for uploading high-res binary media/cover photos from the mobile gallery.
     */
    @retrofit2.http.Multipart
    @POST("api/journal")
    suspend fun createJournalMultipart(
        @retrofit2.http.Part("title") title: RequestBody,
        @retrofit2.http.Part("content") content: RequestBody,
        @retrofit2.http.Part("entry_date") entryDate: RequestBody,
        @retrofit2.http.Part("is_favorite") isFavorite: RequestBody,
        @retrofit2.http.Part photo: MultipartBody.Part?
    ): Response<SingleResponse<JournalEntry>>

    /**
     * Updates the properties of an existing journal entry model.
     */
    @retrofit2.http.PUT("api/journal/{id}")
    suspend fun updateJournalEntry(
        @Path("id") id: Int,
        @Body entry: JournalEntry
    ): Response<SingleResponse<JournalEntry>>

    /**
     * Toggles the favorite (starred/heart) status of a journal entry.
     */
    @POST("api/journal/{id}/toggle-favorite")
    suspend fun toggleFavorite(
        @Path("id") id: Int
    ): Response<Unit>

    /**
     * Deletes a journal entry from the server.
     */
    @retrofit2.http.DELETE("api/journal/{id}")
    suspend fun deleteJournal(
        @Path("id") id: Int
    ): Response<Unit>

    // ==========================================
    // --- 3. Dashboard and Stats Endpoints ---
    // ==========================================

    /**
     * Loads statistical metrics including current journal streak counts and active days
     * for display on the statistics dashboard card.
     */
    @GET("api/dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>

    /**
     * Loads a list of recently created journal entries.
     */
    @GET("api/recents")
    suspend fun getRecents(): Response<List<JournalEntry>>
}

/**
 * Encapsulates response metrics returned by Laravel's stats dashboard controller.
 */
data class DashboardResponse(
    val streak_data: StreakData,
    val recent_entries: List<JournalEntry>,
    val favorite_entries: List<JournalEntry>
)

/**
 * Represents tracking statistics parsed from the user's login and writing habits.
 */
data class StreakData(
    val entries_this_week: Int,
    val active_days: Int,
    val current_streak: Int
)

/**
 * Data payload format for logging in.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Data payload format for registering a new user.
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

/**
 * Wrapper returned upon successful registration or login containing user info and token.
 */
data class LoginResponse(
    val user: UserDto,
    val token: String
)

/**
 * User data transfer model returned in API auth sequences.
 */

data class UserDto(
    @SerializedName("id", alternate = ["user_id"])
    val id: Int?,
    @SerializedName("name", alternate = ["username", "full_name"])
    val name: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("profile_photo_url", alternate = ["avatar_url"])
    val profilePhotoUrl: String?,
    @SerializedName("profile_photo_path")
    val profilePhotoPath: String?,
    
    // Skeleton Key: Look inside potential wrappers automatically
    @SerializedName("user")
    val userWrapper: UserDto? = null,
    @SerializedName("data")
    val dataWrapper: UserDto? = null
)

/**
 * Generic envelop wrapper class that safely extracts the standard "data" JSON key
 * from Laravel single-resource resource calls.
 */
data class SingleResponse<T>(
    val data: T
)

/**
 * Generic envelope wrapper that dynamically parses paginated records returned from the backend.
 */
data class PaginatedResponse<T>(
    val current_page: Int,
    val data: List<T>,
    val first_page_url: String,
    val from: Int?,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String?,
    val path: String,
    val per_page: Int,
    val prev_page_url: String?,
    val to: Int?,
    val total: Int
)