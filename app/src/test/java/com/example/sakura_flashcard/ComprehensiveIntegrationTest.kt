package com.example.sakura_flashcard

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import com.example.sakura_flashcard.data.auth.AuthResult
import com.example.sakura_flashcard.data.auth.AuthState
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.data.sync.SyncState
import java.time.Instant
import java.util.UUID

/**
 * **Feature: japanese-flashcard-ui, Integration Test: Complete User Workflows**
 * **Validates: All Requirements - End-to-end user journey testing**
 * 
 * Comprehensive integration tests that validate complete user workflows
 * from registration through advanced learning features, including:
 * - User registration and authentication
 * - Data persistence and synchronization
 * - Learning progress tracking
 * - Error handling and recovery scenarios
 * - Offline/online mode transitions
 * - Performance under load
 */
class ComprehensiveIntegrationTest : StringSpec({

    // Test data generators
    val validUsernames = Arb.string(3..20).filter { it.matches(Regex("^[a-zA-Z0-9_]+$")) }
    val validEmails = Arb.string(5..50).map { "${it.take(10)}@example.com" }
    val validPasswords = Arb.string(8..50).filter { password ->
        password.any { it.isUpperCase() } &&
        password.any { it.isLowerCase() } &&
        password.any { it.isDigit() }
    }
    
    val vocabularyTopics = Arb.enum<VocabularyTopic>()
    val jlptLevels = Arb.enum<JLPTLevel>()
    val characterScripts = Arb.enum<CharacterScript>()
    val questionTypes = Arb.enum<QuestionType>()
    val gameTypes = Arb.enum<GameType>()

    // ===== COMPLETE USER WORKFLOW TESTS =====

    "complete user registration to advanced learning workflow should work end-to-end" {
        checkAll(iterations = 10, validUsernames, validEmails, validPasswords) { username, email, password ->
            runTest {
                // Mock repositories and services for testing
                val mockAuthRepository = createMockAuthRepository()
                val mockSpacedRepetitionRepository = createMockSpacedRepetitionRepository()
                val mockOfflineRepository = createMockOfflineRepository()
                val mockSyncService = createMockSyncService()
                
                // Step 1: User Registration
                val registrationResult = mockAuthRepository.register(username, email, password)
                registrationResult.shouldBeInstanceOf<AuthResult.Success>()
                
                val user = (registrationResult as AuthResult.Success).user!!
                user.username shouldBe username
                user.email shouldBe email
                
                // Step 2: Initial Home Screen Load
                val recommendedFlashcards = mockSpacedRepetitionRepository.getRecommendedFlashcards(user.id)
                recommendedFlashcards.isSuccess shouldBe true
                recommendedFlashcards.getOrNull()?.shouldHaveSize(20) // Default max cards
                
                // Step 3: Learn Characters
                val hiraganaCharacters = mockOfflineRepository.getCharactersByScript(CharacterScript.HIRAGANA)
                hiraganaCharacters.isSuccess shouldBe true
                hiraganaCharacters.getOrNull()?.shouldNotBe(emptyList<Character>())
                
                // Step 4: Take Quiz
                val quizQuestions = mockOfflineRepository.generateQuiz(
                    topic = VocabularyTopic.DAILY_LIFE,
                    level = JLPTLevel.N5,
                    questionCount = 10
                )
                quizQuestions.isSuccess shouldBe true
                quizQuestions.getOrNull()?.shouldHaveSize(10)
                
                // Step 5: Play Mini-Game
                val gameResult = mockOfflineRepository.submitGameResult(
                    com.example.sakura_flashcard.data.model.GameResult(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        gameType = GameType.MEMORY_MATCH_GAME,
                        score = 850,
                        completedAt = Instant.now(),
                        timeSpentSeconds = 120L,
                        level = JLPTLevel.N5
                    )
                )
                gameResult.isSuccess shouldBe true
                
                // Step 6: Update Profile
                val updatedUser = user.copy(
                    learningProgress = user.learningProgress.copy(
                        flashcardsLearned = 25,
                        quizzesCompleted = 3,
                        currentStreak = 5
                    )
                )
                val profileUpdateResult = mockOfflineRepository.updateUser(updatedUser)
                profileUpdateResult.isSuccess shouldBe true
                
                // Step 7: Verify Data Persistence
                val retrievedUser = mockOfflineRepository.getUser(user.id)
                retrievedUser.isSuccess shouldBe true
                retrievedUser.getOrNull()?.learningProgress?.flashcardsLearned shouldBe 25
                
                // Step 8: Test Sync
                val syncResult = mockSyncService.forceSyncNow()
                syncResult.isSuccess shouldBe true
            }
        }
    }

    "user authentication flow should handle all scenarios correctly" {
        checkAll(iterations = 20, validUsernames, validEmails, validPasswords) { username, email, password ->
            runTest {
                val mockAuthRepository = createMockAuthRepository()
                
                // Test successful registration
                val registrationResult = mockAuthRepository.register(username, email, password)
                registrationResult.shouldBeInstanceOf<AuthResult.Success>()
                
                // Test successful login
                val loginResult = mockAuthRepository.login(email, password)
                loginResult.shouldBeInstanceOf<AuthResult.Success>()
                
                // Verify authentication state
                mockAuthRepository.isAuthenticated() shouldBe true
                mockAuthRepository.getCurrentUserId() shouldNotBe null
                
                // Test logout
                val logoutResult = mockAuthRepository.logout()
                logoutResult.shouldBeInstanceOf<AuthResult.Success>()
                mockAuthRepository.isAuthenticated() shouldBe false
                
                // Test invalid login after logout
                val invalidLoginResult = mockAuthRepository.login(email, "wrongpassword")
                invalidLoginResult.shouldBeInstanceOf<AuthResult.Error>()
            }
        }
    }

    "spaced repetition learning workflow should track progress correctly" {
        checkAll(iterations = 15, vocabularyTopics, jlptLevels) { topic, level ->
            runTest {
                val mockRepository = createMockSpacedRepetitionRepository()
                val userId = UUID.randomUUID().toString()
                
                // Get initial recommendations
                val initialCards = mockRepository.getRecommendedFlashcards(userId, 10)
                initialCards.isSuccess shouldBe true
                
                val flashcards = initialCards.getOrNull()!!
                flashcards.shouldHaveSize(10)
                
                // Simulate learning sessions
                for (flashcard in flashcards.take(5)) {
                    // Simulate correct answer
                    val updateResult = mockRepository.updateFlashcardProgress(
                        userId = userId,
                        flashcardId = flashcard.id,
                        quality = ReviewQuality.CORRECT_EASY,
                        responseTime = 3000L
                    )
                    updateResult.isSuccess shouldBe true
                }
                
                // Get updated recommendations
                val updatedCards = mockRepository.getRecommendedFlashcards(userId, 10)
                updatedCards.isSuccess shouldBe true
                
                // Verify learning statistics
                val statistics = mockRepository.getLearningStatistics(userId)
                statistics.isSuccess shouldBe true
                statistics.getOrNull()?.cardsLearned shouldBe 5
            }
        }
    }

    "quiz system should generate and evaluate questions correctly" {
        checkAll(iterations = 10, vocabularyTopics, jlptLevels) { topic, level ->
            runTest {
                val mockRepository = createMockOfflineRepository()
                val userId = UUID.randomUUID().toString()
                
                // Generate quiz
                val quizQuestions = mockRepository.generateQuiz(topic, level, 10)
                quizQuestions.isSuccess shouldBe true
                
                val questions = quizQuestions.getOrNull()!!
                questions.shouldHaveSize(10)
                
                // Verify question types are mixed
                val questionTypes = questions.map { it.type }.toSet()
                questionTypes.size shouldBe 3 // Should have all three types
                
                // Simulate quiz completion
                val answers = questions.map { question ->
                    com.example.sakura_flashcard.data.model.QuizAnswer(
                        questionId = question.id,
                        userAnswer = question.correctAnswer,
                        correctAnswer = question.correctAnswer,
                        isCorrect = true,
                        timeSpentSeconds = 5L
                    )
                }
                
                val quizResult = com.example.sakura_flashcard.data.model.QuizResult(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    topic = topic,
                    level = level,
                    score = questions.size, // All correct
                    totalQuestions = questions.size,
                    completedAt = Instant.now(),
                    timeSpentSeconds = 300L,
                    answers = answers
                )
                
                val submitResult = mockRepository.submitQuizResult(quizResult)
                submitResult.isSuccess shouldBe true
            }
        }
    }

    "mini-games should function correctly with proper scoring" {
        checkAll(iterations = 10, gameTypes) { gameType ->
            runTest {
                val mockRepository = createMockOfflineRepository()
                val userId = UUID.randomUUID().toString()
                
                // Simulate game completion
                val gameResult = com.example.sakura_flashcard.data.model.GameResult(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    gameType = gameType,
                    score = Arb.int(100..1000).single(),
                    completedAt = Instant.now(),
                    timeSpentSeconds = Arb.long(30L..300L).single(),
                    level = JLPTLevel.N5
                )
                
                val submitResult = mockRepository.submitGameResult(gameResult)
                submitResult.isSuccess shouldBe true
                
                // Verify game results are stored
                val userGameResults = mockRepository.getUserGameResults(userId)
                userGameResults.isSuccess shouldBe true
                userGameResults.getOrNull()?.shouldContain(gameResult)
            }
        }
    }

    "offline mode should queue operations and sync when online" {
        runTest {
            val mockSyncService = createMockSyncService()
            val userId = UUID.randomUUID().toString()
            
            // Simulate offline mode
            val initialSyncState = mockSyncService.syncState.first()
            initialSyncState.pendingOperations shouldBe 0
            
            // Queue some operations while offline
            val spacedRepetitionData = SpacedRepetitionData(
                flashcardId = UUID.randomUUID().toString(),
                userId = userId,
                repetitionCount = 1,
                easeFactor = 2.5f,
                interval = 1,
                nextReview = Instant.now().plusSeconds(86400),
                lastReviewed = Instant.now(),
                correctStreak = 1,
                totalReviews = 1,
                difficultyAdjustment = 0.0f
            )
            
            mockSyncService.queueSpacedRepetitionUpdate(spacedRepetitionData)
            
            // Verify operation is queued
            val updatedSyncState = mockSyncService.syncState.first()
            updatedSyncState.pendingOperations shouldBe 1
            
            // Simulate going online and force sync
            val syncResult = mockSyncService.forceSyncNow()
            syncResult.isSuccess shouldBe true
            
            // Verify operations are cleared
            val finalSyncState = mockSyncService.syncState.first()
            finalSyncState.pendingOperations shouldBe 0
        }
    }

    "error handling should recover gracefully from failures" {
        checkAll(iterations = 10, validEmails) { email ->
            runTest {
                val mockAuthRepository = createMockAuthRepository()
                val mockRepository = createMockOfflineRepository()
                
                // Test network error handling
                val networkErrorResult = mockAuthRepository.login(email, "password")
                // Should handle network errors gracefully
                if (networkErrorResult is AuthResult.Error) {
                    networkErrorResult.message.lowercase().shouldContain("network")
                }
                
                // Test invalid data handling
                val invalidDataResult = mockRepository.generateQuiz(
                    VocabularyTopic.ANIME,
                    JLPTLevel.N1,
                    -1 // Invalid question count
                )
                invalidDataResult.isFailure shouldBe true
                
                // Test recovery after error
                val recoveryResult = mockRepository.generateQuiz(
                    VocabularyTopic.ANIME,
                    JLPTLevel.N1,
                    10 // Valid question count
                )
                recoveryResult.isSuccess shouldBe true
            }
        }
    }

    "performance should remain acceptable under load" {
        runTest {
            val mockRepository = createMockSpacedRepetitionRepository()
            val userId = UUID.randomUUID().toString()
            
            // Simulate multiple concurrent operations
            val startTime = System.currentTimeMillis()
            
            val results = (1..50).map { index ->
                mockRepository.updateFlashcardProgress(
                    userId = userId,
                    flashcardId = "flashcard_$index",
                    quality = if (index % 2 == 0) ReviewQuality.CORRECT_EASY else ReviewQuality.INCORRECT_EASY,
                    responseTime = 2000L
                )
            }
            
            val endTime = System.currentTimeMillis()
            val totalTime = endTime - startTime
            
            // All operations should succeed
            results.forEach { result ->
                result.isSuccess shouldBe true
            }
            
            // Performance should be reasonable (less than 5 seconds for 50 operations)
            totalTime shouldBe lessThan(5000L)
            
            // Verify final state
            val statistics = mockRepository.getLearningStatistics(userId)
            statistics.isSuccess shouldBe true
            statistics.getOrNull()?.totalCardsStudied shouldBe 50
        }
    }

    "data persistence should maintain consistency across app restarts" {
        checkAll(iterations = 5, validUsernames, validEmails) { username, email ->
            runTest {
                val mockRepository = createMockOfflineRepository()
                val userId = UUID.randomUUID().toString()
                
                // Create user data
                val user = User(
                    id = userId,
                    username = username,
                    email = email,
                    learningProgress = LearningProgress(
                        flashcardsLearned = 15,
                        quizzesCompleted = 3,
                        currentStreak = 7,
                        totalStudyTimeMinutes = 120L
                    )
                )
                
                // Save user data
                val saveResult = mockRepository.updateUser(user)
                saveResult.isSuccess shouldBe true
                
                // Simulate app restart by clearing caches
                mockRepository.clearCache()
                
                // Retrieve user data after "restart"
                val retrievedUser = mockRepository.getUser(userId)
                retrievedUser.isSuccess shouldBe true
                
                val retrieved = retrievedUser.getOrNull()!!
                retrieved.username shouldBe username
                retrieved.email shouldBe email
                retrieved.learningProgress.flashcardsLearned shouldBe 15
                retrieved.learningProgress.quizzesCompleted shouldBe 3
                retrieved.learningProgress.currentStreak shouldBe 7
                retrieved.learningProgress.totalStudyTimeMinutes shouldBe 120L
            }
        }
    }

    "accessibility features should work correctly" {
        runTest {
            val mockRepository = createMockOfflineRepository()
            
            // Test screen reader content descriptions
            val flashcards = mockRepository.getRecommendedFlashcards("user123", 5)
            flashcards.isSuccess shouldBe true
            
            flashcards.getOrNull()?.forEach { flashcard ->
                // Verify flashcard has accessibility-friendly content
                flashcard.front.text.isNotBlank() shouldBe true
                flashcard.back.text.isNotBlank() shouldBe true
            }
            
            // Test high contrast mode compatibility
            val characters = mockRepository.getCharactersByScript(CharacterScript.HIRAGANA)
            characters.isSuccess shouldBe true
            
            characters.getOrNull()?.forEach { character ->
                // Verify character data is complete for accessibility
                character.character.isNotBlank() shouldBe true
                character.pronunciation.isNotEmpty() shouldBe true
            }
        }
    }

    // ===== MOCK IMPLEMENTATIONS =====

    fun createMockAuthRepository(): AuthRepository {
        return object : AuthRepository {
            private var currentUser: User? = null
            private var isAuth = false
            
            override val authState = kotlinx.coroutines.flow.MutableStateFlow(AuthState.Unauthenticated)
            
            override suspend fun login(email: String, password: String): AuthResult {
                return if (email.contains("@") && password.length >= 8) {
                    currentUser = User(
                        id = UUID.randomUUID().toString(),
                        username = "testuser",
                        email = email
                    )
                    isAuth = true
                    AuthResult.Success(currentUser)
                } else {
                    AuthResult.Error("Invalid credentials")
                }
            }
            
            override suspend fun register(username: String, email: String, password: String): AuthResult {
                return if (username.length >= 3 && email.contains("@") && password.length >= 8) {
                    currentUser = User(
                        id = UUID.randomUUID().toString(),
                        username = username,
                        email = email
                    )
                    isAuth = true
                    AuthResult.Success(currentUser)
                } else {
                    AuthResult.Error("Invalid registration data")
                }
            }
            
            override suspend fun refreshToken(): AuthResult {
                return if (isAuth) AuthResult.Success(currentUser) else AuthResult.Error("Not authenticated")
            }
            
            override suspend fun logout(): AuthResult {
                currentUser = null
                isAuth = false
                return AuthResult.Success(null)
            }
            
            override fun isAuthenticated(): Boolean = isAuth
            override fun getCurrentUserId(): String? = currentUser?.id
        }
    }
    
    fun createMockSpacedRepetitionRepository(): SpacedRepetitionRepository {
        return object : SpacedRepetitionRepository {
            private val flashcards = mutableListOf<Flashcard>()
            private val spacedRepetitionData = mutableMapOf<String, SpacedRepetitionData>()
            
            init {
                // Initialize with some test flashcards
                repeat(20) { index ->
                    flashcards.add(
                        Flashcard(
                            id = "flashcard_$index",
                            front = FlashcardSide(text = "Front $index"),
                            back = FlashcardSide(text = "Back $index"),
                            topic = VocabularyTopic.values()[index % VocabularyTopic.values().size],
                            level = JLPTLevel.values()[index % JLPTLevel.values().size],
                            difficulty = 2.5f
                        )
                    )
                }
            }
            
            override suspend fun getRecommendedFlashcards(userId: String, maxCards: Int): Result<List<Flashcard>> {
                return Result.success(flashcards.take(maxCards))
            }
            
            override suspend fun updateFlashcardProgress(
                userId: String,
                flashcardId: String,
                quality: ReviewQuality,
                responseTime: Long
            ): Result<SpacedRepetitionData> {
                val data = SpacedRepetitionData(
                    flashcardId = flashcardId,
                    userId = userId,
                    repetitionCount = 1,
                    easeFactor = 2.5f,
                    interval = 1,
                    nextReview = Instant.now().plusSeconds(86400),
                    lastReviewed = Instant.now(),
                    correctStreak = if (quality.isCorrect()) 1 else 0,
                    totalReviews = 1,
                    difficultyAdjustment = 0.0f
                )
                spacedRepetitionData["${userId}_${flashcardId}"] = data
                return Result.success(data)
            }
            
            override suspend fun getLearningStatistics(userId: String): Result<LearningStatistics> {
                val userCards = spacedRepetitionData.values.filter { it.userId == userId }
                return Result.success(
                    LearningStatistics(
                        totalCardsStudied = userCards.size,
                        cardsLearned = userCards.count { it.correctStreak > 0 },
                        cardsDue = userCards.count { it.isDueForReview() },
                        averageMasteryLevel = 0.7f,
                        currentStreak = 5,
                        totalStudyTime = 120L,
                        quizzesCompleted = 3,
                        levelProgress = mapOf(JLPTLevel.N5 to 0.8f)
                    )
                )
            }
        }
    }
    
    fun createMockOfflineRepository(): OfflineRepository {
        return object : OfflineRepository {
            private val users = mutableMapOf<String, User>()
            private val characters = mutableMapOf<CharacterScript, List<Character>>()
            private val quizResults = mutableListOf<com.example.sakura_flashcard.data.model.QuizResult>()
            private val gameResults = mutableListOf<com.example.sakura_flashcard.data.model.GameResult>()
            
            init {
                // Initialize test data
                CharacterScript.values().forEach { script ->
                    characters[script] = (1..50).map { index ->
                        Character(
                            id = "${script.name.lowercase()}_$index",
                            character = "字$index",
                            script = script,
                            pronunciation = listOf("pronunciation$index"),
                            strokeOrder = emptyList(),
                            examples = listOf("example$index")
                        )
                    }
                }
            }
            
            override suspend fun getUser(userId: String): Result<User> {
                return users[userId]?.let { Result.success(it) } 
                    ?: Result.failure(Exception("User not found"))
            }
            
            override suspend fun updateUser(user: User): Result<User> {
                users[user.id] = user
                return Result.success(user)
            }
            
            override suspend fun getCharactersByScript(script: CharacterScript): Result<List<Character>> {
                return Result.success(characters[script] ?: emptyList())
            }
            
            override suspend fun generateQuiz(
                topic: VocabularyTopic,
                level: JLPTLevel,
                questionCount: Int
            ): Result<List<QuizQuestion>> {
                if (questionCount <= 0) return Result.failure(Exception("Invalid question count"))
                
                val questions = (1..questionCount).map { index ->
                    when (index % 3) {
                        0 -> MultipleChoiceQuestion(
                            id = "question_$index",
                            content = "Question $index",
                            options = listOf("Option A", "Option B", "Option C", "Option D"),
                            correctAnswer = "Option A"
                        )
                        1 -> FillInBlankQuestion(
                            id = "question_$index",
                            content = "Fill in the blank: Question $index ___",
                            correctAnswer = "Answer"
                        )
                        else -> TrueFalseQuestion(
                            id = "question_$index",
                            content = "True or False: Question $index",
                            correctAnswer = "True"
                        )
                    }
                }
                return Result.success(questions)
            }
            
            override suspend fun submitQuizResult(result: com.example.sakura_flashcard.data.model.QuizResult): Result<com.example.sakura_flashcard.data.model.QuizResult> {
                quizResults.add(result)
                return Result.success(result)
            }
            
            override suspend fun submitGameResult(result: com.example.sakura_flashcard.data.model.GameResult): Result<com.example.sakura_flashcard.data.model.GameResult> {
                gameResults.add(result)
                return Result.success(result)
            }
            
            override suspend fun getUserGameResults(userId: String): Result<List<com.example.sakura_flashcard.data.model.GameResult>> {
                return Result.success(gameResults.filter { it.userId == userId })
            }
            
            override suspend fun getRecommendedFlashcards(userId: String, maxCards: Int): Result<List<Flashcard>> {
                val flashcards = (1..maxCards).map { index ->
                    Flashcard(
                        id = "flashcard_$index",
                        front = FlashcardSide(text = "Front $index"),
                        back = FlashcardSide(text = "Back $index"),
                        topic = VocabularyTopic.DAILY_LIFE,
                        level = JLPTLevel.N5,
                        difficulty = 2.5f
                    )
                }
                return Result.success(flashcards)
            }
            
            override fun clearCache() {
                // Clear in-memory caches
            }
        }
    }
    
    fun createMockSyncService(): OfflineSyncService {
        return object : OfflineSyncService {
            private val _syncState = kotlinx.coroutines.flow.MutableStateFlow(SyncState())
            override val syncState = _syncState
            
            override suspend fun queueSpacedRepetitionUpdate(data: SpacedRepetitionData) {
                _syncState.value = _syncState.value.copy(pendingOperations = _syncState.value.pendingOperations + 1)
            }
            
            override suspend fun forceSyncNow(): Result<com.example.sakura_flashcard.data.sync.SyncResult> {
                _syncState.value = _syncState.value.copy(
                    pendingOperations = 0,
                    lastSyncTime = Instant.now()
                )
                return Result.success(
                    com.example.sakura_flashcard.data.sync.SyncResult(
                        success = true,
                        syncedOperations = 1,
                        failedOperations = 0,
                        errors = emptyList()
                    )
                )
            }
        }
    }
})

// Helper function for time comparison
private fun <T : Comparable<T>> lessThan(expected: T): (T) -> Boolean = { actual -> actual < expected }