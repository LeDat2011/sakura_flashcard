package com.example.sakura_flashcard.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.sakura_flashcard.data.local.dao.*
import com.example.sakura_flashcard.data.local.entity.*
import com.example.sakura_flashcard.data.model.Converters

/**
 * Room database for offline storage of flashcards, user progress, and sync operations
 */
@Database(
    entities = [
        FlashcardEntity::class,
        UserEntity::class,
        SpacedRepetitionEntity::class,
        QuizResultEntity::class,
        GameResultEntity::class,
        CharacterEntity::class,
        SyncOperationEntity::class,
        CustomDeckEntity::class,
        CustomFlashcardEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlashcardDatabase : RoomDatabase() {
    
    abstract fun flashcardDao(): FlashcardDao
    abstract fun userDao(): UserDao
    abstract fun spacedRepetitionDao(): SpacedRepetitionDao
    abstract fun quizResultDao(): QuizResultDao
    abstract fun gameResultDao(): GameResultDao
    abstract fun characterDao(): CharacterDao
    abstract fun syncOperationDao(): SyncOperationDao
    abstract fun customDeckDao(): CustomDeckDao
    
    companion object {
        const val DATABASE_NAME = "flashcard_database"
        
        fun create(context: Context): FlashcardDatabase {
            return Room.databaseBuilder(
                context,
                FlashcardDatabase::class.java,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}