package com.example.myjourney

import android.app.Application
import com.example.myjourney.network.RetrofitClient
import com.example.myjourney.network.TokenManager

class MyJourneyApplication : Application() {

    lateinit var tokenManager: TokenManager

    override fun onCreate() {
        super.onCreate()
        
        // Initialize TokenManager and RetrofitClient
        tokenManager = TokenManager(this)
        RetrofitClient.init(tokenManager)
    }
}
