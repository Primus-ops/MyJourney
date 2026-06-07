package com.example.myjourney.network

// RetrofitClient is responsible for setting up the Retrofit HTTP client which will be used to make API requests.

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://51.20.108.153/" // hosted link to Laravel backend

    private var tokenManager: TokenManager? = null // TokenManager instance

    fun init(manager: TokenManager) { // Initialize TokenManager
        this.tokenManager = manager
    }

    private val authInterceptor = Interceptor { chain -> // Interceptor to add auth headers
        val originalRequest = chain.request()
        val token = tokenManager?.getToken()

        val newRequest = if (token != null) { // Add token if available
            originalRequest.newBuilder() // Create a new request with headers
                .header("Authorization", "Bearer $token") // Add token to Authorization header
                .header("Accept", "application/json") // Add Accept header
                .build()
        } else { // Otherwise, just add Accept header
            originalRequest.newBuilder()
                .header("Accept", "application/json")
                .build()
        }

        chain.proceed(newRequest)
    }

    private val okHttpClient = OkHttpClient.Builder() // okHttpClient is used to make HTTP requests
        .addInterceptor(authInterceptor) // Add authInterceptor to add headers
        .addInterceptor(HttpLoggingInterceptor().apply { // Add logging interceptor to see requests and responses
            level = HttpLoggingInterceptor.Level.BODY // Log body content to help debug
        })
        .build()

    val apiService: ApiService by lazy { // Lazy-initialized Retrofit ApiService instance
        Retrofit.Builder() // Retrofit instance builder
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Attach OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter
            .build() // Build the Retrofit instance
            .create(ApiService::class.java) // Create the ApiService interface
    }
}
