package com.example.sakura_flashcard.data.repository

import com.example.sakura_flashcard.data.algorithm.LearningRecommendations
import com.example.sakura_flashcard.data.algorithm.SpacedRepetitionAlgorithm
import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.api.toFlashcard
import com.example.sakura_flashcard.data.api.toFlashcardModels
import com.example.sakura_flashcard.data.api.toLearningProgress
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.data.sync.SyncResult
import com.example.sakura_flashcard.data.sync.SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing spaced repetition data and flashcard recommendations.
 * Handles both local caching and backend synchronization.
 * Note: Sync service has been removed - data is stored locally only.
 */
@Singleton
class SpacedRepetitionRepository @Inject constructor(
    private val apiService: ApiService,
    private val spacedRepetitionAlgorithm: SpacedRepetitionAlgorithm
) {
    
    // In-memory cache for spaced repetition data
    private val spacedRepetitionCache = mutableMapOf<String, SpacedRepetitionData>()
    private val userProgressCache = mutableMapOf<String, LearningProgress>()
    
    /**
     * Gets recommended flashcards for a user using spaced repetition algorithm
     */
    suspend fun getRecommendedFlashcards(
        userId: String,
        maxCards: Int = 20
    ): Result<List<Flashcard>> {
        return try {
            // Get all available flashcards
            val flashcardsResponse = apiService.getRecommendedFlashcards(limit = maxCards)
            if (!flashcardsResponse.isSuccessful) {
                return Result.failure(Exception("Failed to fetch flashcards: ${flashcardsResponse.message()}"))
            }
            
            val allFlashcards = flashcardsResponse.body()?.data?.map { it.toFlashcard() } ?: emptyList()
            
            // Get user's spaced repetition data
            val spacedRepetitionData = getSpacedRepetitionData(userId)
            
            // Get user progress
            val userProgress = getUserProgress(userId)
            
            // Generate recommendations using algorithm
            val recommendedFlashcards = spacedRepetitionAlgorithm.generateRecommendedFlashcards(
                allFlashcards = allFlashcards,
                spacedRepetitionData = spacedRepetitionData,
                userProgress = userProgress,
                maxCards = maxCards
            )
            
            Result.success(recommendedFlashcards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates flashcard progress after user interaction
     */
    suspend fun updateFlashcardProgress(
        userId: String,
        flashcardId: String,
        quality: ReviewQuality,
        responseTime: Long = 0L
    ): Result<SpacedRepetitionData> {
        return try {
            // Get existing spaced repetition data
            val existingData = getSpacedRepetitionDataForCard(userId, flashcardId)
            
            // Update using algorithm
            val updatedData = spacedRepetitionAlgorithm.updateAfterReview(
                flashcardId = flashcardId,
                userId = userId,
                quality = quality,
                responseTime = responseTime,
                existingData = existingData
            )
            
            // Update local cache
            spacedRepetitionCache["${userId}_${flashcardId}"] = updatedData
            

            // Update user progress if card was learned
            if (quality.isCorrect() && (existingData?.correctStreak ?: 0) == 0) {
                updateUserProgress(userId, isFlashcardLearned = true)
            }
            
            Result.success(updatedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets learning recommendations for a user
     */
    suspend fun getLearningRecommendations(userId: String): Result<LearningRecommendations> {
        return try {
            val spacedRepetitionData = getSpacedRepetitionData(userId)
            val userProgress = getUserProgress(userId)
            
            // Get all flashcards to analyze topics and levels
            val flashcardsResponse = apiService.getRecommendedFlashcards(limit = 20)
            val allFlashcards = flashcardsResponse.body()?.data?.map { it.toFlashcard() } ?: emptyList()
            
            val recommendations = spacedRepetitionAlgorithm.generateLearningRecommendations(
                userProgress = userProgress,
                spacedRepetitionData = spacedRepetitionData,
                allFlashcards = allFlashcards
            )
            
            Result.success(recommendations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets spaced repetition data for all cards of a user
     */
    private suspend fun getSpacedRepetitionData(userId: String): List<SpacedRepetitionData> {
        // In a real implementation, this would fetch from local database first,
        // then sync with backend if needed
        return spacedRepetitionCache.values.filter { it.userId == userId }
    }
    
    /**
     * Gets spaced repetition data for a specific card
     */
    private suspend fun getSpacedRepetitionDataForCard(
        userId: String, 
        flashcardId: String
    ): SpacedRepetitionData? {
        return spacedRepetitionCache["${userId}_${flashcardId}"]
    }
    
    /**
     * Gets user progress from cache or backend
     */
    private suspend fun getUserProgress(userId: String): LearningProgress {
        // Check cache first
        userProgressCache[userId]?.let { return it }
        
        // Fetch from backend
        return try {
            val response = apiService.getUserProgress()
            if (response.isSuccessful) {
                val progress = response.body()?.data?.toLearningProgress() ?: LearningProgress()
                userProgressCache[userId] = progress
                progress
            } else {
                LearningProgress()
            }
        } catch (e: Exception) {
            LearningProgress()
        }
    }
    
    /**
     * Updates user progress after learning activities
     */
    private suspend fun updateUserProgress(
        userId: String,
        isFlashcardLearned: Boolean = false,
        isQuizCompleted: Boolean = false,
        studyTimeMinutes: Long = 0L
    ) {
        try {
            val currentProgress = getUserProgress(userId)
            
            val updatedProgress = currentProgress.let { progress ->
                var updated = progress
                if (isFlashcardLearned) {
                    updated = updated.addFlashcardLearned().incrementStreak()
                }
                if (isQuizCompleted) {
                    updated = updated.addQuizCompleted()
                }
                if (studyTimeMinutes > 0) {
                    updated = updated.addStudyTime(studyTimeMinutes)
                }
                updated
            }
            
            // Update cache
            userProgressCache[userId] = updatedProgress
            

        } catch (e: Exception) {
            // Log error but don't fail the operation
            println("Failed to update user progress: ${e.message}")
        }
    }
    
    /**
     * Forces immediate sync of all pending operations (no-op - sync not implemented)
     */
    suspend fun forceSyncNow(): SyncResult {
        return SyncResult.Success
    }
    
    /**
     * Gets current sync status
     */
    fun getSyncStatus(): SyncState {
        return SyncState.Idle
    }
    
    /**
     * Converts spaced repetition data to difficulty score for backend compatibility
     */
    private fun calculateDifficultyFromSpacedRepetition(data: SpacedRepetitionData): Float {
        // Convert ease factor and other metrics to a difficulty score (1.0 - 5.0)
        val baseDifficulty = (4.0f - data.easeFactor).coerceIn(0.1f, 5.0f)
        val adjustedDifficulty = baseDifficulty + data.difficultyAdjustment
        return adjustedDifficulty.coerceIn(0.1f, 5.0f)
    }
    
    /**
     * Gets cards due for review
     */
    suspend fun getDueCards(userId: String): Result<List<Flashcard>> {
        return try {
            val spacedRepetitionData = getSpacedRepetitionData(userId)
            val dueCardIds = spacedRepetitionData
                .filter { it.isDueForReview() }
                .sortedByDescending { it.getPriorityScore() }
                .map { it.flashcardId }
            
            // Fetch flashcard details
            val flashcardsResponse = apiService.getRecommendedFlashcards(limit = 20)
            val allFlashcards = flashcardsResponse.body()?.data?.map { it.toFlashcard() } ?: emptyList()
            
            val dueFlashcards = allFlashcards.filter { it.id in dueCardIds }
                .sortedBy { dueCardIds.indexOf(it.id) }
            
            Result.success(dueFlashcards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets learning statistics for analytics
     */
    suspend fun getLearningStatistics(userId: String): Result<LearningStatistics> {
        return try {
            val spacedRepetitionData = getSpacedRepetitionData(userId)
            val userProgress = getUserProgress(userId)
            
            val statistics = LearningStatistics(
                totalCardsStudied = spacedRepetitionData.size,
                cardsLearned = spacedRepetitionData.count { it.getMasteryLevel() >= 0.8f },
                cardsDue = spacedRepetitionData.count { it.isDueForReview() },
                averageMasteryLevel = spacedRepetitionData.map { it.getMasteryLevel() }.average().toFloat(),
                currentStreak = userProgress.currentStreak,
                totalStudyTime = userProgress.totalStudyTimeMinutes,
                quizzesCompleted = userProgress.quizzesCompleted,
                levelProgress = userProgress.levelProgress
            )
            
            Result.success(statistics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clears local cache (useful for logout or data refresh)
     */
    fun clearCache() {
        spacedRepetitionCache.clear()
        userProgressCache.clear()
    }
}

/**
 * Data class for learning statistics
 */
data class LearningStatistics(
    val totalCardsStudied: Int,
    val cardsLearned: Int,
    val cardsDue: Int,
    val averageMasteryLevel: Float,
    val currentStreak: Int,
    val totalStudyTime: Long,
    val quizzesCompleted: Int,
    val levelProgress: Map<JLPTLevel, Float>
)