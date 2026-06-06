package com.beadrop.benchmark

import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Performance monitoring for Beadrop Camera.
 * 
 * Tracks:
 * - Startup time (target < 300ms)
 * - Camera ready time (target < 500ms)
 * - Frame rate (target 60+ FPS)
 * - Memory usage
 * - Capture latency
 */
object PerformanceMonitor {

    private const val TAG = "BeadropPerf"

    private val _metrics = MutableStateFlow(PerformanceMetrics())
    val metrics: StateFlow<PerformanceMetrics> = _metrics.asStateFlow()

    private val timers = mutableMapOf<String, Long>()

    fun startTimer(label: String) {
        timers[label] = SystemClock.elapsedRealtimeNanos()
    }

    fun endTimer(label: String): Long {
        val start = timers.remove(label) ?: return -1
        val elapsed = SystemClock.elapsedRealtimeNanos() - start
        val ms = elapsed / 1_000_000
        Log.d(TAG, "⏱️ $label: ${ms}ms")
        
        when (label) {
            "startup" -> _metrics.value = _metrics.value.copy(startupTimeMs = ms)
            "camera_ready" -> _metrics.value = _metrics.value.copy(cameraReadyTimeMs = ms)
            "capture" -> _metrics.value = _metrics.value.copy(captureLatencyMs = ms)
            "gallery_load" -> _metrics.value = _metrics.value.copy(galleryLoadTimeMs = ms)
        }
        
        return ms
    }

    fun trackFrameRate(fps: Float) {
        _metrics.value = _metrics.value.copy(currentFps = fps)
    }

    fun trackMemory() {
        val runtime = Runtime.getRuntime()
        val usedMemoryMb = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val maxMemoryMb = runtime.maxMemory() / 1024 / 1024
        _metrics.value = _metrics.value.copy(
            usedMemoryMb = usedMemoryMb,
            maxMemoryMb = maxMemoryMb,
        )
    }

    fun logSummary() {
        val m = _metrics.value
        Log.i(TAG, """
            ╔══════════════════════════════════════╗
            ║     Beadrop Performance Summary      ║
            ╠══════════════════════════════════════╣
            ║ Startup:     ${m.startupTimeMs}ms ${if (m.startupTimeMs < 300) "✅" else "⚠️"}
            ║ Camera Ready: ${m.cameraReadyTimeMs}ms ${if (m.cameraReadyTimeMs < 500) "✅" else "⚠️"}
            ║ FPS:          ${m.currentFps} ${if (m.currentFps >= 60) "✅" else "⚠️"}
            ║ Memory:       ${m.usedMemoryMb}/${m.maxMemoryMb} MB
            ║ Capture:      ${m.captureLatencyMs}ms
            ╚══════════════════════════════════════╝
        """.trimIndent())
    }
}

data class PerformanceMetrics(
    val startupTimeMs: Long = 0,
    val cameraReadyTimeMs: Long = 0,
    val currentFps: Float = 0f,
    val usedMemoryMb: Long = 0,
    val maxMemoryMb: Long = 0,
    val captureLatencyMs: Long = 0,
    val galleryLoadTimeMs: Long = 0,
) {
    val startupMeetsTarget: Boolean get() = startupTimeMs in 1..300
    val cameraReadyMeetsTarget: Boolean get() = cameraReadyTimeMs in 1..500
    val fpsMeetsTarget: Boolean get() = currentFps >= 60f
}
