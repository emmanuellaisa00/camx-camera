package com.beadrop.design.components.buttons

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassSurface
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.RadiusTokens
import com.beadrop.design.tokens.SizeTokens
import com.beadrop.design.tokens.SpacingTokens
import androidx.compose.foundation.shape.CircleShape

/**
 * Premium glass icon button with spring-physics interaction.
 */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    isActive: Boolean = false,
    isEnabled: Boolean = true,
    size: Dp = SizeTokens.MinTouchTarget,
    iconSize: Dp = SizeTokens.IconM,
    iconColor: Color = if (isActive) ColorTokens.Primary else ColorTokens.TextPrimary,
    intensity: GlassIntensity = GlassIntensity.REGULAR,
    hapticEnabled: Boolean = true,
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "iconBtnScale",
    )

    GlassSurface(
        modifier = modifier
            .size(size)
            .scale(scale)
            .semantics { this.contentDescription = contentDescription },
        intensity = intensity,
        shape = CircleShape,
        cornerRadius = size / 2,
        isActive = isActive,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = isEnabled,
                    onClick = {
                        if (hapticEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        onClick()
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = if (isEnabled) iconColor else iconColor.copy(alpha = 0.4f),
            )
        }
    }
}
