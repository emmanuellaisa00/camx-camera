package com.beadrop.storage.database.dao

import androidx.room.*
import com.beadrop.storage.database.entity.EditHistoryEntity
import com.beadrop.storage.database.entity.FavoriteEntity
import com.beadrop.storage.database.entity.RecycleBinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY dateAdded DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mediaId = :mediaId)")
    fun isFavorite(mediaId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE mediaId = :mediaId")
    suspend fun removeFavorite(mediaId: Long)

    @Query("DELETE FROM favorites")
    suspend fun clearAll()
}

@Dao
interface RecycleBinDao {
    @Query("SELECT * FROM recycle_bin WHERE expiresAt > :now ORDER BY dateDeleted DESC")
    fun getActiveItems(now: Long = System.currentTimeMillis()): Flow<List<RecycleBinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(item: RecycleBinEntity)

    @Query("DELETE FROM recycle_bin WHERE id = :id")
    suspend fun removeItem(id: Long)

    @Query("DELETE FROM recycle_bin WHERE expiresAt <= :now")
    suspend fun cleanExpired(now: Long = System.currentTimeMillis())

    @Query("DELETE FROM recycle_bin")
    suspend fun emptyBin()

    @Query("SELECT COUNT(*) FROM recycle_bin WHERE expiresAt > :now")
    fun getItemCount(now: Long = System.currentTimeMillis()): Flow<Int>
}

@Dao
interface EditHistoryDao {
    @Query("SELECT * FROM edit_history WHERE mediaId = :mediaId ORDER BY dateEdited DESC")
    fun getHistoryForMedia(mediaId: Long): Flow<List<EditHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEntry(entry: EditHistoryEntity)

    @Query("DELETE FROM edit_history WHERE mediaId = :mediaId")
    suspend fun clearHistoryForMedia(mediaId: Long)

    @Query("DELETE FROM edit_history")
    suspend fun clearAll()
}
