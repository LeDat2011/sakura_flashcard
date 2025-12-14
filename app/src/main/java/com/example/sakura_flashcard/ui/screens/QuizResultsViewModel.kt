package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.model.QuizResult
import com.example.sakura_flashcard.data.repository.QuizResultStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizResultsViewModel @Inject constructor(
    private val quizResultStore: QuizResultStore
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizResultsUiState>(QuizResultsUiState.Loading)
    val uiState: StateFlow<QuizResultsUiState> = _uiState.asStateFlow()

    fun loadQuizResult(resultId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = QuizResultsUiState.Loading

                val quizResult = quizResultStore.getResult(resultId)

                if (quizResult != null) {
                    _uiState.value = QuizResultsUiState.Success(
                        quizResult = quizResult,
                        showExplanations = false
                    )
                } else {
                    _uiState.value = QuizResultsUiState.Error("Không tìm thấy kết quả quiz")
                }

            } catch (e: Exception) {
                _uiState.value = QuizResultsUiState.Error("Lỗi tải kết quả: ${e.message}")
            }
        }
    }

    fun toggleExplanations() {
        val currentState = _uiState.value
        if (currentState is QuizResultsUiState.Success) {
            _uiState.value = currentState.copy(
                showExplanations = !currentState.showExplanations
            )
        }
    }
}

sealed class QuizResultsUiState {
    object Loading : QuizResultsUiState()
    data class Error(val message: String) : QuizResultsUiState()
    data class Success(
        val quizResult: QuizResult,
        val showExplanations: Boolean = false
    ) : QuizResultsUiState()
}