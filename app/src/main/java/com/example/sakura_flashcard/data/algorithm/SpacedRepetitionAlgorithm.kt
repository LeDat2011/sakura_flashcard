package com.example.sakura_flashcard.data.algorithm

import com.example.sakura_flashcard.data.model.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*
import kotlin.random.Random

/**
 * Implementation of spaced repetition algorithm for flashcard scheduling.
 * Based on SuperMemo SM-2 algorithm with enhancements for mobile learning.
 */
@Singleton
class SpacedRepetitionAlgorithm @Inject constructor() {
    
    companion object {
        private const val DEFAULT_EASE_FACTOR = 2.5f
        private const val MIN_EASE_FACTOR = 1.3f
        private const val MAX_EASE_FACTOR = 4.0f
        private const val INITIAL_INTERVAL = 1
        private const val SECOND_INTERVAL = 6
        private const val MAX_DAILY_NEW_CARDS = 20
        private const val MAX_DAILY_REVIEW_CARDS = 100
    }
    
    /**
     * Generates recommended flashcards for a user based on spaced repetition algorithm
     */
    fun generateRecommendedFlashcards(
        allFlashcards: List<Flashcard>,
        spacedRepetitionData: List<SpacedRepetitionData>,
        userProgress: LearningProgress,
        maxCards: Int = 20
    ): List<Flashcard> {
        val now = Instant.now()
        val dataMap = spacedRepetitionData.associateBy { it.flashcardId }
        
        // Separate cards into categories
        val dueCards = mutableListOf<Pair<Flashcard, Float>>()
        val newCards = mutableListOf<Pair<Flashcard, Float>>()
        
        allFlashcards.forEach { flashcard ->
            val srData = dataMap[flashcard.id]
            
            if (srData == null) {
                // New card - never seen before
                val priority = calculateNewCardPriority(flashcard, userProgress)
                newCards.add(flashcard to priority)
            } else if (srData.isDueForReview()) {
                // Due for review
                val priority = srData.getPriorityScore()
                dueCards.add(flashcard to priority)
            }
        }
        
        // Sort by priority (highest first)
        dueCards.sortByDescending { it.second }
        newCards.sortByDescending { it.second }
        
        // Combine cards with preference for due cards
        val result = mutableListOf<Flashcard>()
        
        // Add due cards first (up to 70% of max cards)
        val maxDueCards = (maxCards * 0.7).toInt()
        result.addAll(dueCards.take(maxDueCards).map { it.first })
        
        // Fill remaining slots with new cards
        val remainingSlots = maxCards - result.size
        if (remainingSlots > 0) {
            val maxNewCards = min(remainingSlots, MAX_DAILY_NEW_CARDS)
            result.addAll(newCards.take(maxNewCards).map { it.first })
        }
        
        // Shuffle to avoid predictable ordering while maintaining priority
        return result.shuffleWithPriority()
    }
    
    /**
     * Calculates priority for new cards based on user progress and card characteristics
     */
    private fun calculateNewCardPriority(flashcard: Flashcard, userProgress: LearningProgress): Float {
        var priority = 1.0f
        
        // Prioritize based on JLPT level and user progress
        val levelProgress = userProgress.levelProgress[flashcard.level] ?: 0f
        priority += (1f - levelProgress) * 2f // Higher priority for less progressed levels
        
        // Prioritize easier levels for beginners
        when (flashcard.level) {
            JLPTLevel.N5 -> priority += 2f
            JLPTLevel.N4 -> priority += 1.5f
            JLPTLevel.N3 -> priority += 1f
            JLPTLevel.N2 -> priority += 0.5f
            JLPTLevel.N1 -> priority += 0f
        }
        
        // Adjust based on flashcard difficulty
        priority += (3f - flashcard.difficulty) * 0.5f
        
        // Add some randomness to avoid always showing the same cards
        priority += Random.nextFloat() * 0.5f
        
        return priority
    }
    
    /**
     * Updates spaced repetition data after a review session
     */
    fun updateAfterReview(
        flashcardId: String,
        userId: String,
        quality: ReviewQuality,
        responseTime: Long = 0L,
        existingData: SpacedRepetitionData? = null
    ): SpacedRepetitionData {
        return if (existingData != null) {
            existingData.updateAfterReview(quality.value, responseTime)
        } else {
            // Create new spaced repetition data for first-time review
            createInitialSpacedRepetitionData(userId, flashcardId, quality, responseTime)
        }
    }
    
    /**
     * Creates initial spaced repetition data for a new flashcard
     */
    private fun createInitialSpacedRepetitionData(
        userId: String,
        flashcardId: String,
        quality: ReviewQuality,
        responseTime: Long
    ): SpacedRepetitionData {
        val now = Instant.now()
        val isCorrect = quality.isCorrect()
        
        val initialInterval = if (isCorrect) INITIAL_INTERVAL else 1
        val nextReview = now.plus(initialInterval.toLong(), ChronoUnit.DAYS)
        
        return SpacedRepetitionData(
            userId = userId,
            flashcardId = flashcardId,
            repetitionCount = if (isCorrect) 1 else 0,
            easeFactor = DEFAULT_EASE_FACTOR,
            interval = initialInterval,
            nextReview = nextReview,
            lastReviewed = now,
            correctStreak = if (isCorrect) 1 else 0,
            totalReviews = 1,
            averageResponseTime = responseTime,
            difficultyAdjustment = 0f
        ).updateAfterReview(quality.value, responseTime)
    }
    
    /**
     * Calculates optimal study session size based on user performance
     */
    fun calculateOptimalSessionSize(
        userProgress: LearningProgress,
        recentPerformance: List<SpacedRepetitionData>
    ): Int {
        val baseSize = 10
        
        // Adjust based on current streak
        val streakMultiplier = when {
            userProgress.currentStreak >= 7 -> 1.5f
            userProgress.currentStreak >= 3 -> 1.2f
            userProgress.currentStreak == 0 -> 0.8f
            else -> 1.0f
        }
        
        // Adjust based on recent performance
        val recentAccuracy = if (recentPerformance.isNotEmpty()) {
            recentPerformance.map { it.correctStreak.toFloat() / max(1, it.totalReviews) }
                .average().toFloat()
        } else {
            0.7f // Default assumption
        }
        
        val accuracyMultiplier = when {
            recentAccuracy >= 0.9f -> 1.3f
            recentAccuracy >= 0.7f -> 1.0f
            recentAccuracy >= 0.5f -> 0.8f
            else -> 0.6f
        }
        
        return (baseSize * streakMultiplier * accuracyMultiplier).toInt().coerceIn(5, 25)
    }
    
    /**
     * Generates personalized learning recommendations
     */
    fun generateLearningRecommendations(
        userProgress: LearningProgress,
        spacedRepetitionData: List<SpacedRepetitionData>,
        allFlashcards: List<Flashcard>
    ): LearningRecommendations {
        val dataMap = spacedRepetitionData.associateBy { it.flashcardId }
        
        // Analyze weak areas
        val weakTopics = analyzeWeakTopics(spacedRepetitionData, allFlashcards)
        val weakLevels = analyzeWeakLevels(spacedRepetitionData, allFlashcards)
        
        // Calculate study statistics
        val dueCount = spacedRepetitionData.count { it.isDueForReview() }
        val masteredCount = spacedRepetitionData.count { it.getMasteryLevel() >= 0.8f }
        val strugglingCount = spacedRepetitionData.count { it.getMasteryLevel() < 0.3f }
        
        // Generate recommendations
        val recommendations = mutableListOf<String>()
        
        if (dueCount > 20) {
            recommendations.add("You have $dueCount cards due for review. Consider focusing on reviews before learning new cards.")
        }
        
        if (strugglingCount > 5) {
            recommendations.add("You're struggling with $strugglingCount cards. Try breaking study sessions into smaller chunks.")
        }
        
        if (weakTopics.isNotEmpty()) {
            recommendations.add("Focus on these topics: ${weakTopics.take(3).joinToString(", ")}")
        }
        
        if (userProgress.currentStreak == 0) {
            recommendations.add("Start a new study streak! Even 5 minutes daily can make a big difference.")
        } else if (userProgress.currentStreak >= 7) {
            recommendations.add("Great streak! You're on day ${userProgress.currentStreak}. Keep it up!")
        }
        
        return LearningRecommendations(
            dueCardsCount = dueCount,
            newCardsRecommended = calculateOptimalNewCards(userProgress, dueCount),
            masteredCardsCount = masteredCount,
            strugglingCardsCount = strugglingCount,
            weakTopics = weakTopics.take(3),
            weakLevels = weakLevels.take(2),
            recommendations = recommendations,
            optimalSessionSize = calculateOptimalSessionSize(userProgress, spacedRepetitionData)
        )
    }
    
    private fun analyzeWeakTopics(
        spacedRepetitionData: List<SpacedRepetitionData>,
        allFlashcards: List<Flashcard>
    ): List<VocabularyTopic> {
        val flashcardMap = allFlashcards.associateBy { it.id }
        
        return spacedRepetitionData
            .mapNotNull { srData ->
                flashcardMap[srData.flashcardId]?.let { flashcard ->
                    flashcard.topic to srData.getMasteryLevel()
                }
            }
            .groupBy { it.first }
            .mapValues { (_, values) -> values.map { it.second }.average() }
            .toList()
            .sortedBy { it.second }
            .take(5)
            .map { it.first }
    }
    
    private fun analyzeWeakLevels(
        spacedRepetitionData: List<SpacedRepetitionData>,
        allFlashcards: List<Flashcard>
    ): List<JLPTLevel> {
        val flashcardMap = allFlashcards.associateBy { it.id }
        
        return spacedRepetitionData
            .mapNotNull { srData ->
                flashcardMap[srData.flashcardId]?.let { flashcard ->
                    flashcard.level to srData.getMasteryLevel()
                }
            }
            .groupBy { it.first }
            .mapValues { (_, values) -> values.map { it.second }.average() }
            .toList()
            .sortedBy { it.second }
            .take(3)
            .map { it.first }
    }
    
    private fun calculateOptimalNewCards(userProgress: LearningProgress, dueCount: Int): Int {
        return when {
            dueCount > 30 -> 0 // Focus on reviews first
            dueCount > 15 -> 3
            dueCount > 5 -> 5
            userProgress.currentStreak >= 7 -> 10
            else -> 7
        }
    }
    
    /**
     * Shuffles a list while maintaining relative priority order
     */
    private fun List<Flashcard>.shuffleWithPriority(): List<Flashcard> {
        return this.chunked(3).flatMap { chunk ->
            chunk.shuffled()
        }
    }
}

/**
 * Data class containing learning recommendations for the user
 */
data class LearningRecommendations(
    val dueCardsCount: Int,
    val newCardsRecommended: Int,
    val masteredCardsCount: Int,
    val strugglingCardsCount: Int,
    val weakTopics: List<VocabularyTopic>,
    val weakLevels: List<JLPTLevel>,
    val recommendations: List<String>,
    val optimalSessionSize: Int
)