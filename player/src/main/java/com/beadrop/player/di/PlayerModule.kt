package com.beadrop.player.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    // ExoPlayer instances are created per-screen via remember {} composables
    // No singleton player needed — lifecycle managed by Compose
}
