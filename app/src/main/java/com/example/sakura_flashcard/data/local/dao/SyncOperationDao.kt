package com.example.sakura_flashcard.data.local.dao

import androidx.room.*
import com.example.sakura_flashcard.data.local.entity.SyncOperationEntity
import com.example.sakura_flashcard.data.local.entity.SyncOperationType
import kotlinx.coroutines.flow.Flow

/**
 * DAO for sync operation operations
 */
@Dao
interface SyncOperationDao {
    
    @Query("SELECT * FROM sync_operations ORDER BY timestamp ASC")
    suspend fun getAllSyncOperations(): List<SyncOperationEntity>
    
    @Query("SELECT * FROM sync_operations ORDER BY timestamp ASC")
    fun getAllSyncOperationsFlow(): Flow<List<SyncOperationEntity>>
    
    @Query("SELECT * FROM sync_operations WHERE id = :id")
    suspend fun getSyncOperationById(id: String): SyncOperationEntity?
    
    @Query("SELECT * FROM sync_operations WHERE type = :type ORDER BY timestamp ASC")
    suspend fun getSyncOperationsByType(type: SyncOperationType): List<SyncOperationEntity>
    
    @Query("SELECT * FROM sync_operations WHERE userId = :userId ORDER BY timestamp ASC")
    suspend fun getSyncOperationsByUser(userId: String): List<SyncOperationEntity>
    
    @Query("SELECT * FROM sync_operations WHERE entityId = :entityId ORDER BY timestamp ASC")
    suspend fun getSyncOperationsByEntity(entityId: String): List<SyncOperationEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncOperation(operation: SyncOperationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncOperations(operations: List<SyncOperationEntity>)
    
    @Update
    suspend fun updateSyncOperation(operation: SyncOperationEntity)
    
    @Delete
    suspend fun deleteSyncOperation(operation: SyncOperationEntity)
    
    @Query("DELETE FROM sync_operations WHERE id = :id")
    suspend fun deleteSyncOperationById(id: String)
    
    @Query("DELETE FROM sync_operations WHERE entityId = :entityId AND type = :type")
    suspend fun deleteSyncOperationsByEntityAndType(entityId: String, type: SyncOperationType)
    
    @Query("UPDATE sync_operations SET retryCount = retryCount + 1, lastError = :error WHERE id = :id")
    suspend fun incrementRetryCount(id: String, error: String)
    
    @Query("SELECT COUNT(*) FROM sync_operations")
    suspend fun getSyncOperationCount(): Int
    
    @Query("SELECT COUNT(*) FROM sync_operations WHERE userId = :userId")
    suspend fun getSyncOperationCountByUser(userId: String): Int
    
    @Query("DELETE FROM sync_operations WHERE retryCount >= 5")
    suspend fun deleteFailedOperations()
    
    @Query("DELETE FROM sync_operations")
    suspend fun clearAllSyncOperations()
}