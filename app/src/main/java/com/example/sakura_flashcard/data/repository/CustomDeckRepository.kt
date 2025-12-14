package com.example.sakura_flashcard.data.repository

import com.example.sakura_flashcard.data.local.dao.CustomDeckDao
import com.example.sakura_flashcard.data.local.dao.DeckWithCount
import com.example.sakura_flashcard.data.local.entity.CustomDeckEntity
import com.example.sakura_flashcard.data.local.entity.CustomFlashcardEntity
import com.example.sakura_flashcard.ui.screens.CustomDeck
import com.example.sakura_flashcard.ui.screens.CustomFlashcard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomDeckRepository @Inject constructor(
    private val customDeckDao: CustomDeckDao
) {
    
    fun getAllDecksWithFlashcards(): Flow<List<CustomDeck>> {
        return customDeckDao.getAllDecks().map { decks ->
            decks.map { deckEntity ->
                val flashcards = customDeckDao.getFlashcardsByDeckIdOnce(deckEntity.id)
                CustomDeck(
                    id = deckEntity.id,
                    name = deckEntity.name,
                    flashcards = flashcards.map { it.toCustomFlashcard() },
                    createdAt = deckEntity.createdAt
                )
            }
        }
    }
    
    suspend fun createDeck(name: String): CustomDeck {
        val deck = CustomDeckEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            createdAt = Instant.now()
        )
        customDeckDao.insertDeck(deck)
        return CustomDeck(
            id = deck.id,
            name = deck.name,
            flashcards = emptyList(),
            createdAt = deck.createdAt
        )
    }
    
    suspend fun deleteDeck(deckId: String) {
        customDeckDao.deleteDeckById(deckId)
    }
    
    suspend fun addFlashcardToDeck(
        deckId: String,
        japanese: String,
        romaji: String,
        vietnamese: String
    ): CustomFlashcard {
        val flashcard = CustomFlashcardEntity(
            id = UUID.randomUUID().toString(),
            deckId = deckId,
            japanese = japanese,
            romaji = romaji,
            vietnamese = vietnamese,
            createdAt = Instant.now()
        )
        customDeckDao.insertFlashcard(flashcard)
        return flashcard.toCustomFlashcard()
    }
    
    suspend fun removeFlashcard(flashcardId: String) {
        customDeckDao.deleteFlashcardById(flashcardId)
    }
    
    suspend fun getDeckById(deckId: String): CustomDeck? {
        val deckEntity = customDeckDao.getDeckById(deckId) ?: return null
        val flashcards = customDeckDao.getFlashcardsByDeckIdOnce(deckId)
        return CustomDeck(
            id = deckEntity.id,
            name = deckEntity.name,
            flashcards = flashcards.map { it.toCustomFlashcard() },
            createdAt = deckEntity.createdAt
        )
    }
    
    private fun CustomFlashcardEntity.toCustomFlashcard(): CustomFlashcard {
        return CustomFlashcard(
            id = id,
            japanese = japanese,
            romaji = romaji,
            vietnamese = vietnamese,
            createdAt = createdAt
        )
    }
}
