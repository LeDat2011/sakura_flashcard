package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.auth.AuthResult
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.data.recommendation.UserPreferences
import com.example.sakura_flashcard.data.sync.SyncResult
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant

// Mock interfaces for testing

interface AuthRepository {
    val authState: StateFlow<com.example.sakura_flashcard.data.auth.AuthState>
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(username: String, email: String, password: String): AuthResult
    suspend fun refreshToken(): AuthResult
    suspend fun logout(): AuthResult
    fun isAuthenticated(): Boolean
    fun getCurrentUserId(): String?
}

interface SpacedRepetitionRepository {
    suspend fun getRecommendedFlashcards(userId: String, maxCards: Int = 20): Result<List<Flashcard>>
    suspend fun updateFlashcardProgress(
        userId: String,
        flashcardId: String,
        quality: ReviewQuality,
        responseTime: Long = 0L
    ): Result<SpacedRepetitionData>
    suspend fun getLearningStatistics(userId: String): Result<LearningStatistics>
}

interface OfflineRepository {
    suspend fun getUser(userId: String): Result<User>
    suspend fun updateUser(user: User): Result<User>
    suspend fun getCharactersByScript(script: CharacterScript): Result<List<Character>>
    suspend fun generateQuiz(topic: VocabularyTopic, level: JLPTLevel, questionCount: Int): Result<List<com.example.sakura_flashcard.data.model.QuizQuestion>>
    suspend fun submitQuizResult(result: com.example.sakura_flashcard.data.model.QuizResult): Result<com.example.sakura_flashcard.data.model.QuizResult>
    suspend fun submitGameResult(result: com.example.sakura_flashcard.data.model.GameResult): Result<com.example.sakura_flashcard.data.model.GameResult>
    suspend fun getUserGameResults(userId: String): Result<List<com.example.sakura_flashcard.data.model.GameResult>>
    suspend fun getRecommendedFlashcards(userId: String, maxCards: Int): Result<List<Flashcard>>
    fun clearCache()
}

interface OfflineSyncService {
    val syncState: StateFlow<com.example.sakura_flashcard.data.sync.SyncState>
    suspend fun queueSpacedRepetitionUpdate(data: SpacedRepetitionData)
    suspend fun forceSyncNow(): Result<SyncResult>
}

// Additional data classes needed for testing

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

// Remove duplicate QuizQuestion - use the one from data.model package

// QuizResult and QuizAnswer are imported from the actual data model

// GameResult is imported from the actual data model

// Extension functions for SpacedRepetitionData
fun SpacedRepetitionData.isDueForReview(): Boolean {
    return nextReview.isBefore(Instant.now()) || nextReview == Instant.now()
}

fun SpacedRepetitionData.getPriorityScore(): Float {
    val now = Instant.now()
    val overdueDays = if (nextReview.isBefore(now)) {
        (now.epochSecond - nextReview.epochSecond) / (24 * 3600)
    } else {
        0L
    }
    
    var priority = overdueDays.toFloat()
    priority += (2.5f - easeFactor) * 2
    priority += difficultyAdjustment * 3
    priority -= (correctStreak * 0.1f)
    
    return kotlin.math.max(0f, priority)
}

fun SpacedRepetitionData.getMasteryLevel(): Float {
    if (totalReviews == 0) return 0f
    
    val correctRate = correctStreak.toFloat() / totalReviews
    val easeBonus = (easeFactor - 1.3f) / (3.0f - 1.3f)
    val intervalBonus = kotlin.math.min(interval / 30f, 1f)
    
    return ((correctRate * 0.5f) + (easeBonus * 0.3f) + (intervalBonus * 0.2f)).coerceIn(0f, 1f)
}

// Mock creation functions for testing
fun createMockAuthRepository(): AuthRepository {
    return object : AuthRepository {
        override val authState: StateFlow<com.example.sakura_flashcard.data.auth.AuthState> = 
            kotlinx.coroutines.flow.MutableStateFlow(com.example.sakura_flashcard.data.auth.AuthState.Authenticated("test-user", "test-token"))
        
        override suspend fun login(email: String, password: String): AuthResult = 
            AuthResult.Success(User(
                id = "test-user",
                username = "testuser",
                email = email
            ))
        
        override suspend fun register(username: String, email: String, password: String): AuthResult = 
            AuthResult.Success(User(
                id = "test-user",
                username = username,
                email = email
            ))
        
        override suspend fun refreshToken(): AuthResult = 
            AuthResult.Success(User(
                id = "test-user",
                username = "testuser",
                email = "test@example.com"
            ))
        
        override suspend fun logout(): AuthResult = 
            AuthResult.Success(null)
        
        override fun isAuthenticated(): Boolean = true
        
        override fun getCurrentUserId(): String = "test-user"
    }
}

fun createMockSpacedRepetitionRepository(): SpacedRepetitionRepository {
    return object : SpacedRepetitionRepository {
        override suspend fun getRecommendedFlashcards(userId: String, maxCards: Int): Result<List<Flashcard>> = 
            Result.success(emptyList())
        
        override suspend fun updateFlashcardProgress(
            userId: String,
            flashcardId: String,
            quality: ReviewQuality,
            responseTime: Long
        ): Result<SpacedRepetitionData> = 
            Result.success(SpacedRepetitionData(
                id = "test",
                userId = userId,
                flashcardId = flashcardId,
                easeFactor = 2.5f,
                interval = 1,
                repetitionCount = 1,
                nextReview = Instant.now(),
                lastReviewed = Instant.now(),
                correctStreak = 1,
                totalReviews = 1,
                difficultyAdjustment = 0f
            ))
        
        override suspend fun getLearningStatistics(userId: String): Result<LearningStatistics> = 
            Result.success(LearningStatistics(
                totalCardsStudied = 10,
                cardsLearned = 5,
                cardsDue = 3,
                averageMasteryLevel = 0.7f,
                currentStreak = 2,
                totalStudyTime = 3600L,
                quizzesCompleted = 2,
                levelProgress = mapOf(JLPTLevel.N5 to 0.5f)
            ))
    }
}

fun createMockOfflineRepository(): OfflineRepository {
    return object : OfflineRepository {
        override suspend fun getUser(userId: String): Result<User> = 
            Result.success(User(
                id = userId,
                username = "testuser",
                email = "test@example.com",
                avatar = null,
                createdAt = Instant.now(),
                lastLogin = Instant.now(),
                learningProgress = LearningProgress()
            ))
        
        override suspend fun updateUser(user: User): Result<User> = Result.success(user)
        
        override suspend fun getCharactersByScript(script: CharacterScript): Result<List<Character>> = 
            Result.success(emptyList())
        
        override suspend fun generateQuiz(topic: VocabularyTopic, level: JLPTLevel, questionCount: Int): Result<List<com.example.sakura_flashcard.data.model.QuizQuestion>> = 
            Result.success(emptyList())
        
        override suspend fun submitQuizResult(result: com.example.sakura_flashcard.data.model.QuizResult): Result<com.example.sakura_flashcard.data.model.QuizResult> = 
            Result.success(result)
        
        override suspend fun submitGameResult(result: com.example.sakura_flashcard.data.model.GameResult): Result<com.example.sakura_flashcard.data.model.GameResult> = 
            Result.success(result)
        
        override suspend fun getUserGameResults(userId: String): Result<List<com.example.sakura_flashcard.data.model.GameResult>> = 
            Result.success(emptyList())
        
        override suspend fun getRecommendedFlashcards(userId: String, maxCards: Int): Result<List<Flashcard>> = 
            Result.success(emptyList())
        
        override fun clearCache() {}
    }
}

fun createMockSyncService(): OfflineSyncService {
    return object : OfflineSyncService {
        override val syncState: StateFlow<com.example.sakura_flashcard.data.sync.SyncState> = 
            kotlinx.coroutines.flow.MutableStateFlow(com.example.sakura_flashcard.data.sync.SyncState())
        
        override suspend fun queueSpacedRepetitionUpdate(data: SpacedRepetitionData) {}
        
        override suspend fun forceSyncNow(): Result<SyncResult> = 
            Result.success(SyncResult(success = true, syncedOperations = 0))
    }
}