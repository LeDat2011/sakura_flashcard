package com.example.sakura_flashcard.data.analytics

import com.example.sakura_flashcard.data.model.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for tracking and analyzing learning progress and performance.
 * Provides insights into user learning patterns and effectiveness.
 */
@Singleton
class LearningAnalytics @Inject constructor() {
    
    /**
     * Analyzes learning performance over time
     */
    fun analyzeLearningPerformance(
        spacedRepetitionData: List<SpacedRepetitionData>,
        timeRangedays: Int = 30
    ): LearningPerformanceAnalysis {
        val cutoffDate = Instant.now().minus(timeRangedays.toLong(), ChronoUnit.DAYS)
        val recentData = spacedRepetitionData.filter { 
            it.lastReviewed?.isAfter(cutoffDate) == true 
        }
        
        if (recentData.isEmpty()) {
            return LearningPerformanceAnalysis()
        }
        
        val totalReviews = recentData.sumOf { it.totalReviews }
        val correctReviews = recentData.sumOf { it.correctStreak }
        val accuracy = if (totalReviews > 0) correctReviews.toFloat() / totalReviews else 0f
        
        val averageEaseFactor = recentData.map { it.easeFactor }.average().toFloat()
        val averageMasteryLevel = recentData.map { it.getMasteryLevel() }.average().toFloat()
        val averageResponseTime = recentData.map { it.averageResponseTime }.average().toLong()
        
        // Calculate learning velocity (cards mastered per day)
        val masteredCards = recentData.count { it.getMasteryLevel() >= 0.8f }
        val learningVelocity = masteredCards.toFloat() / timeRangedays
        
        // Calculate retention rate
        val retentionRate = calculateRetentionRate(recentData)
        
        // Identify learning patterns
        val learningPatterns = identifyLearningPatterns(recentData)
        
        return LearningPerformanceAnalysis(
            totalReviews = totalReviews,
            accuracy = accuracy,
            averageEaseFactor = averageEaseFactor,
            averageMasteryLevel = averageMasteryLevel,
            averageResponseTime = averageResponseTime,
            learningVelocity = learningVelocity,
            retentionRate = retentionRate,
            learningPatterns = learningPatterns,
            timeRangeDays = timeRangedays
        )
    }
    
    /**
     * Calculates retention rate based on spaced repetition intervals
     */
    private fun calculateRetentionRate(spacedRepetitionData: List<SpacedRepetitionData>): Float {
        val cardsWithMultipleReviews = spacedRepetitionData.filter { it.totalReviews > 1 }
        if (cardsWithMultipleReviews.isEmpty()) return 0f
        
        val retainedCards = cardsWithMultipleReviews.count { it.correctStreak > 0 }
        return retainedCards.toFloat() / cardsWithMultipleReviews.size
    }
    
    /**
     * Identifies learning patterns and trends
     */
    private fun identifyLearningPatterns(spacedRepetitionData: List<SpacedRepetitionData>): List<LearningPattern> {
        val patterns = mutableListOf<LearningPattern>()
        
        // Pattern 1: Consistent improvement
        val improvingCards = spacedRepetitionData.filter { 
            it.easeFactor > 2.5f && it.correctStreak >= 3 
        }
        if (improvingCards.size > spacedRepetitionData.size * 0.3) {
            patterns.add(LearningPattern.CONSISTENT_IMPROVEMENT)
        }
        
        // Pattern 2: Struggling with retention
        val strugglingCards = spacedRepetitionData.filter { 
            it.easeFactor < 2.0f && it.totalReviews > 5 
        }
        if (strugglingCards.size > spacedRepetitionData.size * 0.2) {
            patterns.add(LearningPattern.RETENTION_ISSUES)
        }
        
        // Pattern 3: Fast learner
        val quickMasteryCards = spacedRepetitionData.filter { 
            it.getMasteryLevel() >= 0.8f && it.totalReviews <= 3 
        }
        if (quickMasteryCards.size > spacedRepetitionData.size * 0.4) {
            patterns.add(LearningPattern.FAST_LEARNER)
        }
        
        // Pattern 4: Needs more practice
        val needsPracticeCards = spacedRepetitionData.filter { 
            it.totalReviews > 10 && it.getMasteryLevel() < 0.5f 
        }
        if (needsPracticeCards.size > spacedRepetitionData.size * 0.15) {
            patterns.add(LearningPattern.NEEDS_MORE_PRACTICE)
        }
        
        return patterns
    }
    
    /**
     * Analyzes performance by topic and level
     */
    fun analyzePerformanceByCategory(
        spacedRepetitionData: List<SpacedRepetitionData>,
        allFlashcards: List<Flashcard>
    ): CategoryPerformanceAnalysis {
        val flashcardMap = allFlashcards.associateBy { it.id }
        
        // Group by topic
        val topicPerformance = spacedRepetitionData
            .mapNotNull { srData ->
                flashcardMap[srData.flashcardId]?.let { flashcard ->
                    flashcard.topic to srData
                }
            }
            .groupBy { it.first }
            .mapValues { (_, pairs) ->
                val data = pairs.map { it.second }
                calculateCategoryPerformance(data)
            }
        
        // Group by JLPT level
        val levelPerformance = spacedRepetitionData
            .mapNotNull { srData ->
                flashcardMap[srData.flashcardId]?.let { flashcard ->
                    flashcard.level to srData
                }
            }
            .groupBy { it.first }
            .mapValues { (_, pairs) ->
                val data = pairs.map { it.second }
                calculateCategoryPerformance(data)
            }
        
        return CategoryPerformanceAnalysis(
            topicPerformance = topicPerformance,
            levelPerformance = levelPerformance
        )
    }
    
    /**
     * Calculates performance metrics for a category
     */
    private fun calculateCategoryPerformance(data: List<SpacedRepetitionData>): CategoryPerformance {
        if (data.isEmpty()) {
            return CategoryPerformance()
        }
        
        val totalCards = data.size
        val masteredCards = data.count { it.getMasteryLevel() >= 0.8f }
        val averageMastery = data.map { it.getMasteryLevel() }.average().toFloat()
        val averageEaseFactor = data.map { it.easeFactor }.average().toFloat()
        val totalReviews = data.sumOf { it.totalReviews }
        val averageResponseTime = data.map { it.averageResponseTime }.average().toLong()
        
        return CategoryPerformance(
            totalCards = totalCards,
            masteredCards = masteredCards,
            averageMastery = averageMastery,
            averageEaseFactor = averageEaseFactor,
            totalReviews = totalReviews,
            averageResponseTime = averageResponseTime
        )
    }
    
    /**
     * Generates personalized study recommendations based on analytics
     */
    fun generateStudyRecommendations(
        performanceAnalysis: LearningPerformanceAnalysis,
        categoryAnalysis: CategoryPerformanceAnalysis,
        userProgress: LearningProgress
    ): List<StudyRecommendation> {
        val recommendations = mutableListOf<StudyRecommendation>()
        
        // Accuracy-based recommendations
        when {
            performanceAnalysis.accuracy < 0.6f -> {
                recommendations.add(
                    StudyRecommendation(
                        type = RecommendationType.REDUCE_SESSION_SIZE,
                        title = "Reduce Study Session Size",
                        description = "Your accuracy is ${(performanceAnalysis.accuracy * 100).toInt()}%. Try studying fewer cards per session to improve focus.",
                        priority = RecommendationPriority.HIGH
                    )
                )
            }
            performanceAnalysis.accuracy > 0.9f -> {
                recommendations.add(
                    StudyRecommendation(
                        type = RecommendationType.INCREASE_DIFFICULTY,
                        title = "Challenge Yourself",
                        description = "Great accuracy! Consider adding more advanced cards or increasing session size.",
                        priority = RecommendationPriority.MEDIUM
                    )
                )
            }
        }
        
        // Learning pattern recommendations
        performanceAnalysis.learningPatterns.forEach { pattern ->
            when (pattern) {
                LearningPattern.RETENTION_ISSUES -> {
                    recommendations.add(
                        StudyRecommendation(
                            type = RecommendationType.FOCUS_ON_REVIEW,
                            title = "Focus on Review",
                            description = "You're having trouble retaining information. Spend more time reviewing cards you've already seen.",
                            priority = RecommendationPriority.HIGH
                        )
                    )
                }
                LearningPattern.FAST_LEARNER -> {
                    recommendations.add(
                        StudyRecommendation(
                            type = RecommendationType.ADD_NEW_CONTENT,
                            title = "Add New Content",
                            description = "You're learning quickly! Consider adding more new cards to your study routine.",
                            priority = RecommendationPriority.MEDIUM
                        )
                    )
                }
                LearningPattern.NEEDS_MORE_PRACTICE -> {
                    recommendations.add(
                        StudyRecommendation(
                            type = RecommendationType.INCREASE_FREQUENCY,
                            title = "Study More Frequently",
                            description = "Some cards need more practice. Try shorter, more frequent study sessions.",
                            priority = RecommendationPriority.MEDIUM
                        )
                    )
                }
                LearningPattern.CONSISTENT_IMPROVEMENT -> {
                    recommendations.add(
                        StudyRecommendation(
                            type = RecommendationType.MAINTAIN_ROUTINE,
                            title = "Keep Up the Great Work!",
                            description = "You're showing consistent improvement. Maintain your current study routine.",
                            priority = RecommendationPriority.LOW
                        )
                    )
                }
            }
        }
        
        // Category-specific recommendations
        val weakestTopic = categoryAnalysis.topicPerformance
            .minByOrNull { it.value.averageMastery }
        
        weakestTopic?.let { (topic, performance) ->
            if (performance.averageMastery < 0.5f) {
                recommendations.add(
                    StudyRecommendation(
                        type = RecommendationType.FOCUS_ON_TOPIC,
                        title = "Focus on ${topic.displayName}",
                        description = "Your mastery in ${topic.displayName} is ${(performance.averageMastery * 100).toInt()}%. Consider dedicating more time to this topic.",
                        priority = RecommendationPriority.HIGH
                    )
                )
            }
        }
        
        return recommendations.sortedByDescending { it.priority.ordinal }
    }
    
    /**
     * Tracks daily study statistics
     */
    fun trackDailyStudy(
        userId: String,
        cardsStudied: Int,
        timeSpentMinutes: Long,
        accuracy: Float
    ): DailyStudyRecord {
        return DailyStudyRecord(
            userId = userId,
            date = LocalDate.now(),
            cardsStudied = cardsStudied,
            timeSpentMinutes = timeSpentMinutes,
            accuracy = accuracy,
            timestamp = Instant.now()
        )
    }
}

/**
 * Data classes for analytics results
 */
data class LearningPerformanceAnalysis(
    val totalReviews: Int = 0,
    val accuracy: Float = 0f,
    val averageEaseFactor: Float = 2.5f,
    val averageMasteryLevel: Float = 0f,
    val averageResponseTime: Long = 0L,
    val learningVelocity: Float = 0f, // cards mastered per day
    val retentionRate: Float = 0f,
    val learningPatterns: List<LearningPattern> = emptyList(),
    val timeRangeDays: Int = 30
)

data class CategoryPerformanceAnalysis(
    val topicPerformance: Map<VocabularyTopic, CategoryPerformance> = emptyMap(),
    val levelPerformance: Map<JLPTLevel, CategoryPerformance> = emptyMap()
)

data class CategoryPerformance(
    val totalCards: Int = 0,
    val masteredCards: Int = 0,
    val averageMastery: Float = 0f,
    val averageEaseFactor: Float = 2.5f,
    val totalReviews: Int = 0,
    val averageResponseTime: Long = 0L
)

data class StudyRecommendation(
    val type: RecommendationType,
    val title: String,
    val description: String,
    val priority: RecommendationPriority
)

data class DailyStudyRecord(
    val userId: String,
    val date: LocalDate,
    val cardsStudied: Int,
    val timeSpentMinutes: Long,
    val accuracy: Float,
    val timestamp: Instant
)

enum class LearningPattern {
    CONSISTENT_IMPROVEMENT,
    RETENTION_ISSUES,
    FAST_LEARNER,
    NEEDS_MORE_PRACTICE
}

enum class RecommendationType {
    REDUCE_SESSION_SIZE,
    INCREASE_DIFFICULTY,
    FOCUS_ON_REVIEW,
    ADD_NEW_CONTENT,
    INCREASE_FREQUENCY,
    MAINTAIN_ROUTINE,
    FOCUS_ON_TOPIC
}

enum class RecommendationPriority {
    LOW, MEDIUM, HIGH
}