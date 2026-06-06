package com.beadrop.player.ui

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassSurface
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SpacingTokens
import com.beadrop.design.tokens.TypographyTokens

/**
 * Hardware-accelerated video player with premium controls.
 */
@Composable
fun VideoPlayerScreen(
    videoUri: String,
    onNavigateBack: () -> Unit = {},
) {
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var playbackSpeed by remember { mutableFloatStateOf(1f) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(videoUri)))
                prepare()
                playWhenReady = true
            }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    duration = exoPlayer.duration
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Update position
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition
            kotlinx.coroutines.delay(100)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                showControls = !showControls
            },
    ) {
        // Video Surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
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
                                tint = Color.White,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        // Speed selector
                        GlassSurface(
                            modifier = Modifier
                                .height(32.dp)
                                .wrapContentWidth()
                                .clickable {
                                    playbackSpeed = when (playbackSpeed) {
                                        0.5f -> 1f
                                        1f -> 1.5f
                                        1.5f -> 2f
                                        2f -> 0.5f
                                        else -> 1f
                                    }
                                    exoPlayer.setPlaybackSpeed(playbackSpeed)
                                },
                            intensity = GlassIntensity.REGULAR,
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                style = TypographyTokens.LabelMedium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = SpacingTokens.M),
                            )
                        }
                    }
                }

                // Center Play/Pause
                IconButton(
                    onClick = {
                        if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f)),
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp),
                    )
                }

                // Bottom Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(SpacingTokens.L),
                ) {
                    // Timeline
                    Slider(
                        value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                        onValueChange = { fraction ->
                            exoPlayer.seekTo((fraction * duration).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = ColorTokens.Primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                        ),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = formatDuration(currentPosition),
                            style = TypographyTokens.LabelSmall,
                            color = Color.White,
                        )
                        Text(
                            text = formatDuration(duration),
                            style = TypographyTokens.LabelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                    }

                    // Bottom action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = SpacingTokens.M),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        // Frame back
                        IconButton(onClick = {
                            exoPlayer.seekTo(maxOf(0, exoPlayer.currentPosition - 33))
                        }) {
                            Icon(Icons.Filled.SkipPrevious, "Frame Back", tint = Color.White)
                        }
                        // Rewind 10s
                        IconButton(onClick = {
                            exoPlayer.seekTo(maxOf(0, exoPlayer.currentPosition - 10000))
                        }) {
                            Icon(Icons.Filled.Replay10, "Rewind", tint = Color.White)
                        }
                        // Forward 10s
                        IconButton(onClick = {
                            exoPlayer.seekTo(minOf(duration, exoPlayer.currentPosition + 10000))
                        }) {
                            Icon(Icons.Filled.Forward10, "Forward", tint = Color.White)
                        }
                        // Frame forward
                        IconButton(onClick = {
                            exoPlayer.seekTo(minOf(duration, exoPlayer.currentPosition + 33))
                        }) {
                            Icon(Icons.Filled.SkipNext, "Frame Forward", tint = Color.White)
                        }
                        // Loop
                        IconButton(onClick = {
                            exoPlayer.repeatMode = if (exoPlayer.repeatMode == Player.REPEAT_MODE_ONE) {
                                Player.REPEAT_MODE_OFF
                            } else {
                                Player.REPEAT_MODE_ONE
                            }
                        }) {
                            Icon(
                                Icons.Filled.Repeat,
                                "Loop",
                                tint = if (exoPlayer.repeatMode == Player.REPEAT_MODE_ONE)
                                    ColorTokens.Primary else Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds)
    else "%d:%02d".format(minutes, seconds)
}
