package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.JapaneseCharacterData
import com.example.sakura_flashcard.data.model.Character
import com.example.sakura_flashcard.data.model.CharacterScript
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearnUiState(
    val hiraganaCharacters: List<Character> = emptyList(),
    val katakanaCharacters: List<Character> = emptyList(),
    val kanjiCharacters: List<Character> = emptyList(),
    val filteredHiraganaCharacters: List<Character> = emptyList(),
    val filteredKatakanaCharacters: List<Character> = emptyList(),
    val filteredKanjiCharacters: List<Character> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedScript: CharacterScript = CharacterScript.HIRAGANA
)

@HiltViewModel
class LearnViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(LearnUiState())
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()
    
    private var isDataLoaded = false
    
    init {
        loadCharacterData()
    }
    
    fun loadCharacterData() {
        if (isDataLoaded) return
        
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            val hiraganaList = JapaneseCharacterData.hiraganaCharacters
            val katakanaList = JapaneseCharacterData.katakanaCharacters
            val kanjiList = JapaneseCharacterData.kanjiCharacters
            
            _uiState.update {
                it.copy(
                    hiraganaCharacters = hiraganaList,
                    katakanaCharacters = katakanaList,
                    kanjiCharacters = kanjiList,
                    filteredHiraganaCharacters = hiraganaList,
                    filteredKatakanaCharacters = katakanaList,
                    filteredKanjiCharacters = kanjiList,
                    isLoading = false
                )
            }
            
            isDataLoaded = true
        }
    }
    
    fun searchCharacters(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        if (query.isBlank()) {
            // Reset to show all characters
            _uiState.value = _uiState.value.copy(
                filteredHiraganaCharacters = _uiState.value.hiraganaCharacters,
                filteredKatakanaCharacters = _uiState.value.katakanaCharacters,
                filteredKanjiCharacters = _uiState.value.kanjiCharacters
            )
            return
        }
        
        // Search locally first for immediate feedback
        val currentState = _uiState.value
        val filteredHiragana = currentState.hiraganaCharacters.filter { character ->
            character.character.contains(query, ignoreCase = true) ||
            character.pronunciation.any { it.contains(query, ignoreCase = true) } ||
            character.examples.any { it.contains(query, ignoreCase = true) }
        }
        
        val filteredKatakana = currentState.katakanaCharacters.filter { character ->
            character.character.contains(query, ignoreCase = true) ||
            character.pronunciation.any { it.contains(query, ignoreCase = true) } ||
            character.examples.any { it.contains(query, ignoreCase = true) }
        }
        
        val filteredKanji = currentState.kanjiCharacters.filter { character ->
            character.character.contains(query, ignoreCase = true) ||
            character.pronunciation.any { it.contains(query, ignoreCase = true) } ||
            character.examples.any { it.contains(query, ignoreCase = true) }
        }
        
        _uiState.value = _uiState.value.copy(
            filteredHiraganaCharacters = filteredHiragana,
            filteredKatakanaCharacters = filteredKatakana,
            filteredKanjiCharacters = filteredKanji
        )
        
        // Local search is already performed above
    }
    
    private fun performLocalSearch(query: String) {
        val allCharacters = JapaneseCharacterData.hiraganaCharacters +
                            JapaneseCharacterData.katakanaCharacters +
                            JapaneseCharacterData.kanjiCharacters
        
        val results = allCharacters.filter { char ->
            char.character.contains(query, ignoreCase = true) ||
            char.pronunciation.any { it.contains(query, ignoreCase = true) }
        }
        
        // Group results by script
        val hiraganaResults = results.filter { it.script == CharacterScript.HIRAGANA }
        val katakanaResults = results.filter { it.script == CharacterScript.KATAKANA }
        val kanjiResults = results.filter { it.script == CharacterScript.KANJI }
        
        _uiState.update {
            it.copy(
                filteredHiraganaCharacters = hiraganaResults,
                filteredKatakanaCharacters = katakanaResults,
                filteredKanjiCharacters = kanjiResults
            )
        }
    }
    
    fun selectScript(script: CharacterScript) {
        _uiState.value = _uiState.value.copy(selectedScript = script)
    }
    
    fun getCharactersForScript(script: CharacterScript): List<Character> {
        return when (script) {
            CharacterScript.HIRAGANA -> _uiState.value.filteredHiraganaCharacters
            CharacterScript.KATAKANA -> _uiState.value.filteredKatakanaCharacters
            CharacterScript.KANJI -> _uiState.value.filteredKanjiCharacters
        }
    }
    
    fun refreshData() {
        isDataLoaded = false
        loadCharacterData()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSearch() {
        searchCharacters("")
    }
}