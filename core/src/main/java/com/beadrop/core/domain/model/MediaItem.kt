package com.beadrop.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaItem(
    val id: Long,
    val uri: String,
    val displayName: String,
    val mimeType: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val dateAdded: Long,
    val dateModified: Long,
    val dateTaken: Long,
    val duration: Long = 0L,
    val bucketId: String = "",
    val bucketDisplayName: String = "",
    val relativePath: String = "",
    val orientation: Int = 0,
    val isFavorite: Boolean = false,
    val isTrashed: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    val isVideo: Boolean
        get() = mimeType.startsWith("video/")

    val isImage: Boolean
        get() = mimeType.startsWith("image/")

    val isRaw: Boolean
        get() = mimeType == "image/x-adobe-dng" || mimeType == "image/dng"

    val isHeic: Boolean
        get() = mimeType == "image/heic" || mimeType == "image/heif"

    val aspectRatio: Float
        get() = if (height > 0) width.toFloat() / height.toFloat() else 1f

    val formattedSize: String
        get() {
            val kb = size / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> "%.1f GB".format(gb)
                mb >= 1.0 -> "%.1f MB".format(mb)
                else -> "%.0f KB".format(kb)
            }
        }

    val formattedDuration: String
        get() {
            val totalSeconds = duration / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                "%d:%02d:%02d".format(hours, minutes, seconds)
            } else {
                "%d:%02d".format(minutes, seconds)
            }
        }

    val formattedResolution: String
        get() = "${width}×${height}"
}

@Serializable
data class MediaAlbum(
    val id: String,
    val name: String,
    val coverUri: String?,
    val itemCount: Int,
    val latestDate: Long,
)

@Serializable
enum class MediaSortOrder {
    DATE_DESC,
    DATE_ASC,
    NAME_ASC,
    NAME_DESC,
    SIZE_DESC,
    SIZE_ASC,
}

@Serializable
enum class MediaFilter {
    ALL,
    PHOTOS,
    VIDEOS,
    FAVORITES,
    RAW,
    SCREENSHOTS,
}
