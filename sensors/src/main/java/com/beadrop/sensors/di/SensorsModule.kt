package com.beadrop.sensors.di

import android.content.Context
import com.beadrop.sensors.light.LightSensorManager
import com.beadrop.sensors.orientation.OrientationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorsModule {

    @Provides
    @Singleton
    fun provideOrientationManager(
        @ApplicationContext context: Context,
    ): OrientationManager = OrientationManager(context)

    @Provides
    @Singleton
    fun provideLightSensorManager(
        @ApplicationContext context: Context,
    ): LightSensorManager = LightSensorManager(context)
}
