package com.example.myjourney.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

/**
 * PrivacyFlipManager
 * 
 * Uses the Accelerometer to detect when the phone is flipped face-down.
 * This acts as a "Privacy Shield" to quickly hide content.
 */
class PrivacyFlipManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private var onFlipped: (() -> Unit)? = null
    private var isFaceDown = false

    /**
     * Starts listening for the flip gesture.
     */
    fun start(callback: () -> Unit) {
        onFlipped = callback
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    /**
     * Stops the listener.
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val z = event.values[2]

            // When face-up, Z is ~ +9.8
            // When face-down, Z is ~ -9.8
            // We trigger when Z becomes negative enough (phone is notably face-down)
            if (z < -7.0 && !isFaceDown) {
                isFaceDown = true
                onFlipped?.invoke()
            } else if (z > 7.0) {
                isFaceDown = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
