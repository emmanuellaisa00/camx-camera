package com.beadrop.design.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Beadrop Camera Haptic Feedback Engine
 * 
 * Provides premium haptic patterns for camera interactions.
 */
class HapticEngine(private val context: Context) {

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * Light tap — for button presses, toggles
     */
    fun lightTap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }

    /**
     * Medium impact — for mode switches, captures
     */
    fun mediumImpact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }
    }

    /**
     * Heavy impact — for shutter release, recording start
     */
    fun heavyImpact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(40)
        }
    }

    /**
     * Success — for capture complete, save complete
     */
    fun success() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 15, 80, 15),
                    intArrayOf(0, 120, 0, 180),
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 15, 80, 15), -1)
        }
    }

    /**
     * Zoom stop — haptic detent for zoom levels
     */
    fun zoomStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createOneShot(8, 100))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(8)
        }
    }

    /**
     * Focus lock — confirmation of focus lock
     */
    fun focusLock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 12, 40, 12),
                    intArrayOf(0, 150, 0, 150),
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 12, 40, 12), -1)
        }
    }

    /**
     * Error — for failed operations
     */
    fun error() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 30, 60, 30, 60, 30),
                    intArrayOf(0, 200, 0, 200, 0, 200),
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 30, 60, 30, 60, 30), -1)
        }
    }

    /**
     * Slider tick — for continuous slider movement
     */
    fun sliderTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createOneShot(4, 60))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(4)
        }
    }
}

@Composable
fun rememberHapticEngine(): HapticEngine {
    val context = LocalContext.current
    return remember { HapticEngine(context) }
}
