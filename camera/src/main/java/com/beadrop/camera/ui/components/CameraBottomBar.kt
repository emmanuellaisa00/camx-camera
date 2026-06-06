package com.beadrop.camera.ui.components

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.beadrop.design.components.buttons.CaptureButton
import com.beadrop.design.components.buttons.GlassIconButton
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassSurface
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SizeTokens
import com.beadrop.design.tokens.SpacingTokens

@Composable
fun CameraBottomBar(
    modifier: Modifier = Modifier,
    isVideoMode: Boolean,
    isRecording: Boolean,
    lastCapturedUri: Uri?,
    onCapture: () -> Unit,
    onSwitchCamera: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Gallery Thumbnail
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                )
                .background(ColorTokens.SurfaceElevated)
                .clickable { onGalleryClick() },
            contentAlignment = Alignment.Center,
        ) {
            if (lastCapturedUri != null) {
                // Would show thumbnail via Coil — placeholder for now
                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = "Gallery",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = "Gallery",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        // Capture Button
        CaptureButton(
            isVideoMode = isVideoMode,
            isRecording = isRecording,
            onCapture = onCapture,
        )

        // Switch Camera
        GlassIconButton(
            icon = Icons.Filled.Cameraswitch,
            onClick = onSwitchCamera,
            contentDescription = "Switch Camera",
            size = 52.dp,
            iconSize = 26.dp,
            intensity = GlassIntensity.REGULAR,
        )
    }
}
