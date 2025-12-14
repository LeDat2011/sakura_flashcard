package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.VocabularyData
import com.example.sakura_flashcard.data.api.toFlashcard
import com.example.sakura_flashcard.data.repository.VocabularyRepository
import com.example.sakura_flashcard.data.model.Flashcard
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.data.model.VocabularyTopic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlashcardDeckUiState(
    val flashcards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val learnedCards: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val topic: VocabularyTopic? = null,
    val level: JLPTLevel? = null,
    val error: String? = null
)

@HiltViewModel
class FlashcardDeckViewModel @Inject constructor(
    private val vocabularyRepository: VocabularyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FlashcardDeckUiState())
    val uiState: StateFlow<FlashcardDeckUiState> = _uiState.asStateFlow()
    
    fun loadFlashcards(topicName: String, levelName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val topic = VocabularyTopic.valueOf(topicName)
                val level = JLPTLevel.valueOf(levelName)
                
                // Gọi API thay vì dùng local data
                vocabularyRepository.getVocabularies(
                    topic = topicName,
                    level = levelName,
                    page = 1,
                    limit = 100
                ).onSuccess { response ->
                    val flashcards = response.data.map { it.toFlashcard() }
                    _uiState.value = _uiState.value.copy(
                        flashcards = flashcards,
                        topic = topic,
                        level = level,
                        isLoading = false,
                        currentIndex = 0,
                        isFlipped = false
                    )
                }.onFailure { error ->
                    // Fallback to local data if API fails
                    val flashcards = VocabularyData.getFlashcards(topic, level)
                    _uiState.value = _uiState.value.copy(
                        flashcards = flashcards,
                        topic = topic,
                        level = level,
                        isLoading = false,
                        currentIndex = 0,
                        isFlipped = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load flashcards: ${e.message}"
                )
            }
        }
    }

    fun flipCard() {
        _uiState.value = _uiState.value.copy(isFlipped = !_uiState.value.isFlipped)
    }
    
    fun nextCard() {
        val currentState = _uiState.value
        if (currentState.currentIndex < currentState.flashcards.size - 1) {
            _uiState.value = currentState.copy(
                currentIndex = currentState.currentIndex + 1,
                isFlipped = false
            )
        }
    }
    
    fun previousCard() {
        val currentState = _uiState.value
        if (currentState.currentIndex > 0) {
            _uiState.value = currentState.copy(
                currentIndex = currentState.currentIndex - 1,
                isFlipped = false
            )
        }
    }
    
    fun markAsLearned(flashcardId: String) {
        val currentLearned = _uiState.value.learnedCards.toMutableSet()
        currentLearned.add(flashcardId)
        _uiState.value = _uiState.value.copy(learnedCards = currentLearned)
    }
    
    fun markAsNotLearned(flashcardId: String) {
        val currentLearned = _uiState.value.learnedCards.toMutableSet()
        currentLearned.remove(flashcardId)
        _uiState.value = _uiState.value.copy(learnedCards = currentLearned)
    }
    
    fun goToCard(index: Int) {
        if (index in 0 until _uiState.value.flashcards.size) {
            _uiState.value = _uiState.value.copy(
                currentIndex = index,
                isFlipped = false
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
