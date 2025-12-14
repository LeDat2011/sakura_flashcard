package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.api.*
import com.example.sakura_flashcard.data.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val vocabularyRepository: VocabularyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyUiState())
    val uiState: StateFlow<VocabularyUiState> = _uiState.asStateFlow()

    private val _dueVocabulary = MutableStateFlow<List<VocabularyProgressDto>>(emptyList())
    val dueVocabulary: StateFlow<List<VocabularyProgressDto>> = _dueVocabulary.asStateFlow()

    init {
        loadTopics()
        loadStats()
    }

    fun loadTopics(level: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTopics = true)

            vocabularyRepository.getTopics(level)
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

    fun loadVocabularies(topic: String? = null, level: String? = null, page: Int = 1) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            vocabularyRepository.getVocabularies(topic, level, page)
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        vocabularies = if (page == 1) response.data else _uiState.value.vocabularies + response.data,
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

    fun loadMoreVocabularies() {
        val pagination = _uiState.value.pagination ?: return
        if (!pagination.hasNext || _uiState.value.isLoading) return

        loadVocabularies(
            topic = _uiState.value.selectedTopic,
            level = _uiState.value.selectedLevel,
            page = _uiState.value.currentPage + 1
        )
    }

    fun searchVocabulary(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)

            vocabularyRepository.searchVocabulary(query, _uiState.value.selectedLevel)
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results,
                        isSearching = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        error = error.message
                    )
                }
        }
    }

    fun loadDueVocabulary() {
        viewModelScope.launch {
            vocabularyRepository.getDueVocabulary()
                .onSuccess { dueItems ->
                    _dueVocabulary.value = dueItems
                }
        }
    }

    fun updateProgress(vocabularyId: String, quality: Int) {
        viewModelScope.launch {
            vocabularyRepository.updateProgress(vocabularyId, quality)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        xpEarned = (_uiState.value.xpEarned ?: 0) + result.xpEarned
                    )
                    // Reload due vocabulary
                    loadDueVocabulary()
                    loadStats()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun loadStats(topic: String? = null, level: String? = null) {
        viewModelScope.launch {
            vocabularyRepository.getStats(topic, level)
                .onSuccess { stats ->
                    _uiState.value = _uiState.value.copy(stats = stats)
                }
        }
    }

    fun selectTopic(topic: String?) {
        _uiState.value = _uiState.value.copy(
            selectedTopic = topic,
            vocabularies = emptyList(),
            currentPage = 1
        )
        loadVocabularies(topic = topic, level = _uiState.value.selectedLevel)
    }

    fun selectLevel(level: String?) {
        _uiState.value = _uiState.value.copy(
            selectedLevel = level,
            vocabularies = emptyList(),
            currentPage = 1
        )
        loadTopics(level)
        loadVocabularies(topic = _uiState.value.selectedTopic, level = level)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearXpEarned() {
        _uiState.value = _uiState.value.copy(xpEarned = null)
    }
}

data class VocabularyUiState(
    val topics: List<TopicInfo> = emptyList(),
    val vocabularies: List<VocabularyDto> = emptyList(),
    val searchResults: List<VocabularyDto> = emptyList(),
    val stats: VocabularyStatsDto? = null,
    val pagination: Pagination? = null,
    val currentPage: Int = 1,
    val selectedTopic: String? = null,
    val selectedLevel: String? = null,
    val isLoading: Boolean = false,
    val isLoadingTopics: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val xpEarned: Int? = null
)
