package com.beadrop.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.beadrop.storage.database.dao.EditHistoryDao
import com.beadrop.storage.database.dao.FavoriteDao
import com.beadrop.storage.database.dao.RecycleBinDao
import com.beadrop.storage.database.entity.EditHistoryEntity
import com.beadrop.storage.database.entity.FavoriteEntity
import com.beadrop.storage.database.entity.RecycleBinEntity

@Database(
    entities = [
        FavoriteEntity::class,
        RecycleBinEntity::class,
        EditHistoryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class BeadropDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recycleBinDao(): RecycleBinDao
    abstract fun editHistoryDao(): EditHistoryDao

    companion object {
        const val DATABASE_NAME = "beadrop_camera.db"
    }
}
