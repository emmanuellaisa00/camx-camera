package com.beadrop.storage.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val mediaId: Long,
    val uri: String,
    val dateAdded: Long = System.currentTimeMillis(),
)

@Entity(tableName = "recycle_bin")
data class RecycleBinEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalUri: String,
    val displayName: String,
    val mimeType: String,
    val size: Long,
    val dateDeleted: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000, // 30 days
    val cachedPath: String = "",
)

@Entity(tableName = "edit_history")
data class EditHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mediaId: Long,
    val originalUri: String,
    val editedUri: String,
    val actionsJson: String,
    val dateEdited: Long = System.currentTimeMillis(),
)
