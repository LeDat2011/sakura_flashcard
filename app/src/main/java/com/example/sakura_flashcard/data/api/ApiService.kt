package com.example.sakura_flashcard.data.api

// ApiService for Retrofit
import com.example.sakura_flashcard.data.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== AUTH ====================
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthData>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthData>>

    @POST("auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<ApiResponse<AuthData>>

    @POST("auth/otp/send")
    suspend fun sendOTP(@Body request: OTPRequest): Response<ApiResponse<Nothing>>

    @POST("auth/otp/verify")
    suspend fun verifyOTP(@Body request: OTPVerifyRequest): Response<ApiResponse<AuthData>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Nothing>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Nothing>>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<ApiResponse<TokenData>>

    @POST("auth/refresh-token")
    fun refreshTokenBlocking(@Body request: RefreshTokenRequest): Call<ApiResponse<TokenData>>

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<ApiResponse<Nothing>>

    @GET("auth/profile")
    suspend fun getProfile(): Response<ApiResponse<UserProfile>>

    @PUT("auth/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserProfile>>

    // ==================== VOCABULARY ====================
    @GET("vocabularies")
    suspend fun getVocabularies(
        @Query("topic") topic: String? = null,
        @Query("level") level: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<VocabularyDto>>

    @GET("vocabularies/topics")
    suspend fun getVocabularyTopics(
        @Query("level") level: String? = null
    ): Response<ApiResponse<List<TopicInfo>>>

    @GET("vocabularies/search")
    suspend fun searchVocabulary(
        @Query("q") query: String,
        @Query("level") level: String? = null
    ): Response<ApiResponse<List<VocabularyDto>>>

    @GET("vocabularies/{id}")
    suspend fun getVocabularyById(@Path("id") id: String): Response<ApiResponse<VocabularyDto>>

    // ==================== QUIZ ====================
    @GET("quiz")
    suspend fun getQuizSets(
        @Query("topic") topic: String? = null,
        @Query("level") level: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<QuizSetDto>>

    @GET("quiz/topics")
    suspend fun getQuizTopics(
        @Query("level") level: String? = null
    ): Response<ApiResponse<List<QuizTopicInfo>>>

    @GET("quiz/{id}")
    suspend fun getQuizSetById(@Path("id") id: String): Response<ApiResponse<QuizSetDto>>

    @POST("quiz/submit")
    suspend fun submitQuiz(@Body request: SubmitQuizRequest): Response<ApiResponse<QuizResultDto>>

    // ==================== USER PROGRESS ====================
    @GET("user/vocabulary/due")
    suspend fun getDueVocabulary(
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<VocabularyProgressDto>>>

    @POST("user/vocabulary/review")
    suspend fun updateVocabularyProgress(
        @Body request: VocabularyReviewRequest
    ): Response<ApiResponse<VocabularyProgressResult>>

    @GET("user/vocabulary/stats")
    suspend fun getVocabularyStats(
        @Query("topic") topic: String? = null,
        @Query("level") level: String? = null
    ): Response<ApiResponse<VocabularyStatsDto>>

    @GET("user/quiz/history")
    suspend fun getQuizHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<QuizAttemptDto>>

    @GET("user/stats")
    suspend fun getUserStats(): Response<ApiResponse<UserStatsDto>>

    // ==================== FLASHCARD ====================
    @GET("flashcards")
    suspend fun getFlashcardsByTopicAndLevel(
        @Query("topic") topic: String,
        @Query("level") level: String
    ): Response<List<FlashcardDto>>

    @GET("vocabularies")
    suspend fun getRecommendedFlashcards(
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1
    ): Response<PaginatedResponse<VocabularyDto>>

    @POST("flashcards/custom")
    suspend fun createCustomFlashcard(
        @Body flashcard: FlashcardDto
    ): Response<FlashcardDto>

    // ==================== CHARACTER ====================
    @GET("characters")
    suspend fun getCharactersByScript(
        @Query("script") script: String
    ): Response<List<CharacterDto>>

    @GET("characters/{id}")
    suspend fun getCharacterDetail(
        @Path("id") id: String
    ): Response<CharacterDto>

    // ==================== USER RESULTS ====================
    @GET("user/quiz/results")
    suspend fun getUserQuizResults(
        @Query("userId") userId: String
    ): Response<List<QuizResultDto>>

    @GET("user/game/results")
    suspend fun getUserGameResults(
        @Query("userId") userId: String
    ): Response<List<GameResultDto>>

    @POST("quiz/result")
    suspend fun submitQuizResult(
        @Body result: QuizResultDto
    ): Response<Unit>

    @POST("game/result")
    suspend fun submitGameResult(
        @Body result: GameResultDto
    ): Response<Unit>

    // ==================== USER ====================
    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") userId: String
    ): Response<ApiResponse<User>>

    @GET("user/progress")
    suspend fun getUserProgress(): Response<ApiResponse<UserProgressDto>>
}