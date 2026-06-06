package com.beadrop.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OrientationData(
    val azimuth: Float = 0f,
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val accuracy: Int = 0,
    val timestamp: Long = 0L,
) {
    val isLevel: Boolean
        get() = kotlin.math.abs(pitch) < LEVEL_THRESHOLD && kotlin.math.abs(roll) < LEVEL_THRESHOLD

    val pitchDegrees: Float get() = Math.toDegrees(pitch.toDouble()).toFloat()
    val rollDegrees: Float get() = Math.toDegrees(roll.toDouble()).toFloat()
    val azimuthDegrees: Float get() = Math.toDegrees(azimuth.toDouble()).toFloat()

    companion object {
        const val LEVEL_THRESHOLD = 0.03f // ~1.7 degrees
    }
}

@Serializable
data class MotionData(
    val accelerationX: Float = 0f,
    val accelerationY: Float = 0f,
    val accelerationZ: Float = 0f,
    val gyroX: Float = 0f,
    val gyroY: Float = 0f,
    val gyroZ: Float = 0f,
    val isStable: Boolean = true,
    val stabilityScore: Float = 1f,
    val timestamp: Long = 0L,
)

@Serializable
data class LightData(
    val lux: Float = 0f,
    val isLowLight: Boolean = false,
    val lightLevel: LightLevel = LightLevel.NORMAL,
    val timestamp: Long = 0L,
)

@Serializable
enum class LightLevel(val displayName: String) {
    VERY_DARK("Very Dark"),
    DARK("Dark"),
    DIM("Dim"),
    NORMAL("Normal"),
    BRIGHT("Bright"),
    VERY_BRIGHT("Very Bright"),
}
