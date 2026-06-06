package com.beadrop.settings.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.beadrop.design.components.glass.GlassCard
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassSurface
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SpacingTokens
import com.beadrop.design.tokens.TypographyTokens

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorTokens.SurfacePure)
            .statusBarsPadding(),
    ) {
        // Top bar
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            intensity = GlassIntensity.THIN,
            shape = RoundedCornerShape(0.dp),
            borderEnabled = false,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = SpacingTokens.L),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "Back",
                        tint = ColorTokens.TextPrimary,
                    )
                }
                Text(
                    text = "Settings",
                    style = TypographyTokens.TitleLarge,
                    color = ColorTokens.TextPrimary,
                    modifier = Modifier.padding(start = SpacingTokens.M),
                )
            }
        }

        // Settings Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L),
        ) {
            // Camera Section
            SettingsSection(title = "Camera") {
                SettingsToggle("Shutter Sound", Icons.Outlined.VolumeUp, true)
                SettingsToggle("Mirror Front Camera", Icons.Outlined.Flip, true)
                SettingsToggle("Geo Tagging", Icons.Outlined.LocationOn, false)
                SettingsToggle("HDR Auto", Icons.Outlined.HdrAuto, true)
            }

            // Video Section
            SettingsSection(title = "Video") {
                SettingsItem("Resolution", "4K", Icons.Outlined.HighQuality)
                SettingsItem("Frame Rate", "30 FPS", Icons.Outlined.Speed)
                SettingsToggle("Stabilization", Icons.Outlined.Straighten, true)
            }

            // Viewfinder Section
            SettingsSection(title = "Viewfinder") {
                SettingsItem("Grid", "Rule of Thirds", Icons.Outlined.Grid3x3)
                SettingsToggle("Level", Icons.Outlined.Straighten, false)
                SettingsToggle("Histogram", Icons.Outlined.BarChart, false)
                SettingsToggle("Focus Peaking", Icons.Outlined.FilterCenterFocus, false)
                SettingsToggle("Zebra Stripes", Icons.Outlined.Warning, false)
            }

            // Storage Section
            SettingsSection(title = "Storage") {
                SettingsItem("Save Location", "DCIM/BeadropCamera", Icons.Outlined.Folder)
                SettingsItem("Image Format", "JPEG", Icons.Outlined.Image)
                SettingsItem("Quality", "High", Icons.Outlined.HighQuality)
            }

            // Accessibility Section
            SettingsSection(title = "Accessibility") {
                SettingsToggle("Haptic Feedback", Icons.Outlined.Vibration, true)
                SettingsToggle("High Contrast", Icons.Outlined.Contrast, false)
                SettingsItem("Text Size", "Default", Icons.Outlined.TextFields)
            }

            // About Section
            SettingsSection(title = "About") {
                SettingsItem("Version", "1.0.0", Icons.Outlined.Info)
                SettingsItem("Build", "Release", Icons.Outlined.Build)
            }

            Spacer(modifier = Modifier.height(SpacingTokens.Massive))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.XS),
    ) {
        Text(
            text = title.uppercase(),
            style = TypographyTokens.LabelMedium,
            color = ColorTokens.TextTertiary,
            modifier = Modifier.padding(start = SpacingTokens.L, bottom = SpacingTokens.XS),
        )

        GlassCard(intensity = GlassIntensity.THIN) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = SpacingTokens.XS),
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ColorTokens.TextSecondary,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = title,
            style = TypographyTokens.BodyLarge,
            color = ColorTokens.TextPrimary,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = SpacingTokens.L),
        )
        Text(
            text = value,
            style = TypographyTokens.BodyMedium,
            color = ColorTokens.TextTertiary,
        )
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    icon: ImageVector,
    defaultValue: Boolean,
) {
    var isChecked by remember { mutableStateOf(defaultValue) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isChecked = !isChecked }
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.S),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ColorTokens.TextSecondary,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = title,
            style = TypographyTokens.BodyLarge,
            color = ColorTokens.TextPrimary,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = SpacingTokens.L),
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = ColorTokens.Primary,
                checkedTrackColor = ColorTokens.Primary.copy(alpha = 0.3f),
                uncheckedThumbColor = ColorTokens.TextTertiary,
                uncheckedTrackColor = ColorTokens.SurfaceElevated,
            ),
        )
    }
}
