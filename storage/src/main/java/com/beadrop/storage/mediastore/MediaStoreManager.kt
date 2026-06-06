package com.beadrop.storage.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.beadrop.core.domain.model.MediaAlbum
import com.beadrop.core.domain.model.MediaFilter
import com.beadrop.core.domain.model.MediaItem
import com.beadrop.core.domain.model.MediaSortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MediaStore integration for accessing and managing device media.
 * 
 * Supports JPEG, PNG, WEBP, HEIC, RAW DNG, MP4.
 */
@Singleton
class MediaStoreManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val contentResolver: ContentResolver = context.contentResolver

    private val imageProjection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.WIDTH,
        MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_MODIFIED,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.ORIENTATION,
    )

    private val videoProjection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.MIME_TYPE,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.WIDTH,
        MediaStore.Video.Media.HEIGHT,
        MediaStore.Video.Media.DATE_ADDED,
        MediaStore.Video.Media.DATE_MODIFIED,
        MediaStore.Video.Media.DATE_TAKEN,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.BUCKET_ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
    )

    /**
     * Query all images from MediaStore.
     */
    suspend fun queryImages(
        sortOrder: MediaSortOrder = MediaSortOrder.DATE_DESC,
        limit: Int = 500,
    ): List<MediaItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<MediaItem>()
        val sort = when (sortOrder) {
            MediaSortOrder.DATE_DESC -> "${MediaStore.Images.Media.DATE_ADDED} DESC"
            MediaSortOrder.DATE_ASC -> "${MediaStore.Images.Media.DATE_ADDED} ASC"
            MediaSortOrder.NAME_ASC -> "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            MediaSortOrder.NAME_DESC -> "${MediaStore.Images.Media.DISPLAY_NAME} DESC"
            MediaSortOrder.SIZE_DESC -> "${MediaStore.Images.Media.SIZE} DESC"
            MediaSortOrder.SIZE_ASC -> "${MediaStore.Images.Media.SIZE} ASC"
        }

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        contentResolver.query(
            uri,
            imageProjection,
            null,
            null,
            "$sort LIMIT $limit",
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val orientationColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )

                items.add(
                    MediaItem(
                        id = id,
                        uri = contentUri.toString(),
                        displayName = cursor.getString(nameColumn) ?: "",
                        mimeType = cursor.getString(mimeColumn) ?: "image/jpeg",
                        size = cursor.getLong(sizeColumn),
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        dateAdded = cursor.getLong(dateAddedColumn) * 1000,
                        dateModified = cursor.getLong(dateModifiedColumn) * 1000,
                        dateTaken = cursor.getLong(dateTakenColumn),
                        bucketId = cursor.getString(bucketIdColumn) ?: "",
                        bucketDisplayName = cursor.getString(bucketNameColumn) ?: "",
                        orientation = cursor.getInt(orientationColumn),
                    )
                )
            }
        }

        items
    }

    /**
     * Query all videos from MediaStore.
     */
    suspend fun queryVideos(
        sortOrder: MediaSortOrder = MediaSortOrder.DATE_DESC,
        limit: Int = 500,
    ): List<MediaItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<MediaItem>()
        val sort = when (sortOrder) {
            MediaSortOrder.DATE_DESC -> "${MediaStore.Video.Media.DATE_ADDED} DESC"
            MediaSortOrder.DATE_ASC -> "${MediaStore.Video.Media.DATE_ADDED} ASC"
            MediaSortOrder.NAME_ASC -> "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
            MediaSortOrder.NAME_DESC -> "${MediaStore.Video.Media.DISPLAY_NAME} DESC"
            MediaSortOrder.SIZE_DESC -> "${MediaStore.Video.Media.SIZE} DESC"
            MediaSortOrder.SIZE_ASC -> "${MediaStore.Video.Media.SIZE} ASC"
        }

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        contentResolver.query(
            uri,
            videoProjection,
            null,
            null,
            "$sort LIMIT $limit",
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                )

                items.add(
                    MediaItem(
                        id = id,
                        uri = contentUri.toString(),
                        displayName = cursor.getString(nameColumn) ?: "",
                        mimeType = cursor.getString(mimeColumn) ?: "video/mp4",
                        size = cursor.getLong(sizeColumn),
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        dateAdded = cursor.getLong(dateAddedColumn) * 1000,
                        dateModified = cursor.getLong(dateModifiedColumn) * 1000,
                        dateTaken = cursor.getLong(dateTakenColumn),
                        duration = cursor.getLong(durationColumn),
                        bucketId = cursor.getString(bucketIdColumn) ?: "",
                        bucketDisplayName = cursor.getString(bucketNameColumn) ?: "",
                    )
                )
            }
        }

        items
    }

    /**
     * Query all media (images + videos) merged and sorted.
     */
    suspend fun queryAllMedia(
        sortOrder: MediaSortOrder = MediaSortOrder.DATE_DESC,
        filter: MediaFilter = MediaFilter.ALL,
        limit: Int = 1000,
    ): List<MediaItem> = withContext(Dispatchers.IO) {
        when (filter) {
            MediaFilter.PHOTOS -> queryImages(sortOrder, limit)
            MediaFilter.VIDEOS -> queryVideos(sortOrder, limit)
            MediaFilter.RAW -> queryImages(sortOrder, limit).filter { it.isRaw }
            MediaFilter.FAVORITES -> {
                (queryImages(sortOrder, limit) + queryVideos(sortOrder, limit))
                    .filter { it.isFavorite }
                    .sortedByDescending { it.dateAdded }
            }
            MediaFilter.SCREENSHOTS -> {
                queryImages(sortOrder, limit).filter {
                    it.bucketDisplayName.equals("Screenshots", ignoreCase = true)
                }
            }
            MediaFilter.ALL -> {
                val allItems = queryImages(sortOrder, limit / 2) + queryVideos(sortOrder, limit / 2)
                when (sortOrder) {
                    MediaSortOrder.DATE_DESC -> allItems.sortedByDescending { it.dateAdded }
                    MediaSortOrder.DATE_ASC -> allItems.sortedBy { it.dateAdded }
                    MediaSortOrder.NAME_ASC -> allItems.sortedBy { it.displayName }
                    MediaSortOrder.NAME_DESC -> allItems.sortedByDescending { it.displayName }
                    MediaSortOrder.SIZE_DESC -> allItems.sortedByDescending { it.size }
                    MediaSortOrder.SIZE_ASC -> allItems.sortedBy { it.size }
                }
            }
        }
    }

    /**
     * Query albums.
     */
    suspend fun queryAlbums(): List<MediaAlbum> = withContext(Dispatchers.IO) {
        val albumMap = mutableMapOf<String, MutableList<MediaItem>>()
        val allMedia = queryAllMedia(limit = 2000)

        allMedia.forEach { item ->
            val bucketName = item.bucketDisplayName.ifEmpty { "Other" }
            albumMap.getOrPut(bucketName) { mutableListOf() }.add(item)
        }

        albumMap.map { (name, items) ->
            MediaAlbum(
                id = items.first().bucketId,
                name = name,
                coverUri = items.maxByOrNull { it.dateAdded }?.uri,
                itemCount = items.size,
                latestDate = items.maxOf { it.dateAdded },
            )
        }.sortedByDescending { it.latestDate }
    }

    /**
     * Delete media items.
     */
    suspend fun deleteMedia(items: List<MediaItem>): Int = withContext(Dispatchers.IO) {
        var deletedCount = 0
        items.forEach { item ->
            val uri = Uri.parse(item.uri)
            val deleted = contentResolver.delete(uri, null, null)
            deletedCount += deleted
        }
        deletedCount
    }

    /**
     * Rename a media item.
     */
    suspend fun renameMedia(item: MediaItem, newName: String): Boolean = withContext(Dispatchers.IO) {
        val uri = Uri.parse(item.uri)
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, newName)
        }
        contentResolver.update(uri, values, null, null) > 0
    }

    /**
     * Observe media changes.
     */
    fun observeMediaChanges(): Flow<Unit> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(Unit)
            }
        }

        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer,
        )
        contentResolver.registerContentObserver(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            true,
            observer,
        )

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }
}
