package com.example.sakura_flashcard.data.performance

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.api.toFlashcardModels
import com.example.sakura_flashcard.data.api.toCharacterModels
import com.example.sakura_flashcard.data.api.toQuizResultModels
import com.example.sakura_flashcard.data.api.toGameResultModels
import com.example.sakura_flashcard.data.local.FlashcardDatabase
import com.example.sakura_flashcard.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages lazy loading for large datasets to improve performance
 */
@Singleton
class LazyLoadingManager @Inject constructor(
    private val apiService: ApiService,
    private val database: FlashcardDatabase
) {
    
    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
        private const val MAX_SIZE = 200
    }
    
    /**
     * Creates a paged flow for flashcards by topic and level
     */
    fun getPagedFlashcards(
        topic: VocabularyTopic,
        level: JLPTLevel
    ): Flow<PagingData<Flashcard>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                maxSize = MAX_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FlashcardPagingSource(
                    apiService = apiService,
                    database = database,
                    topic = topic,
                    level = level
                )
            }
        ).flow
    }
    
    /**
     * Creates a paged flow for characters by script
     */
    fun getPagedCharacters(script: CharacterScript): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                maxSize = MAX_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CharacterPagingSource(
                    apiService = apiService,
                    database = database,
                    script = script
                )
            }
        ).flow
    }
    
    /**
     * Creates a paged flow for quiz results
     */
    fun getPagedQuizResults(userId: String): Flow<PagingData<QuizResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                maxSize = MAX_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                QuizResultPagingSource(
                    apiService = apiService,
                    database = database,
                    userId = userId
                )
            }
        ).flow
    }
    
    /**
     * Creates a paged flow for game results
     */
    fun getPagedGameResults(userId: String): Flow<PagingData<GameResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                maxSize = MAX_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GameResultPagingSource(
                    apiService = apiService,
                    database = database,
                    userId = userId
                )
            }
        ).flow
    }
}

/**
 * Paging source for flashcards
 */
class FlashcardPagingSource(
    private val apiService: ApiService,
    private val database: FlashcardDatabase,
    private val topic: VocabularyTopic,
    private val level: JLPTLevel
) : PagingSource<Int, Flashcard>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Flashcard> {
        return try {
            val page = params.key ?: 0
            val offset = page * params.loadSize
            
            // Try to load from network first
            val networkResult = try {
                val response = apiService.getFlashcardsByTopicAndLevel(topic.name, level.name)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
            
            val data = if (networkResult != null) {
                // Convert DTOs to models
                val models = networkResult.toFlashcardModels()
                
                // Cache converted models
                val entities = models.map { 
                    com.example.sakura_flashcard.data.local.entity.FlashcardEntity.fromFlashcard(it, needsSync = false) 
                }
                database.flashcardDao().insertFlashcards(entities)
                
                // Return paginated subset
                models.drop(offset).take(params.loadSize)
            } else {
                // Fallback to local data
                val localEntities = database.flashcardDao()
                    .getFlashcardsByTopicAndLevelPaged(topic, level, params.loadSize, offset)
                localEntities.map { it.toFlashcard() }
            }
            
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.isEmpty() || data.size < params.loadSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, Flashcard>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

/**
 * Paging source for characters
 */
class CharacterPagingSource(
    private val apiService: ApiService,
    private val database: FlashcardDatabase,
    private val script: CharacterScript
) : PagingSource<Int, Character>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            val page = params.key ?: 0
            val offset = page * params.loadSize
            
            // Try to load from network first
            val networkResult = try {
                val response = apiService.getCharactersByScript(script.name)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
            
            val data = if (networkResult != null) {
                // Convert DTOs to models
                val models = networkResult.toCharacterModels()
                
                // Cache converted models
                val entities = models.map { 
                    com.example.sakura_flashcard.data.local.entity.CharacterEntity.fromCharacter(it) 
                }
                database.characterDao().insertCharacters(entities)
                
                // Return paginated subset
                models.drop(offset).take(params.loadSize)
            } else {
                // Fallback to local data
                val localEntities = database.characterDao()
                    .getCharactersByScriptPaged(script, params.loadSize, offset)
                localEntities.map { it.toCharacter() }
            }
            
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.isEmpty() || data.size < params.loadSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

/**
 * Paging source for quiz results
 */
class QuizResultPagingSource(
    private val apiService: ApiService,
    private val database: FlashcardDatabase,
    private val userId: String
) : PagingSource<Int, QuizResult>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuizResult> {
        return try {
            val page = params.key ?: 0
            val offset = page * params.loadSize
            
            // Try to load from network first
            val networkResult = try {
                val response = apiService.getUserQuizResults(userId)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
            
            val data = if (networkResult != null) {
                // Convert DTOs to models
                val models = networkResult.toQuizResultModels(userId)
                
                // Cache converted models
                val entities = models.map { 
                    com.example.sakura_flashcard.data.local.entity.QuizResultEntity.fromQuizResult(it, needsSync = false) 
                }
                database.quizResultDao().insertQuizResults(entities)
                
                // Return paginated subset
                models.drop(offset).take(params.loadSize)
            } else {
                // Fallback to local data
                val localEntities = database.quizResultDao()
                    .getQuizResultsByUserPaged(userId, params.loadSize, offset)
                localEntities.map { it.toQuizResult() }
            }
            
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.isEmpty() || data.size < params.loadSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, QuizResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

/**
 * Paging source for game results
 */
class GameResultPagingSource(
    private val apiService: ApiService,
    private val database: FlashcardDatabase,
    private val userId: String
) : PagingSource<Int, GameResult>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GameResult> {
        return try {
            val page = params.key ?: 0
            val offset = page * params.loadSize
            
            // Try to load from network first
            val networkResult = try {
                val response = apiService.getUserGameResults(userId)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
            
            val data = if (networkResult != null) {
                // Convert DTOs to models
                val models = networkResult.toGameResultModels(userId)
                
                // Cache converted models
                val entities = models.map { 
                    com.example.sakura_flashcard.data.local.entity.GameResultEntity.fromGameResult(it, needsSync = false) 
                }
                database.gameResultDao().insertGameResults(entities)
                
                // Return paginated subset
                models.drop(offset).take(params.loadSize)
            } else {
                // Fallback to local data
                val localEntities = database.gameResultDao()
                    .getGameResultsByUserPaged(userId, params.loadSize, offset)
                localEntities.map { it.toGameResult() }
            }
            
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.isEmpty() || data.size < params.loadSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, GameResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}