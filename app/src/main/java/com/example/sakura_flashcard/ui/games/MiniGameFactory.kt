package com.example.sakura_flashcard.ui.games

import com.example.sakura_flashcard.data.model.*
import java.util.UUID

/**
 * Factory class for creating mini-game instances based on game type and JLPT level
 */
object MiniGameFactory {
    
    /**
     * Creates a Sentence Order Puzzle game
     */
    fun createSentenceOrderPuzzle(level: JLPTLevel): MiniGame {
        val puzzles = generateSentencePuzzles(level)
        return SentenceOrderPuzzleGame(puzzles, level)
    }
    
    /**
     * Creates a Quick Answer Challenge game
     */
    fun createQuickAnswerChallenge(level: JLPTLevel): MiniGame {
        val questions = generateQuickAnswerQuestions(level)
        return QuickAnswerChallengeGame(questions, level)
    }
    
    /**
     * Creates a Memory Match game
     */
    fun createMemoryMatchGame(level: JLPTLevel): MiniGame {
        val cards = generateMemoryCards(level)
        return MemoryMatchGame(cards, level)
    }
    
    /**
     * Generates sentence puzzles based on JLPT level
     */
    private fun generateSentencePuzzles(level: JLPTLevel): List<SentencePuzzle> {
        return when (level) {
            JLPTLevel.N5 -> listOf(
                SentencePuzzle(
                    words = listOf("私", "は", "学生", "です"),
                    correctOrder = listOf("私", "は", "学生", "です"),
                    translation = "Tôi là học sinh"
                ),
                SentencePuzzle(
                    words = listOf("これ", "は", "本", "です"),
                    correctOrder = listOf("これ", "は", "本", "です"),
                    translation = "Đây là sách"
                ),
                SentencePuzzle(
                    words = listOf("今日", "は", "いい", "天気", "です"),
                    correctOrder = listOf("今日", "は", "いい", "天気", "です"),
                    translation = "Hôm nay thời tiết đẹp"
                ),
                SentencePuzzle(
                    words = listOf("ご飯", "を", "食べます"),
                    correctOrder = listOf("ご飯", "を", "食べます"),
                    translation = "Ăn cơm"
                ),
                SentencePuzzle(
                    words = listOf("日本語", "を", "勉強", "します"),
                    correctOrder = listOf("日本語", "を", "勉強", "します"),
                    translation = "Học tiếng Nhật"
                )
            )
            JLPTLevel.N4 -> listOf(
                SentencePuzzle(
                    words = listOf("明日", "は", "雨", "が", "降る", "かもしれません"),
                    correctOrder = listOf("明日", "は", "雨", "が", "降る", "かもしれません"),
                    translation = "Ngày mai có thể mưa"
                ),
                SentencePuzzle(
                    words = listOf("日本", "に", "行った", "こと", "が", "あります"),
                    correctOrder = listOf("日本", "に", "行った", "こと", "が", "あります"),
                    translation = "Tôi đã từng đến Nhật Bản"
                ),
                SentencePuzzle(
                    words = listOf("彼", "は", "ピアノ", "が", "上手", "です"),
                    correctOrder = listOf("彼", "は", "ピアノ", "が", "上手", "です"),
                    translation = "Anh ấy giỏi piano"
                )
            )
            else -> listOf(
                SentencePuzzle(
                    words = listOf("こんにちは"),
                    correctOrder = listOf("こんにちは"),
                    translation = "Xin chào"
                )
            )
        }
    }
    
    /**
     * Generates quick answer questions based on JLPT level
     */
    private fun generateQuickAnswerQuestions(level: JLPTLevel): List<QuickAnswerQuestion> {
        return when (level) {
            JLPTLevel.N5 -> listOf(
                QuickAnswerQuestion(
                    question = "「おはよう」nghĩa là gì?",
                    options = listOf("Chào buổi sáng", "Chào buổi tối", "Tạm biệt", "Cảm ơn"),
                    correctAnswer = "Chào buổi sáng"
                ),
                QuickAnswerQuestion(
                    question = "「ありがとう」nghĩa là gì?",
                    options = listOf("Xin lỗi", "Cảm ơn", "Xin chào", "Tạm biệt"),
                    correctAnswer = "Cảm ơn"
                ),
                QuickAnswerQuestion(
                    question = "「さようなら」nghĩa là gì?",
                    options = listOf("Xin chào", "Xin lỗi", "Tạm biệt", "Cảm ơn"),
                    correctAnswer = "Tạm biệt"
                ),
                QuickAnswerQuestion(
                    question = "「水」đọc là gì?",
                    options = listOf("みず", "ひ", "き", "かわ"),
                    correctAnswer = "みず"
                ),
                QuickAnswerQuestion(
                    question = "「りんご」viết bằng Kanji là?",
                    options = listOf("林檎", "蜜柑", "桃", "葡萄"),
                    correctAnswer = "林檎"
                )
            )
            JLPTLevel.N4 -> listOf(
                QuickAnswerQuestion(
                    question = "「約束」nghĩa là gì?",
                    options = listOf("Lời hứa", "Cuộc họp", "Bài tập", "Điện thoại"),
                    correctAnswer = "Lời hứa"
                ),
                QuickAnswerQuestion(
                    question = "「連絡する」nghĩa là gì?",
                    options = listOf("Liên lạc", "Nghĩ", "Đợi", "Chạy"),
                    correctAnswer = "Liên lạc"
                ),
                QuickAnswerQuestion(
                    question = "「準備」nghĩa là gì?",
                    options = listOf("Chuẩn bị", "Xong xuôi", "Bắt đầu", "Kết thúc"),
                    correctAnswer = "Chuẩn bị"
                )
            )
            else -> listOf(
                QuickAnswerQuestion(
                    question = "「日本」nghĩa là gì?",
                    options = listOf("Nhật Bản", "Trung Quốc", "Hàn Quốc", "Việt Nam"),
                    correctAnswer = "Nhật Bản"
                )
            )
        }
    }
    
    /**
     * Generates memory cards based on JLPT level
     */
    private fun generateMemoryCards(level: JLPTLevel): List<MemoryCard> {
        val pairs = when (level) {
            JLPTLevel.N5 -> listOf(
                Pair("犬", "Chó"),
                Pair("猫", "Mèo"),
                Pair("水", "Nước"),
                Pair("火", "Lửa"),
                Pair("木", "Cây"),
                Pair("花", "Hoa")
            )
            JLPTLevel.N4 -> listOf(
                Pair("約束", "Lời hứa"),
                Pair("準備", "Chuẩn bị"),
                Pair("連絡", "Liên lạc"),
                Pair("経験", "Kinh nghiệm"),
                Pair("説明", "Giải thích"),
                Pair("質問", "Câu hỏi")
            )
            else -> listOf(
                Pair("日本", "Nhật Bản"),
                Pair("中国", "Trung Quốc"),
                Pair("韓国", "Hàn Quốc")
            )
        }
        
        val cards = mutableListOf<MemoryCard>()
        pairs.forEachIndexed { index, (japanese, vietnamese) ->
            val matchId = UUID.randomUUID().toString()
            cards.add(MemoryCard(content = japanese, matchId = matchId))
            cards.add(MemoryCard(content = vietnamese, matchId = matchId))
        }
        
        return cards.shuffled()
    }
}

/**
 * Implementation of Sentence Order Puzzle game
 */
class SentenceOrderPuzzleGame(
    private val puzzles: List<SentencePuzzle>,
    private val level: JLPTLevel
) : MiniGame {
    
    override val name: String = "Ghép câu tiếng Nhật"
    override val description: String = "Sắp xếp từ để tạo câu đúng"
    override val type: GameType = GameType.SENTENCE_ORDER_PUZZLE
    
    private var currentPuzzleIndex = 0
    private var score = 0
    private var questionsCompleted = 0
    
    override fun startGame(): GameState {
        currentPuzzleIndex = 0
        score = 0
        questionsCompleted = 0
        return GameState.InProgress(
            currentScore = score,
            timeRemaining = null,
            currentQuestion = puzzles.firstOrNull(),
            questionsCompleted = questionsCompleted,
            totalQuestions = puzzles.size
        )
    }
    
    override fun processInput(input: GameInput): GameState {
        if (input !is GameInput.WordOrder) return GameState.NotStarted
        
        val currentPuzzle = puzzles.getOrNull(currentPuzzleIndex) ?: return createCompletedState()
        
        if (input.orderedWords == currentPuzzle.correctOrder) {
            score += 10
        }
        
        questionsCompleted++
        currentPuzzleIndex++
        
        return if (currentPuzzleIndex >= puzzles.size) {
            createCompletedState()
        } else {
            GameState.InProgress(
                currentScore = score,
                timeRemaining = null,
                currentQuestion = puzzles[currentPuzzleIndex],
                questionsCompleted = questionsCompleted,
                totalQuestions = puzzles.size
            )
        }
    }
    
    override fun getMaxScore(): Int = puzzles.size * 10
    override fun getTimeLimit(): Long? = null
    
    private fun createCompletedState(): GameState.Completed {
        val accuracy = if (puzzles.isNotEmpty()) score.toFloat() / getMaxScore() else 0f
        return GameState.Completed(
            finalScore = score,
            totalTime = 0L,
            performance = GamePerformance(
                accuracy = accuracy,
                speed = 0f,
                improvement = 0f
            ),
            questionsCompleted = questionsCompleted,
            totalQuestions = puzzles.size
        )
    }
}

/**
 * Implementation of Quick Answer Challenge game
 */
class QuickAnswerChallengeGame(
    private val questions: List<QuickAnswerQuestion>,
    private val level: JLPTLevel
) : MiniGame {
    
    override val name: String = "Trả lời nhanh"
    override val description: String = "Trả lời câu hỏi trong 10 giây"
    override val type: GameType = GameType.QUICK_ANSWER_CHALLENGE
    
    private var currentQuestionIndex = 0
    private var score = 0
    private var questionsCompleted = 0
    
    override fun startGame(): GameState {
        currentQuestionIndex = 0
        score = 0
        questionsCompleted = 0
        return GameState.InProgress(
            currentScore = score,
            timeRemaining = 10L,
            currentQuestion = questions.firstOrNull(),
            questionsCompleted = questionsCompleted,
            totalQuestions = questions.size
        )
    }
    
    override fun processInput(input: GameInput): GameState {
        if (input !is GameInput.Answer) return GameState.NotStarted
        
        val currentQuestion = questions.getOrNull(currentQuestionIndex) ?: return createCompletedState()
        
        if (input.answer == currentQuestion.correctAnswer) {
            score += 10
        }
        
        questionsCompleted++
        currentQuestionIndex++
        
        return if (currentQuestionIndex >= questions.size) {
            createCompletedState()
        } else {
            GameState.InProgress(
                currentScore = score,
                timeRemaining = 10L,
                currentQuestion = questions[currentQuestionIndex],
                questionsCompleted = questionsCompleted,
                totalQuestions = questions.size
            )
        }
    }
    
    override fun getMaxScore(): Int = questions.size * 10
    override fun getTimeLimit(): Long = 10L
    
    private fun createCompletedState(): GameState.Completed {
        val accuracy = if (questions.isNotEmpty()) score.toFloat() / getMaxScore() else 0f
        return GameState.Completed(
            finalScore = score,
            totalTime = 0L,
            performance = GamePerformance(
                accuracy = accuracy,
                speed = 0f,
                improvement = 0f
            ),
            questionsCompleted = questionsCompleted,
            totalQuestions = questions.size
        )
    }
}

/**
 * Implementation of Memory Match game
 */
class MemoryMatchGame(
    private val cards: List<MemoryCard>,
    private val level: JLPTLevel
) : MiniGame {
    
    override val name: String = "Ghép thẻ nhớ"
    override val description: String = "Tìm cặp thẻ khớp nhau"
    override val type: GameType = GameType.MEMORY_MATCH_GAME
    
    private var score = 0
    private var matchesFound = 0
    private val totalMatches = cards.size / 2
    
    override fun startGame(): GameState {
        score = 0
        matchesFound = 0
        return GameState.InProgress(
            currentScore = score,
            timeRemaining = null,
            currentQuestion = cards,
            questionsCompleted = matchesFound,
            totalQuestions = totalMatches
        )
    }
    
    override fun processInput(input: GameInput): GameState {
        if (input !is GameInput.CardMatch) return GameState.NotStarted
        
        // Check if the cards match (they share the same matchId)
        score += 10
        matchesFound++
        
        return if (matchesFound >= totalMatches) {
            createCompletedState()
        } else {
            GameState.InProgress(
                currentScore = score,
                timeRemaining = null,
                currentQuestion = cards,
                questionsCompleted = matchesFound,
                totalQuestions = totalMatches
            )
        }
    }
    
    override fun getMaxScore(): Int = totalMatches * 10
    override fun getTimeLimit(): Long? = null
    
    private fun createCompletedState(): GameState.Completed {
        val accuracy = if (totalMatches > 0) score.toFloat() / getMaxScore() else 0f
        return GameState.Completed(
            finalScore = score,
            totalTime = 0L,
            performance = GamePerformance(
                accuracy = accuracy,
                speed = 0f,
                improvement = 0f
            ),
            questionsCompleted = matchesFound,
            totalQuestions = totalMatches
        )
    }
}
