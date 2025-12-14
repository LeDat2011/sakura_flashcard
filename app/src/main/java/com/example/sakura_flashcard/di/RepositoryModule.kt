package com.example.sakura_flashcard.di

import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.auth.AuthRepository
import com.example.sakura_flashcard.data.auth.AuthTokenManager
import com.example.sakura_flashcard.data.repository.QuizRepository
import com.example.sakura_flashcard.data.repository.VocabularyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideVocabularyRepository(
        apiService: ApiService
    ): VocabularyRepository {
        return VocabularyRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideQuizRepository(
        apiService: ApiService
    ): QuizRepository {
        return QuizRepository(apiService)
    }
}
