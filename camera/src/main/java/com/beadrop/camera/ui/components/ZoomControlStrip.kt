package com.beadrop.camera.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beadrop.camera.zoom.ZoomController
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassPill
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SpacingTokens
import com.beadrop.design.tokens.TypographyTokens
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Zoom control strip with glass pills for each zoom stop.
 * Active zoom level is highlighted and scaled.
 */
@Composable
fun ZoomControlStrip(
    modifier: Modifier = Modifier,
    currentZoom: Float,
    onZoomSelected: (Float) -> Unit,
) {
    val zoomStops = listOf(0.5f, 1f, 2f, 3f, 10f)

    Row(
        modifier = modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        zoomStops.forEach { stop ->
            val isActive = abs(currentZoom - stop) < ZoomController.SNAP_THRESHOLD
            val isNearby = abs(currentZoom - stop) < 0.5f

            val scale by animateFloatAsState(
                targetValue = when {
                    isActive -> 1.2f
                    isNearby -> 1.05f
                    else -> 1f
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh,
                ),
                label = "zoomScale_$stop",
            )

            val textColor by animateColorAsState(
                targetValue = when {
                    isActive -> ColorTokens.Accent
                    else -> ColorTokens.TextSecondary
                },
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "zoomColor_$stop",
            )

            GlassPill(
                modifier = Modifier
                    .scale(scale)
                    .height(32.dp)
                    .wrapContentWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onZoomSelected(stop)
                    },
                intensity = if (isActive) GlassIntensity.THICK else GlassIntensity.THIN,
                isActive = isActive,
            ) {
                Text(
                    text = if (stop < 1f) ".${(stop * 10).roundToInt()}" else "${stop.roundToInt()}×",
                    style = TypographyTokens.CameraZoom.copy(
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                    ),
                    color = textColor,
                    modifier = Modifier.padding(horizontal = SpacingTokens.M),
                )
            }
        }
    }
}
