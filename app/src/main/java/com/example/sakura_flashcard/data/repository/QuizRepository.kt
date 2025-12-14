package com.example.sakura_flashcard.data.repository

import com.example.sakura_flashcard.data.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getQuizSets(
        topic: String? = null,
        level: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): Result<PaginatedResponse<QuizSetDto>> {
        return try {
            val response = apiService.getQuizSets(topic, level, page, limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuizSetById(id: String): Result<QuizSetDto> {
        return try {
            val response = apiService.getQuizSetById(id)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuizTopics(level: String? = null): Result<List<QuizTopicInfo>> {
        return try {
            val response = apiService.getQuizTopics(level)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitQuiz(
        quizSetId: String,
        answers: List<QuizAnswer>,
        timeSpent: Int
    ): Result<QuizResultDto> {
        return try {
            val response = apiService.submitQuiz(
                SubmitQuizRequest(quizSetId, answers, timeSpent)
            )
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to submit"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuizHistory(page: Int = 1, limit: Int = 20): Result<PaginatedResponse<QuizAttemptDto>> {
        return try {
            val response = apiService.getQuizHistory(page, limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserStats(): Result<UserStatsDto> {
        return try {
            val response = apiService.getUserStats()
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
