package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    
    fun selectTopic(topic: VocabularyTopic) {
        _uiState.value = _uiState.value.copy(
            selectedTopic = topic,
            selectedLevel = null
        )
    }
    
    fun selectLevel(level: JLPTLevel) {
        _uiState.value = _uiState.value.copy(
            selectedLevel = level
        )
    }
    
    fun startQuiz(topic: VocabularyTopic, level: JLPTLevel) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            try {
                // In a real app, this would call the backend API
                // For now, we'll simulate the API call
                val questions = generateSampleQuestions(topic, level)
                val quizSession = QuizSession(
                    topic = topic,
                    level = level,
                    questions = questions
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentQuizSession = quizSession,
                    isQuizStarted = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to start quiz: ${e.message}"
                )
            }
        }
    }
    
    fun resetQuiz() {
        _uiState.value = QuizUiState()
    }
    
    fun goBackToTopics() {
        _uiState.value = _uiState.value.copy(
            selectedTopic = null,
            selectedLevel = null
        )
    }
    
    fun goBackToLevels() {
        _uiState.value = _uiState.value.copy(
            selectedLevel = null
        )
    }
    
    private fun generateSampleQuestions(topic: VocabularyTopic, level: JLPTLevel): List<QuizQuestion> {
        // This is a placeholder implementation
        // In a real app, this would fetch questions from the backend
        return listOf(
            MultipleChoiceQuestion(
                content = "What does 'こんにちは' mean?",
                options = listOf("Hello", "Goodbye", "Thank you", "Excuse me"),
                correctAnswer = "Hello",
                explanation = "'こんにちは' is a common Japanese greeting meaning 'Hello'"
            ),
            FillBlankQuestion(
                content = "Fill in the blank: _____ は学生です。(I am a student)",
                correctAnswer = "私",
                explanation = "'私' (watashi) means 'I' in Japanese"
            ),
            TrueFalseQuestion(
                content = "'ありがとう' means 'thank you' in Japanese",
                correctAnswer = "True",
                explanation = "'ありがとう' (arigatou) is indeed 'thank you' in Japanese"
            ),
            MultipleChoiceQuestion(
                content = "Which hiragana represents the sound 'ka'?",
                options = listOf("か", "き", "く", "け"),
                correctAnswer = "か",
                explanation = "'か' represents the 'ka' sound in hiragana"
            ),
            FillBlankQuestion(
                content = "Complete the sentence: 今日は___です。(Today is ___)",
                correctAnswer = "月曜日",
                explanation = "This could be any day of the week, '月曜日' (getsuyoubi) means Monday"
            ),
            TrueFalseQuestion(
                content = "Japanese has three writing systems: hiragana, katakana, and kanji",
                correctAnswer = "True",
                explanation = "Japanese indeed uses three writing systems"
            ),
            MultipleChoiceQuestion(
                content = "What is the Japanese word for 'water'?",
                options = listOf("水", "火", "土", "風"),
                correctAnswer = "水",
                explanation = "'水' (mizu) means water in Japanese"
            ),
            FillBlankQuestion(
                content = "The polite form of 'to eat' is ___ます。",
                correctAnswer = "食べ",
                explanation = "'食べます' (tabemasu) is the polite form of 'to eat'"
            ),
            TrueFalseQuestion(
                content = "In Japanese, the verb usually comes at the end of the sentence",
                correctAnswer = "True",
                explanation = "Japanese follows a Subject-Object-Verb (SOV) word order"
            ),
            MultipleChoiceQuestion(
                content = "How do you say 'good morning' in Japanese?",
                options = listOf("おはよう", "こんばんは", "さようなら", "すみません"),
                correctAnswer = "おはよう",
                explanation = "'おはよう' (ohayou) means 'good morning' in Japanese"
            )
        )
    }
}

data class QuizUiState(
    val selectedTopic: VocabularyTopic? = null,
    val selectedLevel: JLPTLevel? = null,
    val currentQuizSession: QuizSession? = null,
    val isQuizStarted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val availableTopics: List<VocabularyTopic> = VocabularyTopic.values().toList()
    val availableLevels: List<JLPTLevel> = JLPTLevel.values().toList()
}