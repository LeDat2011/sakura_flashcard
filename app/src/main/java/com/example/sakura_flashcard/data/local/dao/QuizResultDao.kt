package com.example.sakura_flashcard.data.local.dao

import androidx.room.*
import com.example.sakura_flashcard.data.local.entity.QuizResultEntity
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.data.model.VocabularyTopic
import kotlinx.coroutines.flow.Flow

/**
 * DAO for quiz result operations
 */
@Dao
interface QuizResultDao {
    
    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY completedAt DESC")
    suspend fun getQuizResultsByUser(userId: String): List<QuizResultEntity>
    
    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY completedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getQuizResultsByUserPaged(userId: String, limit: Int, offset: Int): List<QuizResultEntity>
    
    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY completedAt DESC")
    fun getQuizResultsByUserFlow(userId: String): Flow<List<QuizResultEntity>>
    
    @Query("SELECT * FROM quiz_results WHERE id = :id")
    suspend fun getQuizResultById(id: String): QuizResultEntity?
    
    @Query("SELECT * FROM quiz_results WHERE userId = :userId AND topic = :topic ORDER BY completedAt DESC")
    suspend fun getQuizResultsByUserAndTopic(userId: String, topic: VocabularyTopic): List<QuizResultEntity>
    
    @Query("SELECT * FROM quiz_results WHERE userId = :userId AND level = :level ORDER BY completedAt DESC")
    suspend fun getQuizResultsByUserAndLevel(userId: String, level: JLPTLevel): List<QuizResultEntity>
    
    @Query("SELECT * FROM quiz_results WHERE needsSync = 1")
    suspend fun getQuizResultsNeedingSync(): List<QuizResultEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResultEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResults(results: List<QuizResultEntity>)
    
    @Update
    suspend fun updateQuizResult(result: QuizResultEntity)
    
    @Delete
    suspend fun deleteQuizResult(result: QuizResultEntity)
    
    @Query("DELETE FROM quiz_results WHERE id = :id")
    suspend fun deleteQuizResultById(id: String)
    
    @Query("DELETE FROM quiz_results WHERE userId = :userId")
    suspend fun deleteQuizResultsByUser(userId: String)
    
    @Query("UPDATE quiz_results SET needsSync = 0 WHERE id = :id")
    suspend fun markAsSynced(id: String)
    
    @Query("UPDATE quiz_results SET needsSync = 1 WHERE id = :id")
    suspend fun markAsNeedsSync(id: String)
    
    @Query("SELECT COUNT(*) FROM quiz_results WHERE userId = :userId")
    suspend fun getQuizResultCount(userId: String): Int
    
    @Query("SELECT AVG(score) FROM quiz_results WHERE userId = :userId")
    suspend fun getAverageScore(userId: String): Float?
    
    @Query("SELECT MAX(score) FROM quiz_results WHERE userId = :userId")
    suspend fun getBestScore(userId: String): Int?
    
    @Query("DELETE FROM quiz_results")
    suspend fun clearAllQuizResults()
}