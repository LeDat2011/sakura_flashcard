package com.example.sakura_flashcard.data.sync

import com.example.sakura_flashcard.data.model.*
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for resolving conflicts between local and remote data
 */
@Singleton
class ConflictResolutionService @Inject constructor() {
    
    /**
     * Resolves conflicts for user data
     */
    fun resolveUserConflict(local: User, remote: User): UserConflictResolution {
        // Use last modified time as primary resolution strategy
        val localModified = local.lastLogin ?: local.createdAt
        val remoteModified = remote.lastLogin ?: remote.createdAt
        
        return when {
            localModified.isAfter(remoteModified) -> {
                UserConflictResolution(
                    resolvedUser = local,
                    strategy = ConflictResolutionStrategy.LOCAL_WINS,
                    reason = "Local data is more recent"
                )
            }
            remoteModified.isAfter(localModified) -> {
                UserConflictResolution(
                    resolvedUser = remote,
                    strategy = ConflictResolutionStrategy.REMOTE_WINS,
                    reason = "Remote data is more recent"
                )
            }
            else -> {
                // Same timestamp, merge the data intelligently
                val mergedUser = mergeUserData(local, remote)
                UserConflictResolution(
                    resolvedUser = mergedUser,
                    strategy = ConflictResolutionStrategy.MERGE,
                    reason = "Data merged from both sources"
                )
            }
        }
    }
    
    /**
     * Resolves conflicts for learning progress
     */
    fun resolveLearningProgressConflict(
        local: LearningProgress, 
        remote: LearningProgress
    ): LearningProgressConflictResolution {
        // For learning progress, we generally want to take the maximum values
        // since progress should only move forward
        val mergedProgress = LearningProgress(
            flashcardsLearned = maxOf(local.flashcardsLearned, remote.flashcardsLearned),
            quizzesCompleted = maxOf(local.quizzesCompleted, remote.quizzesCompleted),
            currentStreak = maxOf(local.currentStreak, remote.currentStreak),
            totalStudyTimeMinutes = maxOf(local.totalStudyTimeMinutes, remote.totalStudyTimeMinutes),
            levelProgress = mergeLevelProgress(local.levelProgress, remote.levelProgress)
        )
        
        return LearningProgressConflictResolution(
            resolvedProgress = mergedProgress,
            strategy = ConflictResolutionStrategy.MERGE,
            reason = "Progress data merged taking maximum values"
        )
    }
    
    /**
     * Resolves conflicts for spaced repetition data
     */
    fun resolveSpacedRepetitionConflict(
        local: SpacedRepetitionData,
        remote: SpacedRepetitionData
    ): SpacedRepetitionConflictResolution {
        // For spaced repetition, use the data with the most recent review
        val localLastReviewed = local.lastReviewed ?: Instant.MIN
        val remoteLastReviewed = remote.lastReviewed ?: Instant.MIN
        
        return when {
            localLastReviewed.isAfter(remoteLastReviewed) -> {
                SpacedRepetitionConflictResolution(
                    resolvedData = local,
                    strategy = ConflictResolutionStrategy.LOCAL_WINS,
                    reason = "Local data has more recent review"
                )
            }
            remoteLastReviewed.isAfter(localLastReviewed) -> {
                SpacedRepetitionConflictResolution(
                    resolvedData = remote,
                    strategy = ConflictResolutionStrategy.REMOTE_WINS,
                    reason = "Remote data has more recent review"
                )
            }
            else -> {
                // Same review time, merge intelligently
                val mergedData = mergeSpacedRepetitionData(local, remote)
                SpacedRepetitionConflictResolution(
                    resolvedData = mergedData,
                    strategy = ConflictResolutionStrategy.MERGE,
                    reason = "Data merged from both sources"
                )
            }
        }
    }
    
    /**
     * Resolves conflicts for flashcard data
     */
    fun resolveFlashcardConflict(
        local: Flashcard,
        remote: Flashcard
    ): FlashcardConflictResolution {
        // For custom flashcards, prefer local changes
        // For system flashcards, prefer remote changes
        return if (local.isCustom) {
            FlashcardConflictResolution(
                resolvedFlashcard = local,
                strategy = ConflictResolutionStrategy.LOCAL_WINS,
                reason = "Local changes preferred for custom flashcards"
            )
        } else {
            FlashcardConflictResolution(
                resolvedFlashcard = remote,
                strategy = ConflictResolutionStrategy.REMOTE_WINS,
                reason = "Remote changes preferred for system flashcards"
            )
        }
    }
    
    // ===== PRIVATE HELPER METHODS =====
    
    /**
     * Merges user data from local and remote sources
     */
    private fun mergeUserData(local: User, remote: User): User {
        return User(
            id = local.id,
            username = if (local.username != remote.username) local.username else remote.username,
            email = if (local.email != remote.email) local.email else remote.email,
            avatar = local.avatar ?: remote.avatar,
            createdAt = if (local.createdAt.isBefore(remote.createdAt)) local.createdAt else remote.createdAt,
            lastLogin = if (local.lastLogin?.isAfter(remote.lastLogin ?: Instant.MIN) == true) 
                local.lastLogin else remote.lastLogin,
            learningProgress = resolveLearningProgressConflict(
                local.learningProgress, 
                remote.learningProgress
            ).resolvedProgress
        )
    }
    
    /**
     * Merges level progress maps
     */
    private fun mergeLevelProgress(
        local: Map<JLPTLevel, Float>,
        remote: Map<JLPTLevel, Float>
    ): Map<JLPTLevel, Float> {
        val merged = mutableMapOf<JLPTLevel, Float>()
        
        // Add all levels from both maps, taking the maximum progress
        val allLevels = (local.keys + remote.keys).distinct()
        
        for (level in allLevels) {
            val localProgress = local[level] ?: 0.0f
            val remoteProgress = remote[level] ?: 0.0f
            merged[level] = maxOf(localProgress, remoteProgress)
        }
        
        return merged
    }
    
    /**
     * Merges spaced repetition data
     */
    private fun mergeSpacedRepetitionData(
        local: SpacedRepetitionData,
        remote: SpacedRepetitionData
    ): SpacedRepetitionData {
        return SpacedRepetitionData(
            userId = local.userId,
            flashcardId = local.flashcardId,
            repetitionCount = maxOf(local.repetitionCount, remote.repetitionCount),
            easeFactor = (local.easeFactor + remote.easeFactor) / 2.0f, // Average ease factor
            interval = maxOf(local.interval, remote.interval),
            nextReview = if (local.nextReview.isAfter(remote.nextReview)) local.nextReview else remote.nextReview,
            lastReviewed = if (local.lastReviewed?.isAfter(remote.lastReviewed ?: Instant.MIN) == true) 
                local.lastReviewed else remote.lastReviewed,
            correctStreak = maxOf(local.correctStreak, remote.correctStreak),
            totalReviews = maxOf(local.totalReviews, remote.totalReviews),
            averageResponseTime = (local.averageResponseTime + remote.averageResponseTime) / 2,
            difficultyAdjustment = (local.difficultyAdjustment + remote.difficultyAdjustment) / 2.0f
        )
    }
}

// ===== CONFLICT RESOLUTION DATA CLASSES =====

enum class ConflictResolutionStrategy {
    LOCAL_WINS,
    REMOTE_WINS,
    MERGE
}

data class UserConflictResolution(
    val resolvedUser: User,
    val strategy: ConflictResolutionStrategy,
    val reason: String
)

data class LearningProgressConflictResolution(
    val resolvedProgress: LearningProgress,
    val strategy: ConflictResolutionStrategy,
    val reason: String
)

data class SpacedRepetitionConflictResolution(
    val resolvedData: SpacedRepetitionData,
    val strategy: ConflictResolutionStrategy,
    val reason: String
)

data class FlashcardConflictResolution(
    val resolvedFlashcard: Flashcard,
    val strategy: ConflictResolutionStrategy,
    val reason: String
)