package com.beadrop.storage.di

import android.content.Context
import androidx.room.Room
import com.beadrop.storage.database.BeadropDatabase
import com.beadrop.storage.database.dao.EditHistoryDao
import com.beadrop.storage.database.dao.FavoriteDao
import com.beadrop.storage.database.dao.RecycleBinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): BeadropDatabase {
        return Room.databaseBuilder(
            context,
            BeadropDatabase::class.java,
            BeadropDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFavoriteDao(db: BeadropDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideRecycleBinDao(db: BeadropDatabase): RecycleBinDao = db.recycleBinDao()

    @Provides
    fun provideEditHistoryDao(db: BeadropDatabase): EditHistoryDao = db.editHistoryDao()
}
