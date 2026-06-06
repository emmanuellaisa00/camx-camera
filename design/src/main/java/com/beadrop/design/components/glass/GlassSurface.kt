package com.beadrop.design.components.glass

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.RadiusTokens

/**
 * Glass intensity levels for the glassmorphism system.
 */
enum class GlassIntensity {
    ULTRA_THIN,
    THIN,
    REGULAR,
    THICK,
    ULTRA_THICK,
}

/**
 * Premium glassmorphic surface component.
 * 
 * Creates a frosted glass effect with:
 * - Translucent background with subtle gradient
 * - Light edge highlights simulating glass refraction
 * - Subtle border for depth separation
 * - Shadow for floating effect
 * - Animated transitions between states
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.REGULAR,
    shape: Shape = RoundedCornerShape(RadiusTokens.L),
    cornerRadius: Dp = RadiusTokens.L,
    isActive: Boolean = false,
    isElevated: Boolean = true,
    borderEnabled: Boolean = true,
    highlightEnabled: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val backgroundColor = when (intensity) {
        GlassIntensity.ULTRA_THIN -> ColorTokens.GlassUltraThin
        GlassIntensity.THIN -> ColorTokens.GlassThin
        GlassIntensity.REGULAR -> ColorTokens.GlassRegular
        GlassIntensity.THICK -> ColorTokens.GlassThick
        GlassIntensity.ULTRA_THICK -> ColorTokens.GlassUltraThick
    }

    val borderColor = if (isActive) {
        ColorTokens.Primary.copy(alpha = 0.5f)
    } else {
        when (intensity) {
            GlassIntensity.ULTRA_THIN -> ColorTokens.GlassBorderLight.copy(alpha = 0.3f)
            GlassIntensity.THIN -> ColorTokens.GlassBorderLight
            GlassIntensity.REGULAR -> ColorTokens.GlassBorderMedium
            GlassIntensity.THICK -> ColorTokens.GlassBorderMedium
            GlassIntensity.ULTRA_THICK -> ColorTokens.GlassBorderStrong
        }
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "glassBorder",
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isElevated) {
            when (intensity) {
                GlassIntensity.ULTRA_THIN -> 2.dp
                GlassIntensity.THIN -> 4.dp
                GlassIntensity.REGULAR -> 8.dp
                GlassIntensity.THICK -> 12.dp
                GlassIntensity.ULTRA_THICK -> 16.dp
            }
        } else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "glassShadow",
    )

    val activeGlow by animateFloatAsState(
        targetValue = if (isActive) 0.15f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "glassGlow",
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = ColorTokens.GlassShadow,
                spotColor = if (isActive) ColorTokens.Primary.copy(alpha = 0.3f) else ColorTokens.GlassShadow,
            )
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (highlightEnabled) {
                    Modifier.drawBehind {
                        // Top edge highlight — simulates light hitting glass
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.08f),
                                    Color.Transparent,
                                ),
                                startY = 0f,
                                endY = size.height * 0.3f,
                            )
                        )
                        // Active glow
                        if (activeGlow > 0f) {
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        ColorTokens.Primary.copy(alpha = activeGlow),
                                        Color.Transparent,
                                    ),
                                    center = Offset(size.width / 2, size.height / 2),
                                    radius = maxOf(size.width, size.height) * 0.8f,
                                )
                            )
                        }
                    }
                } else Modifier
            )
            .then(
                if (borderEnabled) {
                    Modifier.border(
                        width = 0.5.dp,
                        color = animatedBorderColor,
                        shape = shape,
                    )
                } else Modifier
            ),
        contentAlignment = contentAlignment,
        content = content,
    )
}

/**
 * A glass pill — small rounded glass chip.
 * Used for zoom indicators, mode labels, status chips.
 */
@Composable
fun GlassPill(
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.REGULAR,
    isActive: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    GlassSurface(
        modifier = modifier,
        intensity = intensity,
        shape = RoundedCornerShape(RadiusTokens.Pill),
        cornerRadius = RadiusTokens.Pill,
        isActive = isActive,
        content = content,
    )
}

/**
 * A glass card — medium rounded glass container.
 * Used for settings cards, info cards, gallery cards.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.THIN,
    content: @Composable BoxScope.() -> Unit,
) {
    GlassSurface(
        modifier = modifier,
        intensity = intensity,
        shape = RoundedCornerShape(RadiusTokens.XL),
        cornerRadius = RadiusTokens.XL,
        content = content,
    )
}

/**
 * A glass sheet — large glass overlay.
 * Used for bottom sheets, modal panels.
 */
@Composable
fun GlassSheet(
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.THICK,
    content: @Composable BoxScope.() -> Unit,
) {
    GlassSurface(
        modifier = modifier,
        intensity = intensity,
        shape = RoundedCornerShape(topStart = RadiusTokens.XXL, topEnd = RadiusTokens.XXL),
        cornerRadius = RadiusTokens.XXL,
        isElevated = true,
        content = content,
    )
}
