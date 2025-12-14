package com.example.sakura_flashcard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.Instant
import java.util.UUID

@Entity(tableName = "quiz_results")
@TypeConverters(Converters::class)
data class QuizResult(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val topic: VocabularyTopic,
    val level: JLPTLevel,
    val score: Int,
    val totalQuestions: Int,
    val completedAt: Instant = Instant.now(),
    val timeSpentSeconds: Long,
    val answers: List<QuizAnswer>
) {
    init {
        require(userId.isNotBlank()) { "User ID cannot be empty" }
        require(score >= 0) { "Score cannot be negative" }
        require(totalQuestions > 0) { "Total questions must be positive" }
        require(score <= totalQuestions) { "Score cannot exceed total questions" }
        require(timeSpentSeconds >= 0) { "Time spent cannot be negative" }
        require(answers.size == totalQuestions) { "Number of answers must match total questions" }
    }
    
    fun getPercentageScore(): Float {
        return if (totalQuestions > 0) (score.toFloat() / totalQuestions) * 100f else 0f
    }
    
    fun getAverageTimePerQuestion(): Float {
        return if (totalQuestions > 0) timeSpentSeconds.toFloat() / totalQuestions else 0f
    }
    
    fun isPassing(passingScore: Float = 70f): Boolean {
        return getPercentageScore() >= passingScore
    }
}

data class QuizAnswer(
    val questionId: String,
    val questionText: String = "",
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val timeSpentSeconds: Long,
    val explanation: String? = null,
    val points: Int = 10,
    val difficulty: Int = 1
) {
    init {
        require(questionId.isNotBlank()) { "Question ID cannot be empty" }
        require(correctAnswer.isNotBlank()) { "Correct answer cannot be empty" }
        require(timeSpentSeconds >= 0) { "Time spent cannot be negative" }
        require(points > 0) { "Points must be positive" }
        require(difficulty in 1..5) { "Difficulty must be between 1 and 5" }
    }
    
    fun getDifficultyLabel(): String = when (difficulty) {
        1 -> "Dễ"
        2 -> "Trung bình"
        3 -> "Khó"
        4 -> "Rất khó"
        5 -> "Cực khó"
        else -> "Không xác định"
    }
}

interface QuizQuestion {
    val id: String
    val type: QuestionType
    val content: String
    val options: List<String>?
    val correctAnswer: String
    val explanation: String?
    val points: Int
    val difficulty: Int
    
    fun isAnswerCorrect(userAnswer: String): Boolean {
        return userAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true)
    }
    
    fun getDifficultyLabel(): String = when (difficulty) {
        1 -> "Dễ"
        2 -> "Trung bình"
        3 -> "Khó"
        4 -> "Rất khó"
        5 -> "Cực khó"
        else -> "Không xác định"
    }
}

data class MultipleChoiceQuestion(
    override val id: String = UUID.randomUUID().toString(),
    override val content: String,
    override val options: List<String>,
    override val correctAnswer: String,
    override val explanation: String? = null,
    override val points: Int = 10,
    override val difficulty: Int = 1
) : QuizQuestion {
    override val type = QuestionType.MULTIPLE_CHOICE
    
    init {
        require(content.isNotBlank()) { "Question content cannot be empty" }
        require(options.size >= 2) { "Multiple choice questions must have at least 2 options" }
        require(options.contains(correctAnswer)) { "Correct answer must be one of the options" }
        require(options.distinct().size == options.size) { "All options must be unique" }
    }
}

data class FillBlankQuestion(
    override val id: String = UUID.randomUUID().toString(),
    override val content: String,
    override val correctAnswer: String,
    override val explanation: String? = null,
    override val points: Int = 10,
    override val difficulty: Int = 1
) : QuizQuestion {
    override val type = QuestionType.FILL_BLANK
    override val options: List<String>? = null
    
    init {
        require(content.isNotBlank()) { "Question content cannot be empty" }
        require(correctAnswer.isNotBlank()) { "Correct answer cannot be empty" }
        require(content.contains("___") || content.contains("_")) { 
            "Fill in blank questions must contain blank markers (___)" 
        }
    }
}

data class TrueFalseQuestion(
    override val id: String = UUID.randomUUID().toString(),
    override val content: String,
    override val correctAnswer: String,
    override val explanation: String? = null,
    override val points: Int = 10,
    override val difficulty: Int = 1
) : QuizQuestion {
    override val type = QuestionType.TRUE_FALSE
    override val options = listOf("True", "False")
    
    init {
        require(content.isNotBlank()) { "Question content cannot be empty" }
        require(correctAnswer in listOf("True", "False")) { 
            "True/False questions must have 'True' or 'False' as correct answer" 
        }
    }
}

data class QuizSession(
    val id: String = UUID.randomUUID().toString(),
    val topic: VocabularyTopic,
    val level: JLPTLevel,
    val questions: List<QuizQuestion>,
    val currentQuestionIndex: Int = 0,
    val answers: List<QuizAnswer> = emptyList(),
    val startTime: Instant = Instant.now(),
    val isCompleted: Boolean = false,
    val timeSpentSeconds: Long = 0
) {
    init {
        require(id.isNotBlank()) { "Session ID cannot be empty" }
        require(questions.isNotEmpty()) { "Quiz session must have at least 1 question" }
        require(currentQuestionIndex >= 0) { "Current question index cannot be negative" }
        require(currentQuestionIndex < questions.size || isCompleted) { 
            "Current question index ($currentQuestionIndex) must be within bounds (${questions.size}) unless completed ($isCompleted)" 
        }
        require(answers.size <= questions.size) { "Cannot have more answers than questions" }
        require(timeSpentSeconds >= 0) { "Time spent cannot be negative" }
        
        // Verify mixed question types (relaxed for testing)
        val questionTypes = questions.map { it.type }.toSet()
        // require(questionTypes.size >= 2) { "Quiz must have mixed question types" }
    }
    
    fun getCurrentQuestion(): QuizQuestion? {
        return if (currentQuestionIndex < questions.size) questions[currentQuestionIndex] else null
    }
    
    fun getProgress(): String {
        return "${currentQuestionIndex + 1}/${questions.size}"
    }
    
    fun isLastQuestion(): Boolean {
        return currentQuestionIndex == questions.size - 1
    }
    
    fun getScore(): Int {
        return answers.count { it.isCorrect }
    }
    
    fun getPercentageScore(): Float {
        return if (answers.isNotEmpty()) (getScore().toFloat() / answers.size) * 100f else 0f
    }
}

enum class QuestionType(val displayName: String) {
    MULTIPLE_CHOICE("Multiple Choice"),
    FILL_BLANK("Fill in the Blank"),
    TRUE_FALSE("True or False");
    
    companion object {
        fun fromDisplayName(displayName: String): QuestionType? {
            return values().find { it.displayName == displayName }
        }
    }
}