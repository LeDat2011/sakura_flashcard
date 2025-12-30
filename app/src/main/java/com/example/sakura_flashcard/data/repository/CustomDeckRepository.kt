package com.example.sakura_flashcard.data.repository

import android.util.Log
import com.example.sakura_flashcard.data.local.dao.CustomDeckDao
import com.example.sakura_flashcard.data.local.dao.DeckWithCount
import com.example.sakura_flashcard.data.local.entity.CustomDeckEntity
import com.example.sakura_flashcard.data.local.entity.CustomFlashcardEntity
import com.example.sakura_flashcard.ui.screens.CustomDeck
import com.example.sakura_flashcard.ui.screens.CustomFlashcard
import com.example.sakura_flashcard.util.CryptoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing custom flashcard decks.
 * 
 * Security Features:
 * - All vocabulary content (japanese, romaji, vietnamese) is encrypted using AES-256-GCM
 * - Encryption keys are stored securely in Android Keystore
 * - Data is encrypted before saving to database and decrypted when reading
 * 
 * This provides an additional layer of security on top of SQLCipher database encryption.
 */
@Singleton
class CustomDeckRepository @Inject constructor(
    private val customDeckDao: CustomDeckDao
) {
    
    companion object {
        private const val TAG = "CustomDeckRepository"
    }
    
    fun getAllDecksWithFlashcards(): Flow<List<CustomDeck>> {
        return customDeckDao.getAllDecks().map { decks ->
            decks.map { deckEntity ->
                val flashcards = customDeckDao.getFlashcardsByDeckIdOnce(deckEntity.id)
                CustomDeck(
                    id = deckEntity.id,
                    name = deckEntity.name,
                    flashcards = flashcards.map { it.toDecryptedFlashcard() },
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
    
    /**
     * Add a new flashcard to a deck.
     * 
     * The vocabulary content (japanese, romaji, vietnamese) is encrypted before storing.
     * 
     * @param deckId The ID of the deck to add the flashcard to
     * @param japanese The Japanese word/phrase (will be encrypted)
     * @param romaji The romaji pronunciation (will be encrypted)
     * @param vietnamese The Vietnamese meaning (will be encrypted)
     * @return The created flashcard with decrypted content for immediate display
     */
    suspend fun addFlashcardToDeck(
        deckId: String,
        japanese: String,
        romaji: String,
        vietnamese: String
    ): CustomFlashcard {
        // Encrypt vocabulary content before storing
        val encryptedJapanese = CryptoUtils.encrypt(japanese)
        val encryptedRomaji = CryptoUtils.encrypt(romaji)
        val encryptedVietnamese = CryptoUtils.encrypt(vietnamese)
        
        Log.d(TAG, "Encrypting flashcard - Japanese length: ${japanese.length} -> ${encryptedJapanese.length}")
        
        val flashcard = CustomFlashcardEntity(
            id = UUID.randomUUID().toString(),
            deckId = deckId,
            japanese = encryptedJapanese,
            romaji = encryptedRomaji,
            vietnamese = encryptedVietnamese,
            createdAt = Instant.now()
        )
        customDeckDao.insertFlashcard(flashcard)
        
        // Return with original (decrypted) content for immediate display
        return CustomFlashcard(
            id = flashcard.id,
            japanese = japanese,
            romaji = romaji,
            vietnamese = vietnamese,
            createdAt = flashcard.createdAt
        )
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
            flashcards = flashcards.map { it.toDecryptedFlashcard() },
            createdAt = deckEntity.createdAt
        )
    }
    
    /**
     * Convert database entity to UI model with decryption.
     * 
     * Decrypts japanese, romaji, and vietnamese fields.
     * Falls back to original value if decryption fails (for backward compatibility
     * with unencrypted data from before this update).
     */
    private fun CustomFlashcardEntity.toDecryptedFlashcard(): CustomFlashcard {
        return CustomFlashcard(
            id = id,
            japanese = safeDecrypt(japanese),
            romaji = safeDecrypt(romaji),
            vietnamese = safeDecrypt(vietnamese),
            createdAt = createdAt
        )
    }
    
    /**
     * Safely decrypt a string value.
     * 
     * Returns the original value if decryption fails.
     * This provides backward compatibility with unencrypted data
     * that was stored before the encryption update.
     * 
     * @param encryptedValue The potentially encrypted value
     * @return The decrypted value, or original if decryption fails
     */
    private fun safeDecrypt(encryptedValue: String): String {
        return try {
            CryptoUtils.decrypt(encryptedValue)
        } catch (e: Exception) {
            // Decryption failed - likely unencrypted old data
            // Return original value for backward compatibility
            Log.d(TAG, "Decryption failed, returning original value (old unencrypted data)")
            encryptedValue
        }
    }
}
