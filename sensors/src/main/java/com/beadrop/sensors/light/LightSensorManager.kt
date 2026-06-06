package com.beadrop.sensors.light

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.beadrop.core.domain.model.LightData
import com.beadrop.core.domain.model.LightLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ambient light sensor for scene detection and auto-mode switching.
 */
@Singleton
class LightSensorManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    fun observeLight(): Flow<LightData> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val lux = event.values[0]
                val level = classifyLightLevel(lux)
                trySend(
                    LightData(
                        lux = lux,
                        isLowLight = lux < 30f,
                        lightLevel = level,
                        timestamp = event.timestamp,
                    )
                )
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        lightSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    private fun classifyLightLevel(lux: Float): LightLevel = when {
        lux < 1f -> LightLevel.VERY_DARK
        lux < 10f -> LightLevel.DARK
        lux < 50f -> LightLevel.DIM
        lux < 1000f -> LightLevel.NORMAL
        lux < 10000f -> LightLevel.BRIGHT
        else -> LightLevel.VERY_BRIGHT
    }

    fun hasLightSensor(): Boolean = lightSensor != null
}
