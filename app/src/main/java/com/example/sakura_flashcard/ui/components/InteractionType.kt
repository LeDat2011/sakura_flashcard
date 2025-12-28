package com.example.sakura_flashcard.ui.components

/**
 * Enum defining types of interactions a user can have with a flashcard
 */
enum class InteractionType {
    /** User marked the card as learned/remembered */
    LEARNED,
    
    /** User marked the card as not learned/needs review */
    NOT_LEARNED,
    
    /** User requested to play audio pronunciation */
    PLAY_AUDIO,
    
    /** User flipped the card to see the other side */
    FLIP,
    
    /** User skipped the card without learning it */
    SKIP
}
