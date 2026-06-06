package com.beadrop.camera.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beadrop.core.domain.model.*
import com.beadrop.design.components.buttons.GlassIconButton
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassPill
import com.beadrop.design.components.glass.GlassSurface
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SpacingTokens
import com.beadrop.design.tokens.TypographyTokens

@Composable
fun CameraTopBar(
    modifier: Modifier = Modifier,
    config: CameraConfig,
    cameraMode: CameraMode,
    isRecording: Boolean,
    onFlashClick: () -> Unit,
    onTimerClick: () -> Unit,
    onAspectRatioClick: () -> Unit,
    onHDRClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = !isRecording,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
    ) {
        GlassSurface(
            modifier = modifier.height(48.dp),
            intensity = GlassIntensity.THIN,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingTokens.S),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Flash
                GlassIconButton(
                    icon = when (config.flashMode) {
                        FlashMode.OFF -> Icons.Outlined.FlashOff
                        FlashMode.ON -> Icons.Filled.FlashOn
                        FlashMode.AUTO -> Icons.Outlined.FlashAuto
                        FlashMode.TORCH -> Icons.Filled.Flashlight
                    },
                    onClick = onFlashClick,
                    contentDescription = "Flash: ${config.flashMode.displayName}",
                    isActive = config.flashMode != FlashMode.OFF,
                    size = 40.dp,
                    intensity = GlassIntensity.ULTRA_THIN,
                )

                // Timer
                GlassIconButton(
                    icon = when (config.timer) {
                        TimerDuration.OFF -> Icons.Outlined.Timer
                        TimerDuration.SEC_2 -> Icons.Outlined.Timer
                        TimerDuration.SEC_5 -> Icons.Outlined.Timer
                        TimerDuration.SEC_10 -> Icons.Outlined.Timer10
                    },
                    onClick = onTimerClick,
                    contentDescription = "Timer: ${config.timer.displayName}",
                    isActive = config.timer != TimerDuration.OFF,
                    size = 40.dp,
                    intensity = GlassIntensity.ULTRA_THIN,
                )

                // Aspect Ratio
                GlassPill(
                    intensity = GlassIntensity.ULTRA_THIN,
                    modifier = Modifier
                        .height(32.dp)
                        .wrapContentWidth(),
                ) {
                    Text(
                        text = config.aspectRatio.displayName,
                        style = TypographyTokens.LabelMedium,
                        color = ColorTokens.TextPrimary,
                        modifier = Modifier.padding(horizontal = SpacingTokens.M),
                    )
                }

                // HDR
                GlassIconButton(
                    icon = Icons.Outlined.HdrOn,
                    onClick = onHDRClick,
                    contentDescription = "HDR: ${if (config.hdrEnabled) "On" else "Off"}",
                    isActive = config.hdrEnabled,
                    size = 40.dp,
                    intensity = GlassIntensity.ULTRA_THIN,
                )

                // Settings
                GlassIconButton(
                    icon = Icons.Outlined.Settings,
                    onClick = onSettingsClick,
                    contentDescription = "Settings",
                    size = 40.dp,
                    intensity = GlassIntensity.ULTRA_THIN,
                )
            }
        }
    }
}
