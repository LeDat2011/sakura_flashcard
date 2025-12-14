package com.example.sakura_flashcard.data.local.dao

import androidx.room.*
import com.example.sakura_flashcard.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for user operations
 */
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserByIdFlow(id: String): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE needsSync = 1")
    suspend fun getUsersNeedingSync(): List<UserEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)
    
    @Query("UPDATE users SET needsSync = 0 WHERE id = :id")
    suspend fun markAsSynced(id: String)
    
    @Query("UPDATE users SET needsSync = 1 WHERE id = :id")
    suspend fun markAsNeedsSync(id: String)
    
    @Query("UPDATE users SET lastLogin = :lastLogin WHERE id = :id")
    suspend fun updateLastLogin(id: String, lastLogin: Long)
    
    @Query("""
        UPDATE users SET 
        flashcardsLearned = :flashcardsLearned,
        quizzesCompleted = :quizzesCompleted,
        currentStreak = :currentStreak,
        totalStudyTimeMinutes = :totalStudyTimeMinutes,
        needsSync = 1,
        lastModified = :lastModified
        WHERE id = :id
    """)
    suspend fun updateLearningProgress(
        id: String,
        flashcardsLearned: Int,
        quizzesCompleted: Int,
        currentStreak: Int,
        totalStudyTimeMinutes: Long,
        lastModified: Long
    )
    
    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
}