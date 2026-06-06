package com.beadrop.gallery.ui

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.beadrop.core.domain.model.MediaFilter
import com.beadrop.core.domain.model.MediaItem
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassPill
import com.beadrop.design.components.glass.GlassSurface
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SpacingTokens
import com.beadrop.design.tokens.TypographyTokens
import com.beadrop.gallery.viewmodel.GalleryViewModel

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onOpenMedia: (MediaItem) -> Unit = {},
) {
    val mediaItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val isSelectionMode by viewModel.isSelectionMode.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorTokens.SurfacePure)
            .statusBarsPadding(),
    ) {
        // ═══════════════════════════════════════════
        // TOP BAR
        // ═══════════════════════════════════════════
        GalleryCTopBar(
            isSelectionMode = isSelectionMode,
            selectedCount = selectedItems.size,
            onBack = onNavigateBack,
            onClearSelection = { viewModel.clearSelection() },
            onSelectAll = { viewModel.selectAll() },
            onDeleteSelected = { viewModel.deleteSelected() },
        )

        // ═══════════════════════════════════════════
        // FILTER CHIPS
        // ═══════════════════════════════════════════
        GalleryFilterRow(
            currentFilter = currentFilter,
            onFilterSelected = { viewModel.setFilter(it) },
        )

        // ═══════════════════════════════════════════
        // MEDIA GRID
        // ═══════════════════════════════════════════
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = ColorTokens.Primary)
            }
        } else if (mediaItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.M),
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = null,
                        tint = ColorTokens.TextTertiary,
                        modifier = Modifier.size(64.dp),
                    )
                    Text(
                        text = "No media found",
                        style = TypographyTokens.BodyLarge,
                        color = ColorTokens.TextTertiary,
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(SpacingTokens.XXS),
                horizontalArrangement = Arrangement.spacedBy(SpacingTokens.XXS),
                verticalArrangement = Arrangement.spacedBy(SpacingTokens.XXS),
            ) {
                items(
                    items = mediaItems,
                    key = { it.id },
                ) { item ->
                    MediaThumbnail(
                        item = item,
                        isSelected = item.id in selectedItems,
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                viewModel.toggleSelection(item.id)
                            } else {
                                onOpenMedia(item)
                            }
                        },
                        onLongClick = {
                            viewModel.toggleSelection(item.id)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryCTopBar(
    isSelectionMode: Boolean,
    selectedCount: Int,
    onBack: () -> Unit,
    onClearSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
) {
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
            IconButton(onClick = if (isSelectionMode) onClearSelection else onBack) {
                Icon(
                    imageVector = if (isSelectionMode) Icons.Filled.Close else Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ColorTokens.TextPrimary,
                )
            }

            Text(
                text = if (isSelectionMode) "$selectedCount selected" else "Gallery",
                style = TypographyTokens.TitleLarge,
                color = ColorTokens.TextPrimary,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = SpacingTokens.M),
            )

            if (isSelectionMode) {
                IconButton(onClick = onSelectAll) {
                    Icon(Icons.Filled.SelectAll, "Select All", tint = ColorTokens.TextPrimary)
                }
                IconButton(onClick = onDeleteSelected) {
                    Icon(Icons.Filled.Delete, "Delete", tint = ColorTokens.Error)
                }
            }
        }
    }
}

@Composable
private fun GalleryFilterRow(
    currentFilter: MediaFilter,
    onFilterSelected: (MediaFilter) -> Unit,
) {
    val filters = listOf(
        MediaFilter.ALL to "All",
        MediaFilter.PHOTOS to "Photos",
        MediaFilter.VIDEOS to "Videos",
        MediaFilter.FAVORITES to "Favorites",
        MediaFilter.RAW to "RAW",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.S),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S),
    ) {
        filters.forEach { (filter, label) ->
            val isActive = filter == currentFilter
            GlassPill(
                modifier = Modifier
                    .height(32.dp)
                    .clickable { onFilterSelected(filter) },
                intensity = if (isActive) GlassIntensity.THICK else GlassIntensity.THIN,
                isActive = isActive,
            ) {
                Text(
                    text = label,
                    style = TypographyTokens.LabelMedium,
                    color = if (isActive) ColorTokens.Primary else ColorTokens.TextSecondary,
                    modifier = Modifier.padding(horizontal = SpacingTokens.M),
                )
            }
        }
    }
}

@Composable
private fun MediaThumbnail(
    item: MediaItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .background(ColorTokens.SurfaceElevated),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(Uri.parse(item.uri))
                .crossfade(true)
                .size(300)
                .build(),
            contentDescription = item.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Video duration badge
        if (item.isVideo) {
            GlassPill(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(SpacingTokens.XS),
                intensity = GlassIntensity.THICK,
            ) {
                Text(
                    text = item.formattedDuration,
                    style = TypographyTokens.LabelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = SpacingTokens.XS, vertical = 2.dp),
                )
            }
        }

        // RAW badge
        if (item.isRaw) {
            GlassPill(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(SpacingTokens.XS),
                intensity = GlassIntensity.THICK,
            ) {
                Text(
                    text = "RAW",
                    style = TypographyTokens.LabelSmall,
                    color = ColorTokens.Accent,
                    modifier = Modifier.padding(horizontal = SpacingTokens.XS, vertical = 2.dp),
                )
            }
        }

        // Selection indicator
        AnimatedVisibility(
            visible = isSelectionMode,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SpacingTokens.XS),
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) ColorTokens.Primary else Color.White.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}
