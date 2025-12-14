package com.example.sakura_flashcard.data.local.dao

import androidx.room.*
import com.example.sakura_flashcard.data.local.entity.CustomDeckEntity
import com.example.sakura_flashcard.data.local.entity.CustomFlashcardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomDeckDao {
    
    // Deck operations
    @Query("SELECT * FROM custom_decks ORDER BY createdAt DESC")
    fun getAllDecks(): Flow<List<CustomDeckEntity>>
    
    @Query("SELECT * FROM custom_decks WHERE id = :deckId")
    suspend fun getDeckById(deckId: String): CustomDeckEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: CustomDeckEntity)
    
    @Delete
    suspend fun deleteDeck(deck: CustomDeckEntity)
    
    @Query("DELETE FROM custom_decks WHERE id = :deckId")
    suspend fun deleteDeckById(deckId: String)
    
    // Flashcard operations
    @Query("SELECT * FROM custom_flashcards WHERE deckId = :deckId ORDER BY createdAt DESC")
    fun getFlashcardsByDeckId(deckId: String): Flow<List<CustomFlashcardEntity>>
    
    @Query("SELECT * FROM custom_flashcards WHERE deckId = :deckId ORDER BY createdAt DESC")
    suspend fun getFlashcardsByDeckIdOnce(deckId: String): List<CustomFlashcardEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: CustomFlashcardEntity)
    
    @Delete
    suspend fun deleteFlashcard(flashcard: CustomFlashcardEntity)
    
    @Query("DELETE FROM custom_flashcards WHERE id = :flashcardId")
    suspend fun deleteFlashcardById(flashcardId: String)
    
    // Combined query - get deck with flashcard count
    @Query("""
        SELECT d.*, COUNT(f.id) as flashcardCount 
        FROM custom_decks d 
        LEFT JOIN custom_flashcards f ON d.id = f.deckId 
        GROUP BY d.id 
        ORDER BY d.createdAt DESC
    """)
    fun getDecksWithFlashcardCount(): Flow<List<DeckWithCount>>
}

data class DeckWithCount(
    val id: String,
    val name: String,
    val createdAt: java.time.Instant,
    val userId: String,
    val flashcardCount: Int
)
