package com.example.sakura_flashcard.data.repository

import androidx.paging.PagingData
import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.api.toFlashcard
import com.example.sakura_flashcard.data.api.toFlashcardModels
import com.example.sakura_flashcard.data.api.toModel
import com.example.sakura_flashcard.data.api.toDto
import com.example.sakura_flashcard.data.error.ErrorContext
import com.example.sakura_flashcard.data.error.ErrorHandler
import com.example.sakura_flashcard.data.error.RetryManager
import com.example.sakura_flashcard.data.error.executeDataOperation
import com.example.sakura_flashcard.data.local.FlashcardDatabase
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.data.network.NetworkConnectivityManager
import com.example.sakura_flashcard.data.performance.ImageCacheManager
import com.example.sakura_flashcard.data.performance.LazyLoadingManager
import com.example.sakura_flashcard.data.performance.PerformanceMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced repository with performance optimization and error handling
 */
@Singleton
class EnhancedRepository @Inject constructor(
    private val apiService: ApiService,
    private val database: FlashcardDatabase,
    private val lazyLoadingManager: LazyLoadingManager,
    private val imageCacheManager: ImageCacheManager,
    private val performanceMonitor: PerformanceMonitor,
    private val errorHandler: ErrorHandler,
    private val retryManager: RetryManager,
    private val networkManager: NetworkConnectivityManager
) {
    
    // ===== FLASHCARD OPERATIONS WITH PERFORMANCE OPTIMIZATION =====
    
    /**
     * Gets flashcards with lazy loading and performance monitoring
     */
    suspend fun getFlashcardsOptimized(
        topic: VocabularyTopic,
        level: JLPTLevel
    ): Flow<PagingData<Flashcard>> {
        return performanceMonitor.measureOperation("get_flashcards_optimized") {
            // Preload images for better performance
            preloadFlashcardImages(topic, level)
            
            lazyLoadingManager.getPagedFlashcards(topic, level)
        }
    }
    
    /**
     * Gets recommended flashcards with retry and error handling
     */
    suspend fun getRecommendedFlashcardsOptimized(userId: String): Result<List<Flashcard>> {
        return try {
            val flashcards = retryManager.executeDataOperation {
                performanceMonitor.measureOperation("get_recommended_flashcards") {
                    val response = apiService.getRecommendedFlashcards(limit = 20)
                    if (response.isSuccessful) {
                        val flashcards = response.body()?.data?.map { it.toFlashcard() } ?: emptyList()
                        
                        // Preload images for recommended flashcards
                        preloadFlashcardImages(flashcards)
                        
                        // Cache locally
                        cacheFlashcards(flashcards)
                        
                        flashcards
                    } else {
                        throw Exception("Failed to get recommended flashcards: ${response.code()}")
                    }
                }
            }
            Result.success(flashcards)
        } catch (e: Exception) {
            val appError = errorHandler.handleError(
                throwable = e,
                context = ErrorContext.FLASHCARD_LOADING,
                retryAction = { getRecommendedFlashcardsOptimized(userId) }
            )
            Result.failure(appError.originalException ?: e)
        }
    }
    
    /**
     * Creates custom flashcard with validation and error handling
     */
    suspend fun createCustomFlashcardOptimized(flashcard: Flashcard): Result<Flashcard> {
        return try {
            val result = retryManager.executeDataOperation {
                performanceMonitor.measureOperation("create_custom_flashcard") {
                    // Validate flashcard data
                    validateFlashcard(flashcard)
                    
                    // Save locally first for offline support
                    val entity = com.example.sakura_flashcard.data.local.entity.FlashcardEntity.fromFlashcard(
                        flashcard, 
                        needsSync = true
                    )
                    database.flashcardDao().insertFlashcard(entity)
                    
                    // Try to sync with server if online
                    if (networkManager.isCurrentlyOnline()) {
                        try {
                            val response = apiService.createCustomFlashcard(flashcard.toDto())
                            if (response.isSuccessful) {
                                // Mark as synced
                                database.flashcardDao().markAsSynced(flashcard.id)
                            }
                        } catch (e: Exception) {
                            // Sync will be retried later by background service
                        }
                    }
                    
                    flashcard
                }
            }
            Result.success(result)
        } catch (e: Exception) {
            val appError = errorHandler.handleError(
                throwable = e,
                context = ErrorContext.FLASHCARD_LOADING,
                retryAction = { createCustomFlashcardOptimized(flashcard) }
            )
            Result.failure(appError.originalException ?: e)
        }
    }
    
    // ===== CHARACTER OPERATIONS WITH PERFORMANCE OPTIMIZATION =====
    
    /**
     * Gets characters with lazy loading
     */
    suspend fun getCharactersOptimized(script: CharacterScript): Flow<PagingData<Character>> {
        return performanceMonitor.measureOperation("get_characters_optimized") {
            lazyLoadingManager.getPagedCharacters(script)
        }
    }
    
    /**
     * Gets character detail with image caching
     */
    suspend fun getCharacterDetailOptimized(characterId: String): Result<Character> {
        return try {
            val character = retryManager.executeDataOperation {
                performanceMonitor.measureOperation("get_character_detail") {
                    // Try local first
                    val localCharacter = database.characterDao().getCharacterById(characterId)?.toCharacter()
                    
                    if (localCharacter != null) {
                        // Preload stroke order images
                        preloadCharacterImages(localCharacter)
                        return@measureOperation localCharacter
                    }
                    
                    // Fetch from network
                    val response = apiService.getCharacterDetail(characterId)
                    if (response.isSuccessful) {
                        val character = response.body()?.toModel()!!
                        
                        // Cache locally
                        val entity = com.example.sakura_flashcard.data.local.entity.CharacterEntity.fromCharacter(character)
                        database.characterDao().insertCharacter(entity)
                        
                        // Preload images
                        preloadCharacterImages(character)
                        
                        character
                    } else {
                        throw Exception("Character not found: $characterId")
                    }
                }
            }
            Result.success(character)
        } catch (e: Exception) {
            val appError = errorHandler.handleError(
                throwable = e,
                context = ErrorContext.CHARACTER_LOADING,
                retryAction = { getCharacterDetailOptimized(characterId) }
            )
            Result.failure(appError.originalException ?: e)
        }
    }
    
    // ===== QUIZ OPERATIONS WITH PERFORMANCE OPTIMIZATION =====
    
    /**
     * Gets quiz results with lazy loading
     */
    suspend fun getQuizResultsOptimized(userId: String): Flow<PagingData<QuizResult>> {
        return performanceMonitor.measureOperation("get_quiz_results_optimized") {
            lazyLoadingManager.getPagedQuizResults(userId)
        }
    }
    
    /**
     * Submits quiz result with retry logic
     */
    suspend fun submitQuizResultOptimized(result: QuizResult): Result<QuizResult> {
        return try {
            val submittedResult = retryManager.executeDataOperation {
                performanceMonitor.measureOperation("submit_quiz_result") {
                    // Save locally first
                    val entity = com.example.sakura_flashcard.data.local.entity.QuizResultEntity.fromQuizResult(
                        result, 
                        needsSync = true
                    )
                    database.quizResultDao().insertQuizResult(entity)
                    
                    // Try to sync with server if online
                    if (networkManager.isCurrentlyOnline()) {
                        try {
                            val response = apiService.submitQuizResult(result.toDto())
                            if (response.isSuccessful) {
                                database.quizResultDao().markAsSynced(result.id)
                            }
                        } catch (e: Exception) {
                            // Will be synced later
                        }
                    }
                    
                    result
                }
            }
            Result.success(submittedResult)
        } catch (e: Exception) {
            val appError = errorHandler.handleError(
                throwable = e,
                context = ErrorContext.QUIZ_SUBMISSION,
                retryAction = { submitQuizResultOptimized(result) }
            )
            Result.failure(appError.originalException ?: e)
        }
    }
    
    // ===== GAME OPERATIONS WITH PERFORMANCE OPTIMIZATION =====
    
    /**
     * Gets game results with lazy loading
     */
    suspend fun getGameResultsOptimized(userId: String): Flow<PagingData<GameResult>> {
        return performanceMonitor.measureOperation("get_game_results_optimized") {
            lazyLoadingManager.getPagedGameResults(userId)
        }
    }
    
    /**
     * Submits game result with retry logic
     */
    suspend fun submitGameResultOptimized(result: GameResult): Result<GameResult> {
        return try {
            val submittedResult = retryManager.executeDataOperation {
                performanceMonitor.measureOperation("submit_game_result") {
                    // Save locally first
                    val entity = com.example.sakura_flashcard.data.local.entity.GameResultEntity.fromGameResult(
                        result, 
                        needsSync = true
                    )
                    database.gameResultDao().insertGameResult(entity)
                    
                    // Try to sync with server if online
                    if (networkManager.isCurrentlyOnline()) {
                        try {
                            val response = apiService.submitGameResult(result.toDto())
                            if (response.isSuccessful) {
                                database.gameResultDao().markAsSynced(result.id)
                            }
                        } catch (e: Exception) {
                            // Will be synced later
                        }
                    }
                    
                    result
                }
            }
            Result.success(submittedResult)
        } catch (e: Exception) {
            val appError = errorHandler.handleError(
                throwable = e,
                context = ErrorContext.GAME_SUBMISSION,
                retryAction = { submitGameResultOptimized(result) }
            )
            Result.failure(appError.originalException ?: e)
        }
    }
    
    // ===== IMAGE OPTIMIZATION METHODS =====
    
    /**
     * Preloads images for flashcards
     */
    private suspend fun preloadFlashcardImages(topic: VocabularyTopic, level: JLPTLevel) {
        try {
            // Get a sample of flashcards to preload images
            val sampleFlashcards = database.flashcardDao()
                .getFlashcardsByTopicAndLevelPaged(topic, level, 10, 0)
                .map { it.toFlashcard() }
            
            preloadFlashcardImages(sampleFlashcards)
        } catch (e: Exception) {
            // Preloading is optional, don't fail the main operation
        }
    }
    
    /**
     * Preloads images for a list of flashcards
     */
    private suspend fun preloadFlashcardImages(flashcards: List<Flashcard>) {
        try {
            val imageUrls = flashcards.mapNotNull { flashcard ->
                listOfNotNull(
                    flashcard.front.image,
                    flashcard.back.image
                )
            }.flatten()
            
            if (imageUrls.isNotEmpty()) {
                imageCacheManager.preloadImages(imageUrls)
            }
        } catch (e: Exception) {
            // Preloading is optional
        }
    }
    
    /**
     * Preloads images for a character
     */
    private suspend fun preloadCharacterImages(character: Character) {
        try {
            // Preload stroke order images if available
            val strokeOrderImages = character.strokeOrder.mapNotNull { it.imageUrl }
            if (strokeOrderImages.isNotEmpty()) {
                imageCacheManager.preloadImages(strokeOrderImages)
            }
        } catch (e: Exception) {
            // Preloading is optional
        }
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Validates flashcard data
     */
    private fun validateFlashcard(flashcard: Flashcard) {
        require(flashcard.front.text.isNotBlank()) { "Front text cannot be empty" }
        require(flashcard.back.text.isNotBlank()) { "Back text cannot be empty" }
        require(flashcard.front.text.length <= 500) { "Front text too long" }
        require(flashcard.back.text.length <= 500) { "Back text too long" }
    }
    
    /**
     * Caches flashcards locally
     */
    private suspend fun cacheFlashcards(flashcards: List<Flashcard>) {
        try {
            val entities = flashcards.map { 
                com.example.sakura_flashcard.data.local.entity.FlashcardEntity.fromFlashcard(it, needsSync = false) 
            }
            database.flashcardDao().insertFlashcards(entities)
        } catch (e: Exception) {
            // Caching failure shouldn't break the main operation
        }
    }
    
    /**
     * Gets performance statistics
     */
    fun getPerformanceStats() = performanceMonitor.getPerformanceSummary()
    
    /**
     * Gets image cache statistics
     */
    fun getImageCacheStats() = imageCacheManager.getCacheStats()
    
    /**
     * Clears caches for memory management
     */
    suspend fun clearCaches() {
        imageCacheManager.clearMemoryCache()
        imageCacheManager.clearDiskCache()
    }
}