package com.example.sakura_flashcard.data.local.dao

import androidx.room.*
import com.example.sakura_flashcard.data.local.entity.CharacterEntity
import com.example.sakura_flashcard.data.model.CharacterScript
import kotlinx.coroutines.flow.Flow

/**
 * DAO for character operations
 */
@Dao
interface CharacterDao {
    
    @Query("SELECT * FROM characters")
    fun getAllCharacters(): Flow<List<CharacterEntity>>
    
    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: String): CharacterEntity?
    
    @Query("SELECT * FROM characters WHERE script = :script ORDER BY character ASC")
    suspend fun getCharactersByScript(script: CharacterScript): List<CharacterEntity>
    
    @Query("SELECT * FROM characters WHERE script = :script ORDER BY character ASC LIMIT :limit OFFSET :offset")
    suspend fun getCharactersByScriptPaged(
        script: CharacterScript,
        limit: Int,
        offset: Int
    ): List<CharacterEntity>
    
    @Query("SELECT * FROM characters WHERE script = :script ORDER BY character ASC")
    fun getCharactersByScriptFlow(script: CharacterScript): Flow<List<CharacterEntity>>
    
    @Query("SELECT * FROM characters WHERE character LIKE '%' || :query || '%'")
    suspend fun searchCharacters(query: String): List<CharacterEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)
    
    @Update
    suspend fun updateCharacter(character: CharacterEntity)
    
    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)
    
    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacterById(id: String)
    
    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharacterCount(): Int
    
    @Query("SELECT COUNT(*) FROM characters WHERE script = :script")
    suspend fun getCharacterCountByScript(script: CharacterScript): Int
    
    @Query("DELETE FROM characters")
    suspend fun clearAllCharacters()
}