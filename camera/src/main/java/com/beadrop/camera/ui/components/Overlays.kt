package com.beadrop.camera.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.beadrop.core.domain.model.GridType
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassPill
import com.beadrop.design.components.glass.GlassSurface
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SpacingTokens
import com.beadrop.design.tokens.TypographyTokens

/**
 * Grid overlay — Rule of thirds, 4×4, golden ratio, crosshair.
 */
@Composable
fun GridOverlay(
    gridType: GridType,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val gridColor = ColorTokens.GridLine
        val strokeWidth = 0.8f

        when (gridType) {
            GridType.NONE -> {}
            GridType.RULE_OF_THIRDS -> {
                // Vertical lines
                for (i in 1..2) {
                    val x = size.width * i / 3f
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth)
                }
                // Horizontal lines
                for (i in 1..2) {
                    val y = size.height * i / 3f
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth)
                }
            }
            GridType.GRID_4X4 -> {
                for (i in 1..3) {
                    val x = size.width * i / 4f
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth)
                }
                for (i in 1..3) {
                    val y = size.height * i / 4f
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth)
                }
            }
            GridType.GOLDEN_RATIO -> {
                val phi = 1.618f
                val x1 = size.width / (1 + phi)
                val x2 = size.width - x1
                val y1 = size.height / (1 + phi)
                val y2 = size.height - y1
                drawLine(gridColor, Offset(x1, 0f), Offset(x1, size.height), strokeWidth)
                drawLine(gridColor, Offset(x2, 0f), Offset(x2, size.height), strokeWidth)
                drawLine(gridColor, Offset(0f, y1), Offset(size.width, y1), strokeWidth)
                drawLine(gridColor, Offset(0f, y2), Offset(size.width, y2), strokeWidth)
            }
            GridType.CROSSHAIR -> {
                val cx = size.width / 2
                val cy = size.height / 2
                val crossSize = 40f
                drawLine(gridColor, Offset(cx - crossSize, cy), Offset(cx + crossSize, cy), strokeWidth)
                drawLine(gridColor, Offset(cx, cy - crossSize), Offset(cx, cy + crossSize), strokeWidth)
                drawCircle(gridColor, radius = 4f, center = Offset(cx, cy), style = Stroke(strokeWidth))
            }
        }
    }
}

/**
 * Focus ring overlay with animated feedback.
 */
@Composable
fun FocusRingOverlay(
    focusX: Float,
    focusY: Float,
    isFocused: Boolean,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val ringColor by animateColorAsState(
        targetValue = when {
            isLocked -> ColorTokens.FocusLocked
            isFocused -> ColorTokens.FocusRing
            else -> ColorTokens.FocusRing.copy(alpha = 0.6f)
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "focusColor",
    )

    val ringScale by animateFloatAsState(
        targetValue = if (isFocused) 1f else 1.3f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = 500f,
        ),
        label = "focusScale",
    )

    val ringAlpha by animateFloatAsState(
        targetValue = if (isFocused || isLocked) 1f else 0.7f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "focusAlpha",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = focusX * size.width
        val centerY = focusY * size.height
        val radius = 36f * ringScale

        drawCircle(
            color = ringColor.copy(alpha = ringAlpha),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(
                width = if (isLocked) 3f else 2f,
                pathEffect = if (isLocked) null else PathEffect.dashPathEffect(
                    floatArrayOf(8f, 4f), 0f
                ),
            ),
        )

        // Corner brackets for locked focus
        if (isLocked) {
            val bracketSize = 12f
            val offset = radius + 4f
            // Top-left
            drawLine(ringColor, Offset(centerX - offset, centerY - offset), Offset(centerX - offset + bracketSize, centerY - offset), 2f)
            drawLine(ringColor, Offset(centerX - offset, centerY - offset), Offset(centerX - offset, centerY - offset + bracketSize), 2f)
            // Top-right
            drawLine(ringColor, Offset(centerX + offset, centerY - offset), Offset(centerX + offset - bracketSize, centerY - offset), 2f)
            drawLine(ringColor, Offset(centerX + offset, centerY - offset), Offset(centerX + offset, centerY - offset + bracketSize), 2f)
            // Bottom-left
            drawLine(ringColor, Offset(centerX - offset, centerY + offset), Offset(centerX - offset + bracketSize, centerY + offset), 2f)
            drawLine(ringColor, Offset(centerX - offset, centerY + offset), Offset(centerX - offset, centerY + offset - bracketSize), 2f)
            // Bottom-right
            drawLine(ringColor, Offset(centerX + offset, centerY + offset), Offset(centerX + offset - bracketSize, centerY + offset), 2f)
            drawLine(ringColor, Offset(centerX + offset, centerY + offset), Offset(centerX + offset, centerY + offset - bracketSize), 2f)
        }
    }
}

/**
 * Recording duration indicator.
 */
@Composable
fun RecordingIndicator(
    duration: Long,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "recBlink")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "recDotAlpha",
    )

    GlassPill(
        modifier = modifier,
        intensity = GlassIntensity.REGULAR,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SpacingTokens.M, vertical = SpacingTokens.XS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
        ) {
            // Recording dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ColorTokens.RecordingRed.copy(alpha = dotAlpha))
            )

            // Duration
            Text(
                text = formatDuration(duration),
                style = TypographyTokens.LabelMedium,
                color = ColorTokens.TextPrimary,
            )
        }
    }
}

/**
 * Zoom navigator window — shows overview at 10x+ zoom.
 */
@Composable
fun ZoomNavigatorWindow(
    modifier: Modifier = Modifier,
) {
    GlassSurface(
        modifier = modifier.size(100.dp),
        intensity = GlassIntensity.THICK,
        shape = RoundedCornerShape(12.dp),
    ) {
        // Placeholder — in production, this shows a low-res overview
        // with a highlighted rectangle showing the current zoom viewport
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center,
        ) {
            // Viewport rectangle indicator
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.Transparent)
                    .clip(RoundedCornerShape(2.dp))
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    drawRect(
                        color = ColorTokens.FocusRing,
                        style = Stroke(width = 1.5f),
                    )
                }
            }
        }
    }
}

/**
 * Histogram overlay.
 */
@Composable
fun HistogramOverlay(
    modifier: Modifier = Modifier,
) {
    GlassSurface(
        modifier = modifier.size(width = 140.dp, height = 80.dp),
        intensity = GlassIntensity.THICK,
        shape = RoundedCornerShape(8.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(SpacingTokens.XS)) {
            // Simulated histogram — in production, computed from preview frames
            val barCount = 64
            val barWidth = size.width / barCount
            for (i in 0 until barCount) {
                val progress = i.toFloat() / barCount
                // Bell curve simulation
                val height = (kotlin.math.exp(-((progress - 0.5f) * (progress - 0.5f)) / 0.04f) * size.height * 0.8f).toFloat()
                drawRect(
                    color = ColorTokens.HistogramFill,
                    topLeft = Offset(i * barWidth, size.height - height),
                    size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, height),
                )
            }
        }
    }
}

/**
 * Level indicator overlay.
 */
@Composable
fun LevelIndicatorOverlay(
    modifier: Modifier = Modifier,
    pitchDegrees: Float = 0f,
    rollDegrees: Float = 0f,
) {
    val isLevel = kotlin.math.abs(rollDegrees) < 1.5f

    val lineColor by animateColorAsState(
        targetValue = if (isLevel) ColorTokens.LevelAligned else ColorTokens.LevelIndicator,
        label = "levelColor",
    )

    Canvas(modifier = modifier.size(200.dp, 2.dp)) {
        drawLine(
            color = lineColor,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = if (isLevel) 2f else 1.5f,
        )
        // Center tick
        drawLine(
            color = lineColor,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 2f,
        )
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}
