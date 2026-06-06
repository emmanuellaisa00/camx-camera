package com.beadrop.camera.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beadrop.core.domain.model.ProModeState
import com.beadrop.core.domain.model.WhiteBalance
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassSurface
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SpacingTokens
import com.beadrop.design.tokens.TypographyTokens

/**
 * Pro mode manual controls panel.
 * Shows ISO, shutter speed, focus distance, white balance.
 */
@Composable
fun ProModeControlsPanel(
    modifier: Modifier = Modifier,
    proModeState: ProModeState,
    onIsoChanged: (Int) -> Unit,
    onShutterSpeedChanged: (Long) -> Unit,
    onFocusDistanceChanged: (Float) -> Unit,
    onWhiteBalanceChanged: (WhiteBalance) -> Unit,
) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L),
        intensity = GlassIntensity.REGULAR,
        shape = RoundedCornerShape(SpacingTokens.L),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.M),
        ) {
            // ISO Control
            ProModeSlider(
                label = "ISO",
                value = proModeState.manualIso.toFloat(),
                valueRange = 50f..6400f,
                displayValue = "${proModeState.manualIso}",
                onValueChange = { onIsoChanged(it.toInt()) },
            )

            // Shutter Speed Control
            val shutterMs = proModeState.manualShutterSpeed / 1000f
            ProModeSlider(
                label = "SHUTTER",
                value = shutterMs,
                valueRange = 0.1f..30000f,
                displayValue = formatShutterSpeed(proModeState.manualShutterSpeed),
                onValueChange = { onShutterSpeedChanged((it * 1000).toLong()) },
            )

            // Focus Distance
            ProModeSlider(
                label = "FOCUS",
                value = proModeState.manualFocusDistance,
                valueRange = 0f..1f,
                displayValue = if (proModeState.manualFocusDistance < 0.1f) "∞" else "%.1f".format(proModeState.manualFocusDistance),
                onValueChange = { onFocusDistanceChanged(it) },
            )

            // White Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "WB",
                    style = TypographyTokens.ProModeValue,
                    color = ColorTokens.TextTertiary,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS)) {
                    WhiteBalance.entries.forEach { wb ->
                        val isActive = wb == proModeState.whiteBalance
                        GlassSurface(
                            modifier = Modifier
                                .height(28.dp)
                                .wrapContentWidth(),
                            intensity = if (isActive) GlassIntensity.THICK else GlassIntensity.ULTRA_THIN,
                            shape = RoundedCornerShape(8.dp),
                            isActive = isActive,
                        ) {
                            Text(
                                text = wb.displayName.take(3),
                                style = TypographyTokens.LabelSmall,
                                color = if (isActive) ColorTokens.Accent else ColorTokens.TextSecondary,
                                modifier = Modifier.padding(horizontal = SpacingTokens.S, vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProModeSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    displayValue: String,
    onValueChange: (Float) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = TypographyTokens.ProModeValue,
                color = ColorTokens.TextTertiary,
            )
            Text(
                text = displayValue,
                style = TypographyTokens.ProModeValue,
                color = ColorTokens.Accent,
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = ColorTokens.Accent,
                activeTrackColor = ColorTokens.Accent,
                inactiveTrackColor = ColorTokens.GlassThin,
            ),
        )
    }
}

private fun formatShutterSpeed(nanos: Long): String {
    val seconds = nanos / 1_000_000_000.0
    return when {
        seconds >= 1 -> "%.1fs".format(seconds)
        seconds >= 0.1 -> "1/${(1 / seconds).toInt()}"
        seconds >= 0.01 -> "1/${(1 / seconds).toInt()}"
        else -> "1/${(1 / seconds).toInt()}"
    }
}
