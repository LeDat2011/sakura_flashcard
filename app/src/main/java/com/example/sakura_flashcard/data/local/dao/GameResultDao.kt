package com.example.sakura_flashcard.data.local.dao

import androidx.room.*
import com.example.sakura_flashcard.data.local.entity.GameResultEntity
import com.example.sakura_flashcard.data.model.GameType
import kotlinx.coroutines.flow.Flow

/**
 * DAO for game result operations
 */
@Dao
interface GameResultDao {
    
    @Query("SELECT * FROM game_results WHERE userId = :userId ORDER BY completedAt DESC")
    suspend fun getGameResultsByUser(userId: String): List<GameResultEntity>
    
    @Query("SELECT * FROM game_results WHERE userId = :userId ORDER BY completedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getGameResultsByUserPaged(userId: String, limit: Int, offset: Int): List<GameResultEntity>
    
    @Query("SELECT * FROM game_results WHERE userId = :userId ORDER BY completedAt DESC")
    fun getGameResultsByUserFlow(userId: String): Flow<List<GameResultEntity>>
    
    @Query("SELECT * FROM game_results WHERE id = :id")
    suspend fun getGameResultById(id: String): GameResultEntity?
    
    @Query("SELECT * FROM game_results WHERE userId = :userId AND gameType = :gameType ORDER BY completedAt DESC")
    suspend fun getGameResultsByUserAndType(userId: String, gameType: GameType): List<GameResultEntity>
    
    @Query("SELECT * FROM game_results WHERE needsSync = 1")
    suspend fun getGameResultsNeedingSync(): List<GameResultEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameResult(result: GameResultEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameResults(results: List<GameResultEntity>)
    
    @Update
    suspend fun updateGameResult(result: GameResultEntity)
    
    @Delete
    suspend fun deleteGameResult(result: GameResultEntity)
    
    @Query("DELETE FROM game_results WHERE id = :id")
    suspend fun deleteGameResultById(id: String)
    
    @Query("DELETE FROM game_results WHERE userId = :userId")
    suspend fun deleteGameResultsByUser(userId: String)
    
    @Query("UPDATE game_results SET needsSync = 0 WHERE id = :id")
    suspend fun markAsSynced(id: String)
    
    @Query("UPDATE game_results SET needsSync = 1 WHERE id = :id")
    suspend fun markAsNeedsSync(id: String)
    
    @Query("SELECT COUNT(*) FROM game_results WHERE userId = :userId")
    suspend fun getGameResultCount(userId: String): Int
    
    @Query("SELECT AVG(score) FROM game_results WHERE userId = :userId AND gameType = :gameType")
    suspend fun getAverageScoreByType(userId: String, gameType: GameType): Float?
    
    @Query("SELECT MAX(score) FROM game_results WHERE userId = :userId AND gameType = :gameType")
    suspend fun getBestScoreByType(userId: String, gameType: GameType): Int?
    
    @Query("DELETE FROM game_results")
    suspend fun clearAllGameResults()
}