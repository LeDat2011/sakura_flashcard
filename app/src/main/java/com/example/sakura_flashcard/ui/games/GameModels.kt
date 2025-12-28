package com.example.sakura_flashcard.ui.games

import com.example.sakura_flashcard.data.model.JLPTLevel
import java.util.UUID

/**
 * Represents a card in the Memory Match game
 */
data class MemoryCard(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val matchId: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

/**
 * Represents a puzzle where words need to be ordered to form a sentence
 */
data class SentencePuzzle(
    val id: String = UUID.randomUUID().toString(),
    val words: List<String>,
    val correctOrder: List<String>,
    val translation: String = "",
    val level: JLPTLevel = JLPTLevel.N5
)

/**
 * Represents a quick answer question with multiple choice options
 */
data class QuickAnswerQuestion(
    val id: String = UUID.randomUUID().toString(),
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val level: JLPTLevel = JLPTLevel.N5
)

/**
 * Represents a word pair for memory matching game
 */
data class WordPair(
    val id: String = UUID.randomUUID().toString(),
    val japanese: String,
    val english: String,
    val level: JLPTLevel = JLPTLevel.N5
)
