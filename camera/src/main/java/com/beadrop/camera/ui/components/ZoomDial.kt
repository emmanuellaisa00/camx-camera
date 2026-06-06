package com.beadrop.camera.ui.components

import android.view.MotionEvent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beadrop.design.tokens.ColorTokens
import kotlin.math.*

/**
 * iPhone-style circular zoom dial.
 * 
 * A rotatable ring that smoothly controls zoom from 0.5x to 100x.
 * Preset notches at defined zoom stops with haptic feedback.
 * The current zoom level is displayed in the center.
 * 
 * Drag clockwise to zoom in, counterclockwise to zoom out.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ZoomDial(
    modifier: Modifier = Modifier,
    currentZoom: Float,
    minZoom: Float = 0.5f,
    maxZoom: Float = 100f,
    zoomStops: List<Float> = listOf(0.5f, 1f, 2f, 5f, 10f, 30f, 100f),
    size: Dp = 140.dp,
    onZoomChange: (Float) -> Unit,
    onZoomStopHit: (Float) -> Unit = {},
) {
    val density = LocalDensity.current

    // Animate the displayed zoom smoothly
    val animatedZoom by animateFloatAsState(
        targetValue = currentZoom,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "zoomAnim",
    )

    // Convert zoom to angle (logarithmic scale for natural feel)
    val zoomToAngle = remember(minZoom, maxZoom) {
        { zoom: Float ->
            val logMin = ln(minZoom)
            val logMax = ln(maxZoom)
            val logZoom = ln(zoom.coerceIn(minZoom, maxZoom))
            ((logZoom - logMin) / (logMax - logMin) * 300f) - 150f // -150 to +150 degrees
        }
    }

    val angleToZoom = remember(minZoom, maxZoom) {
        { angle: Float ->
            val logMin = ln(minZoom)
            val logMax = ln(maxZoom)
            val normalized = (angle + 150f) / 300f
            exp(logMin + normalized * (logMax - logMin))
        }
    }

    var lastAngle by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var lastHitStop by remember { mutableFloatStateOf(-1f) }

    val currentAngle = zoomToAngle(animatedZoom)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .pointerInteropFilter { event ->
                    val centerX = with(density) { size.toPx() / 2 }
                    val centerY = centerX
                    val dx = event.x - centerX
                    val dy = event.y - centerY

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            val dist = sqrt(dx * dx + dy * dy)
                            val ringRadius = centerX * 0.85f
                            // Only respond to touches near the ring
                            if (dist > ringRadius * 0.55f && dist < ringRadius * 1.3f) {
                                lastAngle = atan2(dy, dx) * 180f / PI.toFloat()
                                isDragging = true
                                true
                            } else false
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (isDragging) {
                                val newAngle = atan2(dy, dx) * 180f / PI.toFloat()
                                var delta = newAngle - lastAngle

                                // Handle wrapping around ±180
                                if (delta > 180f) delta -= 360f
                                if (delta < -180f) delta += 360f

                                // Apply rotation — scale delta for sensitivity
                                val currentZoomAngle = zoomToAngle(currentZoom)
                                val newZoomAngle = (currentZoomAngle + delta * 0.8f).coerceIn(-150f, 150f)
                                val newZoom = angleToZoom(newZoomAngle)

                                onZoomChange(newZoom)

                                // Check if we passed a zoom stop for haptics
                                val nearestStop = zoomStops.minByOrNull { abs(it - newZoom) }
                                if (nearestStop != null && abs(newZoom - nearestStop) < nearestStop * 0.05f) {
                                    if (nearestStop != lastHitStop) {
                                        lastHitStop = nearestStop
                                        onZoomStopHit(nearestStop)
                                    }
                                } else {
                                    lastHitStop = -1f
                                }

                                lastAngle = newAngle
                                true
                            } else false
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            isDragging = false
                            true
                        }
                        else -> false
                    }
                }
        ) {
            val cx = this.size.width / 2
            val cy = this.size.height / 2
            val radius = cx * 0.82f
            val trackWidth = 4.dp.toPx()
            val indicatorWidth = 6.dp.toPx()

            // Background track arc
            drawArc(
                color = ColorTokens.ZoomDialTrack,
                startAngle = -240f,
                sweepAngle = 300f,
                useCenter = false,
                style = Stroke(width = trackWidth, cap = StrokeCap.Round),
                topLeft = Offset(cx - radius, cy - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            )

            // Active arc (from start to current zoom)
            val startDeg = -240f
            val zoomFraction = (ln(animatedZoom) - ln(minZoom)) / (ln(maxZoom) - ln(minZoom))
            val sweepDeg = zoomFraction * 300f

            drawArc(
                color = ColorTokens.ZoomDialActive,
                startAngle = startDeg,
                sweepAngle = sweepDeg.toFloat(),
                useCenter = false,
                style = Stroke(width = indicatorWidth, cap = StrokeCap.Round),
                topLeft = Offset(cx - radius, cy - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            )

            // Zoom stop notches
            for (stop in zoomStops) {
                val stopFraction = (ln(stop) - ln(minZoom)) / (ln(maxZoom) - ln(minZoom))
                val stopAngle = Math.toRadians((startDeg + stopFraction * 300f).toDouble())
                val isActive = abs(animatedZoom - stop) < stop * 0.08f

                val innerR = radius - 12.dp.toPx()
                val outerR = radius + 12.dp.toPx()

                drawLine(
                    color = if (isActive) ColorTokens.ZoomDialActive else ColorTokens.ZoomDialInactive.copy(alpha = 0.5f),
                    start = Offset(
                        cx + innerR * cos(stopAngle).toFloat(),
                        cy + innerR * sin(stopAngle).toFloat(),
                    ),
                    end = Offset(
                        cx + outerR * cos(stopAngle).toFloat(),
                        cy + outerR * sin(stopAngle).toFloat(),
                    ),
                    strokeWidth = if (isActive) 3.dp.toPx() else 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                )

                // Stop label
                val labelR = radius + 22.dp.toPx()
                val labelX = cx + labelR * cos(stopAngle).toFloat()
                val labelY = cy + labelR * sin(stopAngle).toFloat()
                val labelText = if (stop < 1f) ".5" else "${stop.toInt()}"

                drawContext.canvas.nativeCanvas.drawText(
                    labelText,
                    labelX,
                    labelY + 4.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = if (isActive) ColorTokens.ZoomDialActive.hashCode()
                        else ColorTokens.ZoomDialInactive.copy(alpha = 0.6f).hashCode()
                        textSize = if (isActive) 11.sp.toPx() else 9.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            if (isActive) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL
                        )
                    }
                )
            }

            // Current position indicator dot
            val indicatorAngle = Math.toRadians((startDeg + zoomFraction * 300f).toDouble())
            drawCircle(
                color = ColorTokens.ZoomDialActive,
                radius = 6.dp.toPx(),
                center = Offset(
                    cx + radius * cos(indicatorAngle).toFloat(),
                    cy + radius * sin(indicatorAngle).toFloat(),
                ),
            )
        }

        // Center zoom display
        Text(
            text = formatZoom(animatedZoom),
            color = ColorTokens.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

/**

private fun formatZoom(zoom: Float): String {
    return when {
        zoom < 1f -> "%.1f×".format(zoom)
        zoom < 10f -> "%.1f×".format(zoom)
        else -> "${zoom.toInt()}×"
    }
}
