package com.example.sakura_flashcard.data.recommendation

import com.example.sakura_flashcard.data.analytics.LearningAnalytics
import com.example.sakura_flashcard.data.algorithm.LearningRecommendations
import com.example.sakura_flashcard.data.algorithm.SpacedRepetitionAlgorithm
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.data.repository.LearningStatistics
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

/**
 * Advanced recommendation engine that provides personalized content suggestions
 * based on user learning patterns, performance, and preferences.
 */
@Singleton
class RecommendationEngine @Inject constructor(
    private val spacedRepetitionAlgorithm: SpacedRepetitionAlgorithm,
    private val learningAnalytics: LearningAnalytics
) {
    
    /**
     * Generates personalized flashcard recommendations
     */
    fun generatePersonalizedRecommendations(
        userId: String,
        allFlashcards: List<Flashcard>,
        spacedRepetitionData: List<SpacedRepetitionData>,
        userProgress: LearningProgress,
        preferences: UserPreferences? = null
    ): PersonalizedRecommendations {
        
        // Analyze current performance
        val performanceAnalysis = learningAnalytics.analyzeLearningPerformance(spacedRepetitionData)
        val categoryAnalysis = learningAnalytics.analyzePerformanceByCategory(spacedRepetitionData, allFlashcards)
        
        // Generate different types of recommendations
        val reviewCards = generateReviewRecommendations(spacedRepetitionData, allFlashcards)
        val newCards = generateNewCardRecommendations(allFlashcards, spacedRepetitionData, userProgress, preferences)
        val challengeCards = generateChallengeRecommendations(allFlashcards, spacedRepetitionData, userProgress)
        val reinforcementCards = generateReinforcementRecommendations(spacedRepetitionData, allFlashcards, categoryAnalysis)
        
        // Generate study recommendations
        val studyRecommendations = learningAnalytics.generateStudyRecommendations(
            performanceAnalysis, categoryAnalysis, userProgress
        )
        
        return PersonalizedRecommendations(
            reviewCards = reviewCards,
            newCards = newCards,
            challengeCards = challengeCards,
            reinforcementCards = reinforcementCards,
            studyRecommendations = studyRecommendations,
            optimalSessionSize = spacedRepetitionAlgorithm.calculateOptimalSessionSize(userProgress, spacedRepetitionData),
            performanceInsights = generatePerformanceInsights(performanceAnalysis, categoryAnalysis)
        )
    }
    
    /**
     * Generates cards that are due for review
     */
    private fun generateReviewRecommendations(
        spacedRepetitionData: List<SpacedRepetitionData>,
        allFlashcards: List<Flashcard>
    ): List<RecommendedCard> {
        val flashcardMap = allFlashcards.associateBy { it.id }
        
        return spacedRepetitionData
            .filter { it.isDueForReview() }
            .sortedByDescending { it.getPriorityScore() }
            .take(20)
            .mapNotNull { srData ->
                flashcardMap[srData.flashcardId]?.let { flashcard ->
                    RecommendedCard(
                        flashcard = flashcard,
                        reason = "Due for review",
                        priority = calculatePriority(srData),
                        estimatedDifficulty = srData.difficultyAdjustment + 2.5f,
                        recommendationType = RecommendationType.REVIEW
                    )
                }
            }
    }
    
    /**
     * Generates new cards based on user progress and preferences
     */
    private fun generateNewCardRecommendations(
        allFlashcards: List<Flashcard>,
        spacedRepetitionData: List<SpacedRepetitionData>,
        userProgress: LearningProgress,
        preferences: UserPreferences?
    ): List<RecommendedCard> {
        val studiedCardIds = spacedRepetitionData.map { it.flashcardId }.toSet()
        val newFlashcards = allFlashcards.filter { it.id !in studiedCardIds }
        
        // Filter by user preferences if available
        val filteredCards = preferences?.let { prefs ->
            newFlashcards.filter { flashcard ->
                (prefs.preferredTopics.isEmpty() || flashcard.topic in prefs.preferredTopics) &&
                (prefs.preferredLevels.isEmpty() || flashcard.level in prefs.preferredLevels)
            }
        } ?: newFlashcards
        
        // Prioritize based on user's current level progress
        return filteredCards
            .map { flashcard ->
                val levelProgress = userProgress.levelProgress[flashcard.level] ?: 0f
                val topicPriority = calculateTopicPriority(flashcard.topic, userProgress)
                val levelPriority = calculateLevelPriority(flashcard.level, levelProgress)
                
                RecommendedCard(
                    flashcard = flashcard,
                    reason = "New content for ${flashcard.level.displayName}",
                    priority = (topicPriority + levelPriority) / 2f,
                    estimatedDifficulty = flashcard.difficulty,
                    recommendationType = RecommendationType.NEW_CONTENT
                )
            }
            .sortedByDescending { it.priority }
            .take(10)
    }
    
    /**
     * Generates challenging cards for advanced learners
     */
    private fun generateChallengeRecommendations(
        allFlashcards: List<Flashcard>,
        spacedRepetitionData: List<SpacedRepetitionData>,
        userProgress: LearningProgress
    ): List<RecommendedCard> {
        // Only recommend challenges if user has good performance
        val averageMastery = spacedRepetitionData.map { it.getMasteryLevel() }.average()
        if (averageMastery < 0.7) return emptyList()
        
        val studiedCardIds = spacedRepetitionData.map { it.flashcardId }.toSet()
        val challengeCards = allFlashcards
            .filter { it.id !in studiedCardIds }
            .filter { it.difficulty >= 3.0f } // High difficulty cards
            .filter { it.level.ordinal >= 2 } // N3 and above
            .take(5)
        
        return challengeCards.map { flashcard ->
            RecommendedCard(
                flashcard = flashcard,
                reason = "Challenge yourself with advanced content",
                priority = 0.8f,
                estimatedDifficulty = flashcard.difficulty,
                recommendationType = RecommendationType.CHALLENGE
            )
        }
    }
    
    /**
     * Generates cards for reinforcing weak areas
     */
    private fun generateReinforcementRecommendations(
        spacedRepetitionData: List<SpacedRepetitionData>,
        allFlashcards: List<Flashcard>,
        categoryAnalysis: com.example.sakura_flashcard.data.analytics.CategoryPerformanceAnalysis
    ): List<RecommendedCard> {
        val flashcardMap = allFlashcards.associateBy { it.id }
        
        // Find cards with low mastery that need reinforcement
        val reinforcementCards = spacedRepetitionData
            .filter { it.getMasteryLevel() < 0.5f && it.totalReviews >= 2 }
            .sortedBy { it.getMasteryLevel() }
            .take(8)
            .mapNotNull { srData ->
                flashcardMap[srData.flashcardId]?.let { flashcard ->
                    RecommendedCard(
                        flashcard = flashcard,
                        reason = "Reinforce weak area: ${flashcard.topic.displayName}",
                        priority = 1.0f - srData.getMasteryLevel(),
                        estimatedDifficulty = srData.difficultyAdjustment + 3.0f,
                        recommendationType = RecommendationType.REINFORCEMENT
                    )
                }
            }
        
        return reinforcementCards
    }
    
    /**
     * Calculates priority for a spaced repetition data entry
     */
    private fun calculatePriority(spacedRepetitionData: SpacedRepetitionData): Float {
        val urgencyScore = spacedRepetitionData.getPriorityScore()
        val difficultyScore = (3.0f - spacedRepetitionData.easeFactor).coerceAtLeast(0f)
        val masteryScore = 1.0f - spacedRepetitionData.getMasteryLevel()
        
        return (urgencyScore * 0.5f + difficultyScore * 0.3f + masteryScore * 0.2f).coerceIn(0f, 5f)
    }
    
    /**
     * Calculates topic priority based on user progress
     */
    private fun calculateTopicPriority(topic: VocabularyTopic, userProgress: LearningProgress): Float {
        // This could be enhanced with user preference data
        return when (topic) {
            VocabularyTopic.COMMON_EXPRESSIONS, VocabularyTopic.DAILY_LIFE -> 1.0f
            VocabularyTopic.NUMBERS, VocabularyTopic.COLORS -> 0.9f
            VocabularyTopic.FAMILY, VocabularyTopic.FOOD -> 0.8f
            VocabularyTopic.SCHOOL, VocabularyTopic.TRAVEL -> 0.7f
            VocabularyTopic.TECHNOLOGY, VocabularyTopic.ANIME -> 0.6f
            else -> 0.5f
        }
    }
    
    /**
     * Calculates level priority based on current progress
     */
    private fun calculateLevelPriority(level: JLPTLevel, currentProgress: Float): Float {
        return when {
            currentProgress < 0.3f -> when (level) {
                JLPTLevel.N5 -> 1.0f
                JLPTLevel.N4 -> 0.3f
                else -> 0.1f
            }
            currentProgress < 0.7f -> when (level) {
                JLPTLevel.N5 -> 0.8f
                JLPTLevel.N4 -> 1.0f
                JLPTLevel.N3 -> 0.4f
                else -> 0.1f
            }
            else -> when (level) {
                JLPTLevel.N5 -> 0.3f
                JLPTLevel.N4 -> 0.6f
                JLPTLevel.N3 -> 1.0f
                JLPTLevel.N2 -> 0.7f
                JLPTLevel.N1 -> 0.4f
            }
        }
    }
    
    /**
     * Generates performance insights for the user
     */
    private fun generatePerformanceInsights(
        performanceAnalysis: com.example.sakura_flashcard.data.analytics.LearningPerformanceAnalysis,
        categoryAnalysis: com.example.sakura_flashcard.data.analytics.CategoryPerformanceAnalysis
    ): List<PerformanceInsight> {
        val insights = mutableListOf<PerformanceInsight>()
        
        // Accuracy insights
        when {
            performanceAnalysis.accuracy >= 0.9f -> {
                insights.add(
                    PerformanceInsight(
                        title = "Excellent Accuracy!",
                        description = "You're getting ${(performanceAnalysis.accuracy * 100).toInt()}% of answers correct. Great job!",
                        type = InsightType.POSITIVE,
                        actionable = false
                    )
                )
            }
            performanceAnalysis.accuracy < 0.6f -> {
                insights.add(
                    PerformanceInsight(
                        title = "Focus on Accuracy",
                        description = "Your accuracy is ${(performanceAnalysis.accuracy * 100).toInt()}%. Consider reviewing cards more carefully.",
                        type = InsightType.IMPROVEMENT_NEEDED,
                        actionable = true
                    )
                )
            }
        }
        
        // Learning velocity insights
        if (performanceAnalysis.learningVelocity > 0.5f) {
            insights.add(
                PerformanceInsight(
                    title = "Great Learning Pace",
                    description = "You're mastering ${String.format("%.1f", performanceAnalysis.learningVelocity)} cards per day on average.",
                    type = InsightType.POSITIVE,
                    actionable = false
                )
            )
        }
        
        // Topic-specific insights
        val weakestTopic = categoryAnalysis.topicPerformance.minByOrNull { it.value.averageMastery }
        weakestTopic?.let { (topic, performance) ->
            if (performance.averageMastery < 0.4f) {
                insights.add(
                    PerformanceInsight(
                        title = "Challenging Topic: ${topic.displayName}",
                        description = "You might want to spend extra time on ${topic.displayName} vocabulary.",
                        type = InsightType.IMPROVEMENT_NEEDED,
                        actionable = true
                    )
                )
            }
        }
        
        return insights
    }
}

/**
 * Data classes for recommendation results
 */
data class PersonalizedRecommendations(
    val reviewCards: List<RecommendedCard>,
    val newCards: List<RecommendedCard>,
    val challengeCards: List<RecommendedCard>,
    val reinforcementCards: List<RecommendedCard>,
    val studyRecommendations: List<com.example.sakura_flashcard.data.analytics.StudyRecommendation>,
    val optimalSessionSize: Int,
    val performanceInsights: List<PerformanceInsight>
)

data class RecommendedCard(
    val flashcard: Flashcard,
    val reason: String,
    val priority: Float,
    val estimatedDifficulty: Float,
    val recommendationType: RecommendationType
)

data class UserPreferences(
    val preferredTopics: Set<VocabularyTopic> = emptySet(),
    val preferredLevels: Set<JLPTLevel> = emptySet(),
    val maxSessionSize: Int = 20,
    val preferredStudyTime: Int = 15, // minutes
    val difficultyPreference: DifficultyPreference = DifficultyPreference.BALANCED
)

data class PerformanceInsight(
    val title: String,
    val description: String,
    val type: InsightType,
    val actionable: Boolean
)

enum class RecommendationType {
    REVIEW,
    NEW_CONTENT,
    CHALLENGE,
    REINFORCEMENT
}

enum class DifficultyPreference {
    EASY,
    BALANCED,
    CHALLENGING
}

enum class InsightType {
    POSITIVE,
    NEUTRAL,
    IMPROVEMENT_NEEDED
}