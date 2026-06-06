package com.beadrop.camera.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beadrop.core.domain.model.CameraMode
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SpacingTokens
import com.beadrop.design.tokens.TypographyTokens

/**
 * Horizontal mode selector strip — Samsung/Apple style.
 * Active mode is highlighted with scale and color animation.
 */
@Composable
fun CameraModeSelectorStrip(
    modifier: Modifier = Modifier,
    currentMode: CameraMode,
    onModeSelected: (CameraMode) -> Unit,
) {
    val listState = rememberLazyListState()
    val modes = CameraMode.defaultOrder

    // Scroll to selected mode
    LaunchedEffect(currentMode) {
        val index = modes.indexOf(currentMode)
        if (index >= 0) {
            listState.animateScrollToItem(
                index = maxOf(0, index - 1),
            )
        }
    }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
        contentPadding = PaddingValues(horizontal = SpacingTokens.XXL),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(modes) { mode ->
            val isSelected = mode == currentMode

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh,
                ),
                label = "modeScale",
            )

            val textColor by animateColorAsState(
                targetValue = if (isSelected) ColorTokens.Accent else ColorTokens.TextTertiary,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "modeColor",
            )

            Text(
                text = mode.shortName,
                style = TypographyTokens.CameraMode.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                ),
                color = textColor,
                modifier = Modifier
                    .scale(scale)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onModeSelected(mode)
                    }
                    .padding(horizontal = SpacingTokens.S, vertical = SpacingTokens.XS),
            )
        }
    }
}
