package com.example.sakura_flashcard.data.repository

import com.example.sakura_flashcard.data.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabularyRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getVocabularies(
        topic: String? = null,
        level: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): Result<PaginatedResponse<VocabularyDto>> {
        return try {
            val response = apiService.getVocabularies(topic, level, page, limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVocabularyById(id: String): Result<VocabularyDto> {
        return try {
            val response = apiService.getVocabularyById(id)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopics(level: String? = null): Result<List<TopicInfo>> {
        return try {
            val response = apiService.getVocabularyTopics(level)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchVocabulary(query: String, level: String? = null): Result<List<VocabularyDto>> {
        return try {
            val response = apiService.searchVocabulary(query, level)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== User Progress ====================

    suspend fun getDueVocabulary(limit: Int = 20): Result<List<VocabularyProgressDto>> {
        return try {
            val response = apiService.getDueVocabulary(limit)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProgress(vocabularyId: String, quality: Int): Result<VocabularyProgressResult> {
        return try {
            val response = apiService.updateVocabularyProgress(
                VocabularyReviewRequest(vocabularyId, quality)
            )
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStats(topic: String? = null, level: String? = null): Result<VocabularyStatsDto> {
        return try {
            val response = apiService.getVocabularyStats(topic, level)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
