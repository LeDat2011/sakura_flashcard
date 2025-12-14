package com.example.sakura_flashcard.data.repository

import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.api.toFlashcard
import com.example.sakura_flashcard.data.api.toFlashcardModels
import com.example.sakura_flashcard.data.api.toCharacterModels
import com.example.sakura_flashcard.data.api.toModel
import com.example.sakura_flashcard.data.api.toUser
import com.example.sakura_flashcard.data.local.FlashcardDatabase
import com.example.sakura_flashcard.data.local.entity.*
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.data.sync.SyncResult
import com.example.sakura_flashcard.data.sync.SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles offline-first data access
 * Note: Sync functionality has been removed - data is fetched directly from MongoDB backend
 */
@Singleton
class OfflineRepository @Inject constructor(
    private val database: FlashcardDatabase,
    private val apiService: ApiService
) {
    // Simple sync state for compatibility
    private val _syncState = MutableStateFlow(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState
    
    // ===== FLASHCARD OPERATIONS =====
    
    /**
     * Gets flashcards by topic and level, with offline-first approach
     */
    suspend fun getFlashcardsByTopicAndLevel(
        topic: VocabularyTopic,
        level: JLPTLevel,
        forceRefresh: Boolean = false
    ): Result<List<Flashcard>> {
        return try {
            // Try to get from local database first
            val localFlashcards = database.flashcardDao()
                .getFlashcardsByTopicAndLevel(topic, level)
                .map { it.toFlashcard() }
            
            // If we have local data and don't need to force refresh, return it
            if (localFlashcards.isNotEmpty() && !forceRefresh) {
                return Result.success(localFlashcards)
            }
            
            // Try to fetch from network
            try {
                val response = apiService.getFlashcardsByTopicAndLevel(topic.name, level.name)
                if (response.isSuccessful) {
                    val networkFlashcards = response.body()?.toFlashcardModels() ?: emptyList()
                    
                    // Cache the network data locally
                    val entities = networkFlashcards.map { 
                        FlashcardEntity.fromFlashcard(it, needsSync = false) 
                    }
                    database.flashcardDao().insertFlashcards(entities)
                    
                    Result.success(networkFlashcards)
                } else {
                    // Network failed, return local data if available
                    if (localFlashcards.isNotEmpty()) {
                        Result.success(localFlashcards)
                    } else {
                        Result.failure(Exception("No data available offline and network request failed"))
                    }
                }
            } catch (e: Exception) {
                // Network error, return local data if available
                if (localFlashcards.isNotEmpty()) {
                    Result.success(localFlashcards)
                } else {
                    Result.failure(e)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets recommended flashcards for a user
     */
    suspend fun getRecommendedFlashcards(
        userId: String,
        forceRefresh: Boolean = false
    ): Result<List<Flashcard>> {
        return try {
            // Try network first for recommendations (they're dynamic)
            try {
                val response = apiService.getRecommendedFlashcards(limit = 20)
                if (response.isSuccessful) {
                    val networkFlashcards = response.body()?.data?.map { it.toFlashcard() } ?: emptyList()
                    
                    // Cache the network data locally
                    val entities = networkFlashcards.map { 
                        FlashcardEntity.fromFlashcard(it, needsSync = false) 
                    }
                    database.flashcardDao().insertFlashcards(entities)
                    
                    return Result.success(networkFlashcards)
                }
            } catch (e: Exception) {
                // Network failed, fall back to local data
            }
            
            // Fallback to local flashcards (not ideal for recommendations, but better than nothing)
            // For offline mode, return a subset as "recommended"
            val entities = database.flashcardDao().getAllFlashcards()
            var result: List<Flashcard> = emptyList()
            entities.collect { entityList ->
                result = entityList.map { it.toFlashcard() }.take(20)
            }
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Creates a custom flashcard
     */
    suspend fun createCustomFlashcard(flashcard: Flashcard): Result<Flashcard> {
        return try {
            // Save locally first
            val entity = FlashcardEntity.fromFlashcard(flashcard, needsSync = true)
            database.flashcardDao().insertFlashcard(entity)
            
            Result.success(flashcard)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates a custom flashcard
     */
    suspend fun updateCustomFlashcard(flashcard: Flashcard): Result<Flashcard> {
        return try {
            // Update locally first
            val entity = FlashcardEntity.fromFlashcard(flashcard, needsSync = true)
            database.flashcardDao().updateFlashcard(entity)
            
            Result.success(flashcard)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes a custom flashcard
     */
    suspend fun deleteCustomFlashcard(flashcardId: String): Result<Unit> {
        return try {
            // Delete locally first
            database.flashcardDao().deleteFlashcardById(flashcardId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets custom flashcards for a user
     */
    suspend fun getCustomFlashcards(userId: String): Result<List<Flashcard>> {
        return try {
            val entities = database.flashcardDao().getCustomFlashcardsByUser(userId)
            val flashcards = entities.map { it.toFlashcard() }
            Result.success(flashcards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ===== USER OPERATIONS =====
    
    /**
     * Gets user data with offline support
     */
    suspend fun getUser(userId: String, forceRefresh: Boolean = false): Result<User> {
        return try {
            // Try local first
            val localUser = database.userDao().getUserById(userId)?.toUser()
            
            if (localUser != null && !forceRefresh) {
                return Result.success(localUser)
            }
            
            // Try network
            try {
                val response = apiService.getUser(userId)
                if (response.isSuccessful) {
                    val networkUser = response.body()?.data 
                        ?: throw Exception("User data is null")
                    
                    // Cache locally
                    val entity = UserEntity.fromUser(networkUser, needsSync = false)
                    database.userDao().insertUser(entity)
                    
                    Result.success(networkUser)
                } else {
                    localUser?.let { Result.success(it) } 
                        ?: Result.failure(Exception("User not found"))
                }
            } catch (e: Exception) {
                localUser?.let { Result.success(it) } ?: Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates user data
     */
    suspend fun updateUser(user: User): Result<User> {
        return try {
            // Update locally first
            val entity = UserEntity.fromUser(user, needsSync = true)
            database.userDao().updateUser(entity)
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates user progress
     */
    suspend fun updateUserProgress(userId: String, progress: LearningProgress): Result<LearningProgress> {
        return try {
            // Update locally first
            database.userDao().updateLearningProgress(
                id = userId,
                flashcardsLearned = progress.flashcardsLearned,
                quizzesCompleted = progress.quizzesCompleted,
                currentStreak = progress.currentStreak,
                totalStudyTimeMinutes = progress.totalStudyTimeMinutes,
                lastModified = Instant.now().epochSecond
            )
            
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ===== SPACED REPETITION OPERATIONS =====
    
    /**
     * Gets spaced repetition data for a user
     */
    suspend fun getSpacedRepetitionData(userId: String): Result<List<SpacedRepetitionData>> {
        return try {
            val entities = database.spacedRepetitionDao().getSpacedRepetitionDataByUser(userId)
            val data = entities.map { it.toSpacedRepetitionData() }
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates spaced repetition data
     */
    suspend fun updateSpacedRepetitionData(data: SpacedRepetitionData): Result<SpacedRepetitionData> {
        return try {
            // Update locally first
            val entity = SpacedRepetitionEntity.fromSpacedRepetitionData(data, needsSync = true)
            database.spacedRepetitionDao().insertSpacedRepetitionData(entity)
            
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ===== QUIZ OPERATIONS =====
    
    /**
     * Submits quiz result
     */
    suspend fun submitQuizResult(result: QuizResult): Result<QuizResult> {
        return try {
            // Save locally first
            val entity = QuizResultEntity.fromQuizResult(result, needsSync = true)
            database.quizResultDao().insertQuizResult(entity)
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets quiz results for a user
     */
    suspend fun getQuizResults(userId: String): Result<List<QuizResult>> {
        return try {
            val entities = database.quizResultDao().getQuizResultsByUser(userId)
            val results = entities.map { it.toQuizResult() }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ===== GAME OPERATIONS =====
    
    /**
     * Submits game result
     */
    suspend fun submitGameResult(result: GameResult): Result<GameResult> {
        return try {
            // Save locally first
            val entity = GameResultEntity.fromGameResult(result, needsSync = true)
            database.gameResultDao().insertGameResult(entity)
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets game results for a user
     */
    suspend fun getGameResults(userId: String): Result<List<GameResult>> {
        return try {
            val entities = database.gameResultDao().getGameResultsByUser(userId)
            val results = entities.map { it.toGameResult() }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ===== CHARACTER OPERATIONS =====
    
    /**
     * Gets characters by script with offline support
     */
    suspend fun getCharactersByScript(
        script: CharacterScript,
        forceRefresh: Boolean = false
    ): Result<List<Character>> {
        return try {
            // Try local first
            val localCharacters = database.characterDao()
                .getCharactersByScript(script)
                .map { it.toCharacter() }
            
            if (localCharacters.isNotEmpty() && !forceRefresh) {
                return Result.success(localCharacters)
            }
            
            // Try network
            try {
                val response = apiService.getCharactersByScript(script.name)
                if (response.isSuccessful) {
                    val networkCharacters = response.body()?.toCharacterModels() ?: emptyList()
                    
                    // Cache locally
                    val entities = networkCharacters.map { CharacterEntity.fromCharacter(it) }
                    database.characterDao().insertCharacters(entities)
                    
                    Result.success(networkCharacters)
                } else {
                    if (localCharacters.isNotEmpty()) {
                        Result.success(localCharacters)
                    } else {
                        Result.failure(Exception("No characters available offline"))
                    }
                }
            } catch (e: Exception) {
                if (localCharacters.isNotEmpty()) {
                    Result.success(localCharacters)
                } else {
                    Result.failure(e)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets character detail
     */
    suspend fun getCharacterDetail(characterId: String): Result<Character> {
        return try {
            // Try local first
            val localCharacter = database.characterDao().getCharacterById(characterId)?.toCharacter()
            
            if (localCharacter != null) {
                return Result.success(localCharacter)
            }
            
            // Try network
            val response = apiService.getCharacterDetail(characterId)
            if (response.isSuccessful) {
                val networkCharacter = response.body()?.toModel()!!
                
                // Cache locally
                val entity = CharacterEntity.fromCharacter(networkCharacter)
                database.characterDao().insertCharacter(entity)
                
                Result.success(networkCharacter)
            } else {
                Result.failure(Exception("Character not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ===== SYNC OPERATIONS =====
    
    /**
     * Forces immediate synchronization (no-op - sync not implemented)
     */
    suspend fun forceSyncNow(): Result<SyncResult> {
        return Result.success(SyncResult.Success)
    }
    
    /**
     * Gets sync status
     */
    fun getSyncStatus(): Flow<SyncState> {
        return _syncState
    }
    
    /**
     * Checks if device is in offline mode
     */
    fun isOfflineMode(): Boolean {
        return false
    }
    
    /**
     * Clears all local data (for logout)
     */
    suspend fun clearAllLocalData() {
        database.flashcardDao().clearAllFlashcards()
        database.userDao().clearAllUsers()
        database.spacedRepetitionDao().clearAllSpacedRepetitionData()
        database.quizResultDao().clearAllQuizResults()
        database.gameResultDao().clearAllGameResults()
        database.characterDao().clearAllCharacters()
        database.syncOperationDao().clearAllSyncOperations()
    }
    
    /**
     * Gets offline statistics
     */
    suspend fun getOfflineStatistics(): OfflineStatistics {
        return OfflineStatistics(
            cachedFlashcards = database.flashcardDao().getFlashcardCount(),
            cachedCharacters = database.characterDao().getCharacterCount(),
            pendingSyncOperations = database.syncOperationDao().getSyncOperationCount(),
            lastSyncTime = null
        )
    }
}

/**
 * Statistics about offline data
 */
data class OfflineStatistics(
    val cachedFlashcards: Int,
    val cachedCharacters: Int,
    val pendingSyncOperations: Int,
    val lastSyncTime: Instant?
)