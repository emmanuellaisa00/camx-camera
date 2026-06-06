package com.beadrop.sensors.orientation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.beadrop.core.domain.model.OrientationData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orientation sensor manager using gyroscope, accelerometer, and rotation vector.
 * 
 * Provides:
 * - Real-time pitch/roll/azimuth
 * - Level detection
 * - Horizon lock data
 * - Tilt indicators
 */
@Singleton
class OrientationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val rotationMatrix = FloatArray(9)
    private val orientationValues = FloatArray(3)
    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    /**
     * Observe orientation data as a Flow.
     * Uses rotation vector sensor (best accuracy) with accelerometer/magnetometer fallback.
     */
    fun observeOrientation(): Flow<OrientationData> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        SensorManager.getOrientation(rotationMatrix, orientationValues)

                        val data = OrientationData(
                            azimuth = orientationValues[0],
                            pitch = orientationValues[1],
                            roll = orientationValues[2],
                            accuracy = event.accuracy,
                            timestamp = event.timestamp,
                        )
                        trySend(data)
                    }
                    Sensor.TYPE_ACCELEROMETER -> {
                        gravity = event.values.clone()
                        computeFallbackOrientation()?.let { trySend(it) }
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        geomagnetic = event.values.clone()
                        computeFallbackOrientation()?.let { trySend(it) }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Prefer rotation vector; fall back to accel + mag
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(
                listener,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI,
            )
        } else {
            accelerometer?.let {
                sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
            }
            magnetometer?.let {
                sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
            }
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    /**
     * Observe gyroscope data for horizon lock.
     */
    fun observeGyroscope(): Flow<FloatArray> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(event.values.clone())
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        gyroscope?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    private fun computeFallbackOrientation(): OrientationData? {
        val g = gravity ?: return null
        val m = geomagnetic ?: return null
        val r = FloatArray(9)
        val i = FloatArray(9)

        if (SensorManager.getRotationMatrix(r, i, g, m)) {
            SensorManager.getOrientation(r, orientationValues)
            return OrientationData(
                azimuth = orientationValues[0],
                pitch = orientationValues[1],
                roll = orientationValues[2],
                timestamp = System.nanoTime(),
            )
        }
        return null
    }

    /**
     * Check if required sensors are available.
     */
    fun hasOrientationSensors(): Boolean =
        rotationVectorSensor != null || (accelerometer != null && magnetometer != null)

    fun hasGyroscope(): Boolean = gyroscope != null
}
