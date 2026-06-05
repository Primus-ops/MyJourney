package com.example.myjourney.network

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

/**
 * TokenManager
 * 
 * Handles secure local storage of the Laravel Sanctum API authentication tokens.
 * By using Android's hardware-backed EncryptedSharedPreferences (part of Android Jetpack Security),
 * the session tokens are encrypted automatically using AES-256 encryption. This protects user sessions
 * from extraction or tampering on rooted or compromised physical devices.
 */
class TokenManager(context: Context) {
    
    // Master Key creation using Keystore keys with AES-256 Galois/Counter Mode (GCM) encryption
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Encrypted preferences instance which handles encryption of preference keys and values automatically
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Saves the authenticated Sanctum bearer token securely into local storage.
     */
    fun saveToken(token: String) {
        prefs.edit { putString("api_token", token) }
    }

    /**
     * Retrieves the stored bearer token to authorize network requests.
     * Returns null if the user is logged out or unauthenticated.
     */
    fun getToken(): String? {
        return prefs.getString("api_token", null)
    }

    /**
     * Removes the stored bearer token, effectively ending the secure mobile session (Logout).
     */
    fun deleteToken() {
        prefs.edit { remove("api_token") }
    }
}
