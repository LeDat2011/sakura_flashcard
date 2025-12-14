package com.example.sakura_flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.model.*
import java.time.Instant

/**
 * Room entity for storing game results locally
 */
@Entity(tableName = "game_results")
@TypeConverters(Converters::class)
data class GameResultEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val gameType: GameType,
    val score: Int,
    val completedAt: Instant,
    val timeSpentSeconds: Long,
    val level: JLPTLevel,
    val lastModified: Instant,
    val needsSync: Boolean = false
) {
    fun toGameResult(): GameResult {
        return GameResult(
            id = id,
            userId = userId,
            gameType = gameType,
            score = score,
            completedAt = completedAt,
            timeSpentSeconds = timeSpentSeconds,
            level = level
        )
    }
    
    companion object {
        fun fromGameResult(result: GameResult, needsSync: Boolean = false): GameResultEntity {
            return GameResultEntity(
                id = result.id,
                userId = result.userId,
                gameType = result.gameType,
                score = result.score,
                completedAt = result.completedAt,
                timeSpentSeconds = result.timeSpentSeconds,
                level = result.level,
                lastModified = Instant.now(),
                needsSync = needsSync
            )
        }
    }
}