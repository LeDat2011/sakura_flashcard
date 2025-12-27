package com.example.sakura_flashcard.data.api

import com.google.gson.annotations.SerializedName

// ==================== Base Response ====================
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class PaginatedResponse<T>(
    val success: Boolean,
    val message: String,
    val data: List<T>,
    val pagination: Pagination
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean
)

// ==================== Auth ====================
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val displayName: String? = null
)

data class GoogleLoginRequest(
    val idToken: String
)

data class OTPRequest(
    val email: String
)

data class OTPVerifyRequest(
    val email: String,
    val otp: String
)

data class UpdateProfileRequest(
    val username: String? = null,
    val displayName: String? = null,
    val avatar: String? = null,
    val currentLevel: String? = null
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class LogoutRequest(
    val refreshToken: String
)

data class AuthData(
    val user: UserProfile,
    val accessToken: String,
    val refreshToken: String
)

data class TokenData(
    val accessToken: String,
    val refreshToken: String
)

data class UserProfile(
    @SerializedName(value = "_id", alternate = ["id"]) val id: String,
    val email: String,
    val username: String,
    val profile: UserProfileDetails,
    val stats: UserStats? = null,
    val role: String = "user",
    val isActive: Boolean = true
)

data class UserProfileDetails(
    val displayName: String,
    val avatar: String? = null,
    val currentLevel: String = "N5"
)

data class UserStats(
    val totalXP: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStudyDate: String? = null
)

// ==================== Vocabulary ====================
data class VocabularyDto(
    @SerializedName("_id") val id: String,
    val word: WordInfo,
    val details: VocabularyDetails,
    val media: MediaInfo? = null,
    val topic: String,
    val level: String,
    val difficulty: Int = 1,
    val order: Int = 0,
    val tags: List<String> = emptyList(),
    val isActive: Boolean = true
)

data class WordInfo(
    val japanese: String,
    val hiragana: String,
    val romaji: String? = null,
    val vietnamese: String,
    val wordType: String? = null
)

data class VocabularyDetails(
    val explanation: String? = null,
    val exampleSentences: List<ExampleSentence> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val memoryTip: String? = null
)

data class ExampleSentence(
    val japanese: String,
    val vietnamese: String
)

data class MediaInfo(
    val audioUrl: String? = null,
    val imageUrl: String? = null
)

data class TopicInfo(
    val topic: String,
    val level: String,
    val count: Int
)

// ==================== Quiz ====================
data class QuizSetDto(
    @SerializedName("_id") val id: String,
    val topic: String,
    val level: String,
    val setNumber: Int,
    val title: String,
    val description: String? = null,
    val questions: List<QuizQuestionDto>,
    val settings: QuizSettings? = null
)

data class QuizQuestionDto(
    @SerializedName("_id") val id: String? = null,
    val questionNumber: Int? = null,
    val questionType: String,
    val questionText: String,
    val options: List<QuizOption>,
    val correctAnswer: String? = null,
    val explanation: String? = null,
    val points: Int = 10,
    val difficulty: Int = 1
)

data class QuizOption(
    val optionId: String,
    val text: String,
    val isCorrect: Boolean = false
)

data class QuizSettings(
    val timeLimit: Int = 600,
    val passingScore: Int = 70,
    val shuffleQuestions: Boolean = true,
    val shuffleOptions: Boolean = true
)

data class QuizTopicInfo(
    val topic: String,
    val level: String,
    val quizCount: Int,
    val totalQuestions: Int
)

data class SubmitQuizRequest(
    val quizSetId: String,
    val answers: List<QuizAnswer>,
    val timeSpent: Int
)

data class QuizAnswer(
    val questionId: String,
    val answerId: String
)

data class QuizResultDto(
    val attempt: QuizAttemptSummary,
    val results: List<QuestionResult>,
    val summary: QuizSummary
)

data class QuizAttemptSummary(
    val quizSetId: String,
    val score: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val passed: Boolean,
    val completedAt: String
)

data class QuestionResult(
    val questionId: String,
    val userAnswer: String?,
    val correctAnswer: String?,
    val isCorrect: Boolean,
    val points: Int,
    val explanation: String?
)

data class QuizSummary(
    val score: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val passed: Boolean,
    val passingScore: Int
)

data class QuizAttemptDto(
    @SerializedName("_id") val id: String,
    val quizSetId: QuizSetRef,
    val score: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val passed: Boolean,
    val completedAt: String
)

data class QuizSetRef(
    @SerializedName("_id") val id: String,
    val title: String,
    val topic: String,
    val level: String
)

// ==================== User Progress ====================
data class VocabularyProgressDto(
    @SerializedName("_id") val id: String,
    val vocabularyId: VocabularyDto,
    val status: String,
    val sm2: SM2Data,
    val stats: ProgressStats
)

data class SM2Data(
    val repetitions: Int,
    val easeFactor: Double,
    val interval: Int,
    val nextReviewAt: String,
    val lastReviewedAt: String?
)

data class ProgressStats(
    val correctCount: Int,
    val incorrectCount: Int,
    val totalReviews: Int
)

data class VocabularyReviewRequest(
    val vocabularyId: String,
    val quality: Int // 0-5
)

data class VocabularyProgressResult(
    val progress: VocabularyProgressDto,
    val xpEarned: Int
)

data class VocabularyStatsDto(
    val new: Int,
    val learning: Int,
    val reviewing: Int,
    val mastered: Int,
    val total: Int
)

data class UserStatsDto(
    val user: UserStats?,
    val vocabulary: VocabSummary,
    val quiz: QuizSummaryStats
)

data class VocabSummary(
    val total: Int,
    val mastered: Int,
    val totalReviews: Int
)

data class QuizSummaryStats(
    val totalAttempts: Int,
    val avgScore: Double,
    val totalPassed: Int
)

// ==================== Game & Leaderboard ====================
data class LeaderboardEntry(
    val userId: String,
    val username: String,
    val score: Int,
    val gameType: String,
    val completedAt: String,
    val rank: Int
)

data class FlashcardProgressUpdate(
    val userId: String,
    val difficulty: Float,
    val lastReviewed: String,
    val nextReview: String?
)

// ==================== Flashcard DTOs ====================
data class FlashcardDto(
    @SerializedName("_id") val id: String,
    val front: String,
    val back: String,
    val reading: String? = null,
    val topic: String? = null,
    val level: String? = null,
    val category: String? = null,
    val audioUrl: String? = null,
    val imageUrl: String? = null
)

// ==================== Character DTOs ====================
data class CharacterDto(
    @SerializedName("_id") val id: String,
    val character: String,
    val romaji: String,
    val type: String, // "hiragana", "katakana", "kanji"
    val meaning: String? = null,
    val strokeOrder: List<String>? = null,
    val examples: List<String>? = null
)

// ==================== Game DTOs ====================
data class GameResultDto(
    @SerializedName("_id") val id: String? = null,
    val gameType: String,
    val score: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val timeSpent: Int,
    val completedAt: String? = null
)

// ==================== User Progress DTOs ====================
data class UserProgressDto(
    val userId: String,
    val totalXP: Int,
    val currentLevel: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastStudyDate: String?,
    val vocabularyProgress: VocabProgressSummary,
    val quizProgress: QuizProgressSummary
)

data class VocabProgressSummary(
    val learned: Int,
    val mastered: Int,
    val total: Int
)

data class QuizProgressSummary(
    val completed: Int,
    val averageScore: Double
)
