package com.beadrop.camera.zoom

import com.beadrop.core.domain.model.ZoomState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Flagship zoom controller with haptic detent stops.
 * 
 * Features:
 * - Smooth pinch-to-zoom
 * - Preset zoom stops (0.5x, 1x, 2x, 3x, 10x, 30x, 100x)
 * - Zoom navigator at 10x+
 * - Stabilization assistance at 100x
 * - Lens transition animations
 */
@Singleton
class ZoomController @Inject constructor() {

    companion object {
        val ZOOM_STOPS = listOf(0.5f, 1f, 2f, 3f, 5f, 10f, 30f, 100f)
        const val SNAP_THRESHOLD = 0.08f // Snap to stop within this range
        const val NAVIGATOR_THRESHOLD = 10f
        const val TARGETING_THRESHOLD = 30f
        const val STABILIZATION_THRESHOLD = 100f
    }

    private val _currentZoom = MutableStateFlow(1f)
    val currentZoom: StateFlow<Float> = _currentZoom.asStateFlow()

    private val _showNavigator = MutableStateFlow(false)
    val showNavigator: StateFlow<Boolean> = _showNavigator.asStateFlow()

    private val _showTargeting = MutableStateFlow(false)
    val showTargeting: StateFlow<Boolean> = _showTargeting.asStateFlow()

    private val _showStabilization = MutableStateFlow(false)
    val showStabilization: StateFlow<Boolean> = _showStabilization.asStateFlow()

    private val _nearestStop = MutableStateFlow(1f)
    val nearestStop: StateFlow<Float> = _nearestStop.asStateFlow()

    /**
     * Handle pinch zoom gesture.
     */
    fun onPinchZoom(scaleFactor: Float, currentRatio: Float): Float {
        val newZoom = (currentRatio * scaleFactor).coerceIn(0.5f, 100f)
        updateZoom(newZoom)
        return newZoom
    }

    /**
     * Set zoom to a specific ratio.
     */
    fun setZoom(ratio: Float) {
        val clamped = ratio.coerceIn(0.5f, 100f)
        updateZoom(clamped)
    }

    /**
     * Snap to nearest zoom stop.
     */
    fun snapToNearestStop(): Float {
        val nearest = findNearestStop(_currentZoom.value)
        updateZoom(nearest)
        return nearest
    }

    /**
     * Cycle to next zoom stop.
     */
    fun nextZoomStop(): Float {
        val current = _currentZoom.value
        val nextStop = ZOOM_STOPS.firstOrNull { it > current + SNAP_THRESHOLD } ?: ZOOM_STOPS.first()
        updateZoom(nextStop)
        return nextStop
    }

    /**
     * Cycle to previous zoom stop.
     */
    fun previousZoomStop(): Float {
        val current = _currentZoom.value
        val prevStop = ZOOM_STOPS.lastOrNull { it < current - SNAP_THRESHOLD } ?: ZOOM_STOPS.last()
        updateZoom(prevStop)
        return prevStop
    }

    /**
     * Check if current zoom is near a stop (for haptic feedback).
     */
    fun isNearStop(ratio: Float): Boolean {
        return ZOOM_STOPS.any { kotlin.math.abs(ratio - it) < SNAP_THRESHOLD }
    }

    /**
     * Get the nearest zoom stop for haptic detent.
     */
    fun findNearestStop(ratio: Float): Float {
        return ZOOM_STOPS.minByOrNull { kotlin.math.abs(it - ratio) } ?: 1f
    }

    /**
     * Get zoom display text.
     */
    fun getZoomDisplayText(ratio: Float): String {
        return when {
            ratio < 1f -> "%.1f×".format(ratio)
            ratio < 10f -> "%.1f×".format(ratio)
            else -> "${ratio.roundToInt()}×"
        }
    }

    /**
     * Get zoom level category for UI adaptations.
     */
    fun getZoomLevel(ratio: Float): ZoomLevel {
        return when {
            ratio < 1f -> ZoomLevel.ULTRA_WIDE
            ratio < 2f -> ZoomLevel.WIDE
            ratio < 5f -> ZoomLevel.TELEPHOTO
            ratio < 10f -> ZoomLevel.SUPER_TELEPHOTO
            ratio < 30f -> ZoomLevel.DIGITAL_ZOOM
            ratio < 100f -> ZoomLevel.HYPER_ZOOM
            else -> ZoomLevel.MAX_ZOOM
        }
    }

    private fun updateZoom(ratio: Float) {
        _currentZoom.value = ratio
        _nearestStop.value = findNearestStop(ratio)
        _showNavigator.value = ratio >= NAVIGATOR_THRESHOLD
        _showTargeting.value = ratio >= TARGETING_THRESHOLD
        _showStabilization.value = ratio >= STABILIZATION_THRESHOLD
    }
}

enum class ZoomLevel {
    ULTRA_WIDE,
    WIDE,
    TELEPHOTO,
    SUPER_TELEPHOTO,
    DIGITAL_ZOOM,
    HYPER_ZOOM,
    MAX_ZOOM,
}
