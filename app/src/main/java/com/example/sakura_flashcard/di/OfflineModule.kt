package com.example.sakura_flashcard.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.sakura_flashcard.data.local.FlashcardDatabase
import com.example.sakura_flashcard.data.local.dao.*
import com.example.sakura_flashcard.data.network.NetworkConnectivityManager
import com.example.sakura_flashcard.data.repository.OfflineRepository
import com.example.sakura_flashcard.data.sync.ConflictResolutionService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for offline support components
 */
@Module
@InstallIn(SingletonComponent::class)
object OfflineModule {
    
    @Provides
    @Singleton
    fun provideFlashcardDatabase(@ApplicationContext context: Context): FlashcardDatabase {
        return Room.databaseBuilder(
            context,
            FlashcardDatabase::class.java,
            FlashcardDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideFlashcardDao(database: FlashcardDatabase): FlashcardDao {
        return database.flashcardDao()
    }
    
    @Provides
    fun provideUserDao(database: FlashcardDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideSpacedRepetitionDao(database: FlashcardDatabase): SpacedRepetitionDao {
        return database.spacedRepetitionDao()
    }
    
    @Provides
    fun provideQuizResultDao(database: FlashcardDatabase): QuizResultDao {
        return database.quizResultDao()
    }
    
    @Provides
    fun provideGameResultDao(database: FlashcardDatabase): GameResultDao {
        return database.gameResultDao()
    }
    
    @Provides
    fun provideCharacterDao(database: FlashcardDatabase): CharacterDao {
        return database.characterDao()
    }
    
    @Provides
    fun provideSyncOperationDao(database: FlashcardDatabase): SyncOperationDao {
        return database.syncOperationDao()
    }

    @Provides
    @Singleton
    fun provideCustomDeckDao(database: FlashcardDatabase): CustomDeckDao {
        return database.customDeckDao()
    }

    @Provides
    @Singleton
    fun provideCustomDeckRepository(customDeckDao: CustomDeckDao): com.example.sakura_flashcard.data.repository.CustomDeckRepository {
        return com.example.sakura_flashcard.data.repository.CustomDeckRepository(customDeckDao)
    }
    
    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(@ApplicationContext context: Context): NetworkConnectivityManager {
        return NetworkConnectivityManager(context)
    }
    

    
    @Provides
    @Singleton
    fun provideConflictResolutionService(): ConflictResolutionService {
        return ConflictResolutionService()
    }
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideOfflineRepository(
        database: FlashcardDatabase,
        apiService: com.example.sakura_flashcard.data.api.ApiService
    ): OfflineRepository {
        return OfflineRepository(database, apiService)
    }
}