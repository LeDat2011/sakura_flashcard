package com.example.sakura_flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.model.Converters
import java.time.Instant

/**
 * Room entity for storing pending sync operations
 */
@Entity(tableName = "sync_operations")
@TypeConverters(Converters::class)
data class SyncOperationEntity(
    @PrimaryKey
    val id: String,
    val type: SyncOperationType,
    val entityId: String, // ID of the entity to sync (flashcard, user, etc.)
    val userId: String,
    val data: String, // JSON serialized data
    val timestamp: Instant,
    val retryCount: Int = 0,
    val lastError: String? = null
)

enum class SyncOperationType {
    FLASHCARD_PROGRESS_UPDATE,
    USER_PROGRESS_UPDATE,
    CUSTOM_FLASHCARD_CREATE,
    CUSTOM_FLASHCARD_UPDATE,
    CUSTOM_FLASHCARD_DELETE,
    QUIZ_RESULT_SUBMIT,
    GAME_RESULT_SUBMIT
}