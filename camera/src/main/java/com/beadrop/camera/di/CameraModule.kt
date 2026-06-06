package com.beadrop.camera.di

import android.content.Context
import com.beadrop.camera.capture.PhotoCaptureManager
import com.beadrop.camera.capture.VideoCaptureManager
import com.beadrop.camera.engine.CameraEngine
import com.beadrop.camera.zoom.ZoomController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CameraModule {

    @Provides
    @Singleton
    fun provideCameraEngine(
        @ApplicationContext context: Context,
    ): CameraEngine = CameraEngine(context)

    @Provides
    @Singleton
    fun providePhotoCaptureManager(
        @ApplicationContext context: Context,
    ): PhotoCaptureManager = PhotoCaptureManager(context)

    @Provides
    @Singleton
    fun provideVideoCaptureManager(
        @ApplicationContext context: Context,
    ): VideoCaptureManager = VideoCaptureManager(context)

    @Provides
    @Singleton
    fun provideZoomController(): ZoomController = ZoomController()
}
