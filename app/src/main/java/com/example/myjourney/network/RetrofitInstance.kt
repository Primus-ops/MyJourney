package com.example.myjourney.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitInstance
 * 
 * A singleton helper object that provides a simple default instance of the Retrofit
 * HTTP client. This defines our baseline base URL targeting the local emulator loopback (10.0.2.2)
 * and configures Gson as the standard JSON deserializer converter factory.
 */
object RetrofitInstance {

    // 10.0.2.2 is the special IP address mapped by the Android emulator to access your laptop's localhost
    private const val BASE_URL = "http://51.20.108.153/api/"

    /**
     * Lazy-initialized Retrofit ApiService instance.
     * Initiated only when accessed for the first time in memory, optimizing performance.
     */
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}