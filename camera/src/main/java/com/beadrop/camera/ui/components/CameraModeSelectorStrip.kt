package com.beadrop.camera.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beadrop.core.domain.model.CameraMode
import com.beadrop.design.tokens.ColorTokens

/**
 * Samsung One UI style mode selector — horizontal text labels.
 * Active mode highlighted in gold.
 */
@Composable
fun CameraModeSelectorStrip(
    modifier: Modifier = Modifier,
    currentMode: CameraMode,
    onModeSelected: (CameraMode) -> Unit,
) {
    val modes = CameraMode.defaultOrder

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        modes.forEachIndexed { index, mode ->
            val isSelected = mode == currentMode

            val textColor by animateColorAsState(
                targetValue = if (isSelected) ColorTokens.ModeActive else ColorTokens.ModeInactive,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "modeColor_$index",
            )

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh,
                ),
                label = "modeScale_$index",
            )

            Text(
                text = mode.shortName,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .scale(scale)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onModeSelected(mode) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}
