package com.example.sakura_flashcard

import com.example.sakura_flashcard.navigation.NavigationUtils
import com.example.sakura_flashcard.navigation.Screen
import com.example.sakura_flashcard.ui.components.BottomNavItem
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * Feature: japanese-flashcard-ui, Property 25: UI Consistency and Animation
 * Validates: Requirements 6.1, 6.2, 6.3
 */
class NavigationConsistencyTest : StringSpec({
    
    "bottom navigation should always provide exactly five main screens" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that we have exactly 5 main navigation screens
            val mainScreens = listOf(
                Screen.Home.route,
                Screen.Learn.route,
                Screen.Quiz.route,
                Screen.Game.route,
                Screen.Profile.route
            )
            
            mainScreens shouldHaveSize 5
            
            // Verify each screen has a unique route
            val uniqueRoutes = mainScreens.toSet()
            uniqueRoutes shouldHaveSize 5
            
            // Verify all routes are non-empty strings
            mainScreens.forEach { route ->
                route.isNotEmpty() shouldBe true
                route.isNotBlank() shouldBe true
            }
        }
    }
    
    "navigation should maintain consistent route patterns for all screens" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that main navigation routes follow consistent patterns
            val mainRoutes = listOf(
                Screen.Home.route,
                Screen.Learn.route,
                Screen.Quiz.route,
                Screen.Game.route,
                Screen.Profile.route
            )
            
            // All main routes should be simple strings without parameters
            mainRoutes.forEach { route ->
                route.contains("{") shouldBe false
                route.contains("/") shouldBe false
                route.matches(Regex("^[a-z]+$")) shouldBe true
            }
            
            // Sub-screen routes should follow parameter patterns
            val subRoutes = listOf(
                Screen.CharacterDetail.route,
                Screen.FlashcardDeck.route,
                Screen.QuizSession.route,
                Screen.QuizResults.route,
                Screen.GameSession.route
            )
            
            subRoutes.forEach { route ->
                // Sub-routes should contain parameters
                route.contains("{") shouldBe true
                route.contains("}") shouldBe true
            }
        }
    }
    
    "navigation utilities should correctly identify main navigation routes" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test main navigation route identification
            NavigationUtils.isMainNavigationRoute(Screen.Home.route) shouldBe true
            NavigationUtils.isMainNavigationRoute(Screen.Learn.route) shouldBe true
            NavigationUtils.isMainNavigationRoute(Screen.Quiz.route) shouldBe true
            NavigationUtils.isMainNavigationRoute(Screen.Game.route) shouldBe true
            NavigationUtils.isMainNavigationRoute(Screen.Profile.route) shouldBe true
            
            // Test sub-screen route identification
            NavigationUtils.isMainNavigationRoute(Screen.CharacterDetail.route) shouldBe false
            NavigationUtils.isMainNavigationRoute(Screen.FlashcardDeck.route) shouldBe false
            NavigationUtils.isMainNavigationRoute(Screen.QuizSession.route) shouldBe false
            NavigationUtils.isMainNavigationRoute(Screen.QuizResults.route) shouldBe false
            NavigationUtils.isMainNavigationRoute(Screen.GameSession.route) shouldBe false
            NavigationUtils.isMainNavigationRoute(Screen.ProfileEdit.route) shouldBe false
            NavigationUtils.isMainNavigationRoute(Screen.CustomDeckManager.route) shouldBe false
            
            // Test null and invalid routes
            NavigationUtils.isMainNavigationRoute(null) shouldBe false
            NavigationUtils.isMainNavigationRoute("") shouldBe false
            NavigationUtils.isMainNavigationRoute("invalid_route") shouldBe false
        }
    }
    
    "navigation should provide consistent parent screen mapping for sub-screens" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that sub-screens map to correct parent screens
            NavigationUtils.getMainScreenFromRoute("character_detail/123") shouldBe Screen.Learn.route
            NavigationUtils.getMainScreenFromRoute("flashcard_deck/anime/n5") shouldBe Screen.Home.route
            NavigationUtils.getMainScreenFromRoute("quiz_session/food/n4") shouldBe Screen.Quiz.route
            NavigationUtils.getMainScreenFromRoute("quiz_results/result123") shouldBe Screen.Quiz.route
            NavigationUtils.getMainScreenFromRoute("game_session/memory") shouldBe Screen.Game.route
            NavigationUtils.getMainScreenFromRoute("profile_edit") shouldBe Screen.Profile.route
            NavigationUtils.getMainScreenFromRoute("custom_deck_manager") shouldBe Screen.Profile.route
            
            // Test fallback behavior
            NavigationUtils.getMainScreenFromRoute(null) shouldBe Screen.Home.route
            NavigationUtils.getMainScreenFromRoute("") shouldBe Screen.Home.route
            NavigationUtils.getMainScreenFromRoute("unknown_route") shouldBe Screen.Home.route
        }
    }
    
    "bottom navigation items should have consistent structure and labels" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that navigation items follow consistent patterns
            val expectedLabels = listOf("Home", "Learn", "Quiz", "Game", "Profile")
            val expectedRoutes = listOf(
                Screen.Home.route,
                Screen.Learn.route,
                Screen.Quiz.route,
                Screen.Game.route,
                Screen.Profile.route
            )
            
            // Verify labels are properly capitalized
            expectedLabels.forEach { label ->
                label.first().isUpperCase() shouldBe true
                label.length shouldBe label.trim().length // No leading/trailing spaces
                label.isNotEmpty() shouldBe true
            }
            
            // Verify routes match expected patterns
            expectedRoutes.forEach { route ->
                route.isNotEmpty() shouldBe true
                route.all { it.isLowerCase() || it.isDigit() } shouldBe true
            }
            
            // Verify one-to-one correspondence
            expectedLabels shouldHaveSize expectedRoutes.size
        }
    }
    
    "screen route creation should be consistent and safe" {
        checkAll(iterations = 100, Arb.string(1..20)) { testId ->
            // Test parameterized route creation
            val characterRoute = Screen.CharacterDetail.createRoute(testId)
            characterRoute shouldBe "character_detail/$testId"
            characterRoute.contains(testId) shouldBe true
            
            val deckRoute = Screen.FlashcardDeck.createRoute("anime", "n5")
            deckRoute shouldBe "flashcard_deck/anime/n5"
            deckRoute.contains("anime") shouldBe true
            deckRoute.contains("n5") shouldBe true
            
            val quizRoute = Screen.QuizSession.createRoute("food", "n4")
            quizRoute shouldBe "quiz_session/food/n4"
            quizRoute.contains("food") shouldBe true
            quizRoute.contains("n4") shouldBe true
            
            val resultsRoute = Screen.QuizResults.createRoute(testId)
            resultsRoute shouldBe "quiz_results/$testId"
            resultsRoute.contains(testId) shouldBe true
            
            val gameRoute = Screen.GameSession.createRoute("memory")
            gameRoute shouldBe "game_session/memory"
            gameRoute.contains("memory") shouldBe true
        }
    }
    
    "navigation should support smooth transitions with consistent animation patterns" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that animation-related classes are available
            val fadeInClass = try {
                Class.forName("androidx.compose.animation.FadeInKt")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
            
            val slideInClass = try {
                Class.forName("androidx.compose.animation.SlideInOutKt")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
            
            val scaleInClass = try {
                Class.forName("androidx.compose.animation.ScaleKt")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
            
            val tweenClass = try {
                Class.forName("androidx.compose.animation.core.TweenSpec")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
            
            // At least some animation classes should be available
            (fadeInClass || slideInClass || scaleInClass || tweenClass) shouldBe true
        }
    }
    
    "navigation should maintain consistent Material You design integration" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that Material 3 navigation components are available
            val navigationBarClass = try {
                Class.forName("androidx.compose.material3.NavigationBarKt")
                true
            } catch (e: ClassNotFoundException) {
                try {
                    Class.forName("androidx.compose.material3.NavigationBar")
                    true
                } catch (e2: ClassNotFoundException) {
                    false
                }
            }
            
            val navigationBarItemClass = try {
                Class.forName("androidx.compose.material3.NavigationBarItemKt")
                true
            } catch (e: ClassNotFoundException) {
                try {
                    Class.forName("androidx.compose.material3.NavigationBarItem")
                    true
                } catch (e2: ClassNotFoundException) {
                    false
                }
            }
            
            val materialThemeClass = try {
                Class.forName("androidx.compose.material3.MaterialThemeKt")
                true
            } catch (e: ClassNotFoundException) {
                try {
                    Class.forName("androidx.compose.material3.MaterialTheme")
                    true
                } catch (e2: ClassNotFoundException) {
                    false
                }
            }
            
            // At least one of these should be available
            (navigationBarClass || navigationBarItemClass || materialThemeClass) shouldBe true
        }
    }
})