package com.beadrop.ai.di

import android.content.Context
import com.beadrop.ai.detection.FaceDetector
import com.beadrop.ai.enhancement.ImageEnhancer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideFaceDetector(
        @ApplicationContext context: Context,
    ): FaceDetector = FaceDetector(context)

    @Provides
    @Singleton
    fun provideImageEnhancer(
        @ApplicationContext context: Context,
    ): ImageEnhancer = ImageEnhancer(context)
}
