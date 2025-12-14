package com.example.sakura_flashcard.di

import android.content.Context
import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.error.ErrorHandler
import com.example.sakura_flashcard.data.error.RetryManager
import com.example.sakura_flashcard.data.local.FlashcardDatabase
import com.example.sakura_flashcard.data.performance.ImageCacheManager
import com.example.sakura_flashcard.data.performance.LazyLoadingManager
import com.example.sakura_flashcard.data.performance.PerformanceMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for performance optimization components
 */
@Module
@InstallIn(SingletonComponent::class)
object PerformanceModule {
    
    @Provides
    @Singleton
    fun provideImageCacheManager(
        @ApplicationContext context: Context
    ): ImageCacheManager {
        return ImageCacheManager(context)
    }
    
    @Provides
    @Singleton
    fun provideLazyLoadingManager(
        apiService: ApiService,
        database: FlashcardDatabase
    ): LazyLoadingManager {
        return LazyLoadingManager(apiService, database)
    }
    
    @Provides
    @Singleton
    fun providePerformanceMonitor(): PerformanceMonitor {
        return PerformanceMonitor()
    }
    
    @Provides
    @Singleton
    fun provideErrorHandler(
        @ApplicationContext context: Context
    ): ErrorHandler {
        return ErrorHandler(context)
    }
    
    @Provides
    @Singleton
    fun provideRetryManager(): RetryManager {
        return RetryManager()
    }
}