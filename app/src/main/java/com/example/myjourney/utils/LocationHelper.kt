package com.example.myjourney.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

/**
 * LocationHelper
 * 
 * A utility class to fetch the device's current GPS location.
 * It uses Google Play Services' FusedLocationProviderClient for high accuracy.
 */
class LocationHelper(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Fetches the current location coordinates and attempts to convert them 
     * into a human-readable address (City, Country).
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationResult: (String) -> Unit) {
        try {
            // First try to get the fresh current location (High Accuracy)
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                if (location != null) {
                    processLocation(location, onLocationResult)
                } else {
                    // Fallback: Try "Last Known Location" if fresh one is slow
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            processLocation(lastLoc, onLocationResult)
                        } else {
                            onLocationResult("Location unavailable")
                        }
                    }
                }
            }.addOnFailureListener {
                onLocationResult("Failed to get location")
            }
        } catch (e: Exception) {
            onLocationResult("Location error")
        }
    }

    private fun processLocation(location: android.location.Location, onLocationResult: (String) -> Unit) {
        val lat = location.latitude
        val lon = location.longitude
        
        // Convert coordinates to a City name
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val city = addresses[0].locality ?: addresses[0].subAdminArea ?: "Unknown City"
                val country = addresses[0].countryName ?: ""
                onLocationResult("$city, $country")
            } else {
                onLocationResult("Location: $lat, $lon")
            }
        } catch (e: Exception) {
            onLocationResult("Location: $lat, $lon")
        }
    }
}
