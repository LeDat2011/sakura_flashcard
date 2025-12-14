package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.api.*
import com.example.sakura_flashcard.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizListViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizListUiState())
    val uiState: StateFlow<QuizListUiState> = _uiState.asStateFlow()

    init {
        loadQuizTopics()
    }

    fun loadQuizTopics(level: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTopics = true)

            quizRepository.getQuizTopics(level)
                .onSuccess { topics ->
                    _uiState.value = _uiState.value.copy(
                        topics = topics,
                        isLoadingTopics = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingTopics = false,
                        error = error.message
                    )
                }
        }
    }

    fun loadQuizSets(topic: String? = null, level: String? = null, page: Int = 1) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            quizRepository.getQuizSets(topic, level, page)
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        quizSets = if (page == 1) response.data else _uiState.value.quizSets + response.data,
                        pagination = response.pagination,
                        currentPage = page,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun loadQuizHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingHistory = true)

            quizRepository.getQuizHistory()
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        quizHistory = response.data,
                        isLoadingHistory = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingHistory = false,
                        error = error.message
                    )
                }
        }
    }

    fun selectTopic(topic: String?) {
        _uiState.value = _uiState.value.copy(
            selectedTopic = topic,
            quizSets = emptyList(),
            currentPage = 1
        )
        loadQuizSets(topic = topic, level = _uiState.value.selectedLevel)
    }

    fun selectLevel(level: String?) {
        _uiState.value = _uiState.value.copy(
            selectedLevel = level,
            quizSets = emptyList(),
            currentPage = 1
        )
        loadQuizTopics(level)
        loadQuizSets(topic = _uiState.value.selectedTopic, level = level)
    }

    fun loadMoreQuizSets() {
        val pagination = _uiState.value.pagination ?: return
        if (!pagination.hasNext || _uiState.value.isLoading) return

        loadQuizSets(
            topic = _uiState.value.selectedTopic,
            level = _uiState.value.selectedLevel,
            page = _uiState.value.currentPage + 1
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class QuizListUiState(
    val topics: List<QuizTopicInfo> = emptyList(),
    val quizSets: List<QuizSetDto> = emptyList(),
    val quizHistory: List<QuizAttemptDto> = emptyList(),
    val pagination: Pagination? = null,
    val currentPage: Int = 1,
    val selectedTopic: String? = null,
    val selectedLevel: String? = null,
    val isLoading: Boolean = false,
    val isLoadingTopics: Boolean = false,
    val isLoadingHistory: Boolean = false,
    val error: String? = null
)
