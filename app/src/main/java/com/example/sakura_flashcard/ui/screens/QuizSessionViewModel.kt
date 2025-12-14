package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.api.QuizQuestionDto
import com.example.sakura_flashcard.data.api.QuizSetDto
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.data.model.MultipleChoiceQuestion
import com.example.sakura_flashcard.data.model.QuizAnswer
import com.example.sakura_flashcard.data.model.QuizResult
import com.example.sakura_flashcard.data.model.QuizSession
import com.example.sakura_flashcard.data.model.VocabularyTopic
import com.example.sakura_flashcard.data.repository.QuizRepository
import com.example.sakura_flashcard.data.repository.QuizResultStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class QuizSessionViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val quizResultStore: QuizResultStore
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizSessionUiState>(QuizSessionUiState.Loading)
    val uiState: StateFlow<QuizSessionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var currentQuizSession: QuizSession? = null
    private val userAnswers = mutableListOf<QuizAnswer>()
    private var quizTimeLimit: Int = 600
    private var currentTopic: String = ""
    private var currentLevel: String = ""

    private fun getQuestionId(question: QuizQuestionDto): String {
        return question.id ?: question.questionNumber?.toString() ?: UUID.randomUUID().toString()
    }

    fun startQuiz(topic: String, level: String) {
        currentTopic = topic
        currentLevel = level

        viewModelScope.launch {
            try {
                _uiState.value = QuizSessionUiState.Loading

                val result = quizRepository.getQuizSets(topic = topic, level = level, limit = 1)

                result.onSuccess { response ->
                    val quizSetDto = response.data.firstOrNull()
                    if (quizSetDto != null) {
                        val questions = quizSetDto.questions.map { q ->
                            val qId = getQuestionId(q)

                            // Lấy text của đáp án đúng dựa trên correctAnswer (A, B, C, D)
                            val correctOptionText = q.options.find { it.optionId == q.correctAnswer }?.text
                                ?: q.options.find { it.isCorrect }?.text
                                ?: q.correctAnswer
                                ?: ""

                            MultipleChoiceQuestion(
                                id = qId,
                                content = q.questionText,
                                options = q.options.map { it.text },
                                correctAnswer = correctOptionText,
                                explanation = q.explanation
                            )
                        }

                        val quizSession = QuizSession(
                            topic = try { VocabularyTopic.valueOf(topic.uppercase()) } catch (e: Exception) { VocabularyTopic.DAILY_LIFE },
                            level = try { JLPTLevel.valueOf(level.uppercase()) } catch (e: Exception) { JLPTLevel.N5 },
                            questions = questions,
                            currentQuestionIndex = 0,
                            answers = emptyList(),
                            startTime = Instant.now(),
                            isCompleted = false
                        )

                        currentQuizSession = quizSession
                        userAnswers.clear()

                        quizTimeLimit = quizSetDto.settings?.timeLimit ?: 600
                        _uiState.value = QuizSessionUiState.InProgress(
                            quizSession = quizSession,
                            currentAnswer = "",
                            timeRemainingSeconds = quizTimeLimit
                        )
                        startTimer(quizTimeLimit)
                    } else {
                        _uiState.value = QuizSessionUiState.Error("Không tìm thấy quiz cho chủ đề này")
                    }
                }.onFailure { e ->
                    _uiState.value = QuizSessionUiState.Error(e.message ?: "Lỗi tải quiz")
                }
            } catch (e: Exception) {
                _uiState.value = QuizSessionUiState.Error(e.message ?: "Đã xảy ra lỗi")
            }
        }
    }

    fun selectAnswer(answer: String) {
        val currentState = _uiState.value
        if (currentState is QuizSessionUiState.InProgress) {
            _uiState.value = currentState.copy(currentAnswer = answer)
        }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        if (currentState is QuizSessionUiState.InProgress) {
            val quizSession = currentState.quizSession
            val currentQuestion = quizSession.getCurrentQuestion()

            if (currentQuestion != null) {
                val isCorrect = currentQuestion.isAnswerCorrect(currentState.currentAnswer)

                val answer = QuizAnswer(
                    questionId = currentQuestion.id,
                    userAnswer = currentState.currentAnswer,
                    correctAnswer = currentQuestion.correctAnswer,
                    isCorrect = isCorrect,
                    timeSpentSeconds = 0L
                )

                userAnswers.add(answer)

                if (quizSession.isLastQuestion()) {
                    completeQuiz()
                } else {
                    val nextQuizSession = quizSession.copy(
                        currentQuestionIndex = quizSession.currentQuestionIndex + 1,
                        answers = userAnswers.toList()
                    )

                    currentQuizSession = nextQuizSession

                    val nextQuestion = nextQuizSession.getCurrentQuestion()
                    val existingAnswer = userAnswers.find { it.questionId == nextQuestion?.id }

                    _uiState.value = currentState.copy(
                        quizSession = nextQuizSession,
                        currentAnswer = existingAnswer?.userAnswer ?: ""
                    )
                }
            }
        }
    }

    private fun completeQuiz() {
        val currentState = _uiState.value
        if (currentState is QuizSessionUiState.InProgress) {
            val quizSession = currentState.quizSession
            val score = userAnswers.count { it.isCorrect }
            val totalTime = (quizTimeLimit - currentState.timeRemainingSeconds).toLong()

            val quizResult = QuizResult(
                id = UUID.randomUUID().toString(),
                userId = "current-user",
                topic = quizSession.topic,
                level = quizSession.level,
                score = score,
                totalQuestions = quizSession.questions.size,
                timeSpentSeconds = totalTime,
                answers = userAnswers.toList()
            )

            // Lưu kết quả vào store
            quizResultStore.saveResult(quizResult)

            timerJob?.cancel()
            _uiState.value = QuizSessionUiState.Completed(quizResult)
        }
    }

    private fun startTimer(initialTime: Int = 600) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var timeRemaining = initialTime

            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--

                val currentState = _uiState.value
                if (currentState is QuizSessionUiState.InProgress) {
                    _uiState.value = currentState.copy(timeRemainingSeconds = timeRemaining)
                } else {
                    break
                }
            }

            if (timeRemaining <= 0) {
                completeQuiz()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}