package com.example.myjourney.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * AmbientMoodManager
 * 
 * Uses the phone's Light Sensor (Photometer) to detect the surrounding 
 * environment brightness and translate it into a "Mood" and a Theme suggestion.
 */
class AmbientMoodManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    
    private var onMoodChangedWithLux: ((String, Float) -> Unit)? = null

    /**
     * Starts listening to light changes with the fastest response rate.
     */
    fun start(callback: (String, Float) -> Unit) {
        onMoodChangedWithLux = callback
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        } ?: callback("Mood sensor unavailable", 0f)
    }

    /**
     * Stops the sensor.
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lux = event.values[0]
            val mood = when {
                lux < 15 -> "Moonlit Mood 🌙"       // Deep/Dark room
                lux < 100 -> "Cozy Indoors 🛋️"      // Normal indoor light
                lux < 1000 -> "Bright Studio 🎨"      // Bright office/daylight
                else -> "Brilliant Outdoors ☀️"       // Direct sunlight
            }
            onMoodChangedWithLux?.invoke(mood, lux)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
