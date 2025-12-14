package com.example.sakura_flashcard.data.local.dao

import androidx.room.*
import com.example.sakura_flashcard.data.local.entity.FlashcardEntity
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.data.model.VocabularyTopic
import kotlinx.coroutines.flow.Flow

/**
 * DAO for flashcard operations
 */
@Dao
interface FlashcardDao {
    
    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>
    
    @Query("SELECT * FROM flashcards WHERE id = :id")
    suspend fun getFlashcardById(id: String): FlashcardEntity?
    
    @Query("SELECT * FROM flashcards WHERE topic = :topic AND level = :level")
    suspend fun getFlashcardsByTopicAndLevel(
        topic: VocabularyTopic, 
        level: JLPTLevel
    ): List<FlashcardEntity>
    
    @Query("SELECT * FROM flashcards WHERE topic = :topic AND level = :level LIMIT :limit OFFSET :offset")
    suspend fun getFlashcardsByTopicAndLevelPaged(
        topic: VocabularyTopic, 
        level: JLPTLevel,
        limit: Int,
        offset: Int
    ): List<FlashcardEntity>
    
    @Query("SELECT * FROM flashcards WHERE isCustom = 1 AND createdBy = :userId")
    suspend fun getCustomFlashcardsByUser(userId: String): List<FlashcardEntity>
    
    @Query("SELECT * FROM flashcards WHERE needsSync = 1")
    suspend fun getFlashcardsNeedingSync(): List<FlashcardEntity>
    
    @Query("SELECT * FROM flashcards WHERE nextReview <= :currentTime")
    suspend fun getDueFlashcards(currentTime: Long): List<FlashcardEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<FlashcardEntity>)
    
    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)
    
    @Delete
    suspend fun deleteFlashcard(flashcard: FlashcardEntity)
    
    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteFlashcardById(id: String)
    
    @Query("UPDATE flashcards SET needsSync = 0 WHERE id = :id")
    suspend fun markAsSynced(id: String)
    
    @Query("UPDATE flashcards SET needsSync = 1 WHERE id = :id")
    suspend fun markAsNeedsSync(id: String)
    
    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun getFlashcardCount(): Int
    
    @Query("SELECT COUNT(*) FROM flashcards WHERE isCustom = 1 AND createdBy = :userId")
    suspend fun getCustomFlashcardCount(userId: String): Int
    
    @Query("DELETE FROM flashcards WHERE isCustom = 0")
    suspend fun clearNonCustomFlashcards()
    
    @Query("DELETE FROM flashcards")
    suspend fun clearAllFlashcards()
}