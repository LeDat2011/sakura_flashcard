package com.example.sakura_flashcard.navigation

sealed class Screen(val route: String) {
    // Authentication screens
    object Login : Screen("login")
    object Register : Screen("register")
    object Onboarding : Screen("onboarding")
    
    // Main app screens
    object Home : Screen("home")
    object Learn : Screen("learn")
    object Quiz : Screen("quiz")
    object Game : Screen("game")
    object Profile : Screen("profile")
    
    // Sub-screens
    object CharacterDetail : Screen("character_detail/{characterId}") {
        fun createRoute(characterId: String) = "character_detail/$characterId"
    }
    object FlashcardDeck : Screen("flashcard_deck/{topic}/{level}") {
        fun createRoute(topic: String, level: String) = "flashcard_deck/$topic/$level"
    }
    object QuizSession : Screen("quiz_session/{topic}/{level}") {
        fun createRoute(topic: String, level: String) = "quiz_session/$topic/$level"
    }
    object QuizResults : Screen("quiz_results/{resultId}") {
        fun createRoute(resultId: String) = "quiz_results/$resultId"
    }
    object GameSession : Screen("game_session/{gameType}") {
        fun createRoute(gameType: String) = "game_session/$gameType"
    }
    object ProfileEdit : Screen("profile_edit")
    object CustomDeckManager : Screen("custom_deck_manager")
    object CustomDeckFlashcard : Screen("custom_deck_flashcard/{deckId}") {
        fun createRoute(deckId: String) = "custom_deck_flashcard/$deckId"
    }
}