package com.beadrop.camera.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beadrop.design.tokens.ColorTokens
import kotlin.math.abs

/**
 * Compact zoom pills — quick tap access to zoom stops.
 * Samsung-style floating pills above the mode selector.
 */
@Composable
fun ZoomQuickPills(
    modifier: Modifier = Modifier,
    currentZoom: Float,
    onZoomSelected: (Float) -> Unit,
) {
    val quickStops = listOf(0.5f, 1f, 2f, 5f, 10f)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        quickStops.forEach { stop ->
            val isActive = abs(currentZoom - stop) < stop * 0.1f

            val scale by animateFloatAsState(
                targetValue = if (isActive) 1.15f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh,
                ),
                label = "pillScale_$stop",
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .scale(scale)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onZoomSelected(stop) }
                    .background(
                        color = if (isActive) Color.Black.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.4f),
                        shape = CircleShape,
                    )
                    .then(
                        if (isActive) Modifier.border(
                            1.5.dp,
                            ColorTokens.ZoomDialActive,
                            CircleShape,
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (stop < 1f) ".5" else "${stop.toInt()}×",
                    fontSize = if (isActive) 12.sp else 10.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActive) ColorTokens.ZoomDialActive else ColorTokens.TextSecondary,
                )
            }
        }
    }
}
