package com.example.sakura_flashcard.data.local.dao

import androidx.room.*
import com.example.sakura_flashcard.data.local.entity.SpacedRepetitionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for spaced repetition operations
 */
@Dao
interface SpacedRepetitionDao {
    
    @Query("SELECT * FROM spaced_repetition WHERE userId = :userId")
    suspend fun getSpacedRepetitionDataByUser(userId: String): List<SpacedRepetitionEntity>
    
    @Query("SELECT * FROM spaced_repetition WHERE userId = :userId")
    fun getSpacedRepetitionDataByUserFlow(userId: String): Flow<List<SpacedRepetitionEntity>>
    
    @Query("SELECT * FROM spaced_repetition WHERE id = :id")
    suspend fun getSpacedRepetitionDataById(id: String): SpacedRepetitionEntity?
    
    @Query("SELECT * FROM spaced_repetition WHERE userId = :userId AND flashcardId = :flashcardId")
    suspend fun getSpacedRepetitionData(userId: String, flashcardId: String): SpacedRepetitionEntity?
    
    @Query("SELECT * FROM spaced_repetition WHERE needsSync = 1")
    suspend fun getSpacedRepetitionDataNeedingSync(): List<SpacedRepetitionEntity>
    
    @Query("SELECT * FROM spaced_repetition WHERE userId = :userId AND nextReview <= :currentTime")
    suspend fun getDueSpacedRepetitionData(userId: String, currentTime: Long): List<SpacedRepetitionEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpacedRepetitionData(data: SpacedRepetitionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpacedRepetitionDataList(dataList: List<SpacedRepetitionEntity>)
    
    @Update
    suspend fun updateSpacedRepetitionData(data: SpacedRepetitionEntity)
    
    @Delete
    suspend fun deleteSpacedRepetitionData(data: SpacedRepetitionEntity)
    
    @Query("DELETE FROM spaced_repetition WHERE id = :id")
    suspend fun deleteSpacedRepetitionDataById(id: String)
    
    @Query("DELETE FROM spaced_repetition WHERE userId = :userId")
    suspend fun deleteSpacedRepetitionDataByUser(userId: String)
    
    @Query("UPDATE spaced_repetition SET needsSync = 0 WHERE id = :id")
    suspend fun markAsSynced(id: String)
    
    @Query("UPDATE spaced_repetition SET needsSync = 1 WHERE id = :id")
    suspend fun markAsNeedsSync(id: String)
    
    @Query("SELECT COUNT(*) FROM spaced_repetition WHERE userId = :userId")
    suspend fun getSpacedRepetitionCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM spaced_repetition WHERE userId = :userId AND correctStreak >= 3")
    suspend fun getLearnedCardsCount(userId: String): Int
    
    @Query("DELETE FROM spaced_repetition")
    suspend fun clearAllSpacedRepetitionData()
}