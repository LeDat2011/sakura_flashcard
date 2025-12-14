package com.example.sakura_flashcard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions

/**
 * Navigation utility functions to ensure consistent navigation patterns across the app
 */
object NavigationUtils {
    
    /**
     * Navigate to a main screen (Home, Learn, Quiz, Game, Profile) with consistent behavior
     */
    fun NavController.navigateToMainScreen(route: String) {
        navigate(route) {
            // Pop up to the start destination to avoid building up a large stack
            popUpTo(graph.startDestinationId) {
                saveState = true
            }
            // Avoid multiple copies of the same destination
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    
    /**
     * Navigate to a sub-screen with standard options
     */
    fun NavController.navigateToSubScreen(route: String) {
        navigate(route) {
            launchSingleTop = true
        }
    }
    
    /**
     * Navigate to a detail screen with animation-friendly options
     */
    fun NavController.navigateToDetailScreen(route: String) {
        navigate(route) {
            launchSingleTop = true
            // Allow back navigation to work properly
            restoreState = false
        }
    }
    
    /**
     * Get the index of a main screen route for animation direction
     */
    fun getMainScreenIndex(route: String): Int {
        return when (route) {
            Screen.Home.route -> 0
            Screen.Learn.route -> 1
            Screen.Quiz.route -> 2
            Screen.Game.route -> 3
            Screen.Profile.route -> 4
            else -> -1
        }
    }
    
    /**
     * Determine animation direction: true = left to right, false = right to left
     */
    fun shouldSlideFromRight(fromRoute: String?, toRoute: String): Boolean {
        val fromIndex = fromRoute?.let { getMainScreenIndex(it) } ?: -1
        val toIndex = getMainScreenIndex(toRoute)
        return toIndex > fromIndex
    }
    
    /**
     * Navigate back with consistent behavior
     */
    fun NavController.navigateBack(): Boolean {
        return if (canGoBack()) {
            popBackStack()
            true
        } else {
            false
        }
    }
    
    /**
     * Check if navigation can go back
     */
    fun NavController.canGoBack(): Boolean {
        return previousBackStackEntry != null
    }
    
    /**
     * Get standard navigation options for main screens
     */
    fun getMainScreenNavOptions(startDestinationId: Int): NavOptions {
        return navOptions {
            popUpTo(startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    
    /**
     * Get standard navigation options for sub-screens
     */
    fun getSubScreenNavOptions(): NavOptions {
        return navOptions {
            launchSingleTop = true
        }
    }
    
    /**
     * Check if current route is a main navigation route
     */
    fun isMainNavigationRoute(route: String?): Boolean {
        return when (route) {
            Screen.Home.route,
            Screen.Learn.route,
            Screen.Quiz.route,
            Screen.Game.route,
            Screen.Profile.route -> true
            else -> false
        }
    }
    
    /**
     * Get the main screen route from any sub-screen route
     */
    fun getMainScreenFromRoute(route: String?): String {
        return when {
            route?.startsWith("character_detail") == true -> Screen.Learn.route
            route?.startsWith("flashcard_deck") == true -> Screen.Home.route
            route?.startsWith("quiz_session") == true -> Screen.Quiz.route
            route?.startsWith("quiz_results") == true -> Screen.Quiz.route
            route?.startsWith("game_session") == true -> Screen.Game.route
            route?.startsWith("profile_edit") == true -> Screen.Profile.route
            route?.startsWith("custom_deck_manager") == true -> Screen.Profile.route
            else -> Screen.Home.route
        }
    }
}