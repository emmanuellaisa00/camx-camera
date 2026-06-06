package com.beadrop.design.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SizeTokens

/**
 * Premium capture button with:
 * - Spring-physics press animation
 * - Long-press for burst/video
 * - Recording pulse animation
 * - Haptic feedback on press
 * - Smooth state transitions
 */
@Composable
fun CaptureButton(
    modifier: Modifier = Modifier,
    isVideoMode: Boolean = false,
    isRecording: Boolean = false,
    isEnabled: Boolean = true,
    onCapture: () -> Unit = {},
    onLongPressStart: () -> Unit = {},
    onLongPressEnd: () -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }

    // Spring animation for press
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.88f
            isRecording -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "captureScale",
    )

    // Inner button size animation
    val innerSize by animateDpAsState(
        targetValue = when {
            isRecording -> 28.dp
            isVideoMode -> SizeTokens.CaptureButtonInner
            else -> SizeTokens.CaptureButtonInner
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "captureInner",
    )

    // Inner corner radius
    val innerCorner by animateDpAsState(
        targetValue = if (isRecording) 8.dp else SizeTokens.CaptureButtonInner / 2,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "captureCorner",
    )

    // Inner color
    val innerColor by animateColorAsState(
        targetValue = when {
            isRecording -> ColorTokens.RecordingRed
            isVideoMode -> ColorTokens.RecordingRed
            else -> ColorTokens.CaptureButton
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "captureColor",
    )

    // Recording pulse
    val infiniteTransition = rememberInfiniteTransition(label = "recordPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isRecording) 0.4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    // Ring color
    val ringColor by animateColorAsState(
        targetValue = when {
            isRecording -> ColorTokens.RecordingRed.copy(alpha = 0.6f)
            isVideoMode -> ColorTokens.RecordingRed.copy(alpha = 0.4f)
            else -> Color.White.copy(alpha = 0.9f)
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "ringColor",
    )

    Box(
        modifier = modifier
            .size(SizeTokens.CaptureButtonOuter)
            .scale(scale)
            .pointerInput(isEnabled, isVideoMode) {
                if (!isEnabled) return@pointerInput
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val released = tryAwaitRelease()
                        isPressed = false
                        if (released) {
                            // Normal tap
                        }
                    },
                    onTap = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCapture()
                    },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPressStart()
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        // Outer pulse ring (recording)
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(SizeTokens.CaptureButtonOuter + 8.dp)
                    .clip(CircleShape)
                    .background(ColorTokens.RecordingPulse.copy(alpha = pulseAlpha))
            )
        }

        // Outer ring
        Box(
            modifier = Modifier
                .size(SizeTokens.CaptureButtonOuter)
                .clip(CircleShape)
                .border(
                    width = SizeTokens.CaptureButtonRing,
                    color = ringColor,
                    shape = CircleShape,
                )
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = if (isRecording) ColorTokens.RecordingRed.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.2f),
                ),
            contentAlignment = Alignment.Center,
        ) {
            // Inner button
            Box(
                modifier = Modifier
                    .size(innerSize)
                    .clip(
                        if (isRecording) {
                            androidx.compose.foundation.shape.RoundedCornerShape(innerCorner)
                        } else {
                            CircleShape
                        }
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                innerColor,
                                innerColor.copy(alpha = 0.85f),
                            )
                        )
                    )
            )
        }
    }
}
