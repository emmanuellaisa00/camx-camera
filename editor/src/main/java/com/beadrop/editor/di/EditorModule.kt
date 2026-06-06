package com.beadrop.editor.di

import android.content.Context
import com.beadrop.editor.engine.EditEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EditorModule {

    @Provides
    @Singleton
    fun provideEditEngine(
        @ApplicationContext context: Context,
    ): EditEngine = EditEngine(context)
}
