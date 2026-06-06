package com.beadrop.gallery.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GalleryModule {
    // Gallery uses MediaStoreManager from storage module
    // All dependencies injected via constructor injection
}
