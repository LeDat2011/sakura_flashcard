package com.example.sakura_flashcard.data.repository

import com.example.sakura_flashcard.data.model.QuizResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizResultStore @Inject constructor() {
    private val results = mutableMapOf<String, QuizResult>()

    fun saveResult(result: QuizResult) {
        results[result.id] = result
    }

    fun getResult(resultId: String): QuizResult? {
        return results[resultId]
    }

    fun clearResult(resultId: String) {
        results.remove(resultId)
    }

    fun clearAll() {
        results.clear()
    }
}
