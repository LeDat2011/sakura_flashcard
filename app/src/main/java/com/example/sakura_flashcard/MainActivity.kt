
package com.example.sakura_flashcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sakura_flashcard.data.auth.AuthRepository
import com.example.sakura_flashcard.data.auth.AuthState
import com.example.sakura_flashcard.navigation.Screen
import com.example.sakura_flashcard.navigation.NavigationUtils.navigateToMainScreen
import com.example.sakura_flashcard.ui.components.BottomNavigationBar
import com.example.sakura_flashcard.ui.screens.*
import com.example.sakura_flashcard.ui.screens.QuizScreen
import com.example.sakura_flashcard.ui.screens.QuizResultsScreen
import com.example.sakura_flashcard.ui.theme.SakuraFlashcardTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    @Inject
    lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SakuraFlashcardTheme {
                SakuraFlashcardApp(authRepository)
            }
        }
    }
}

@Composable
fun SakuraFlashcardApp(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val authState by authRepository.authState.collectAsState()
    
    // Track current route for bottom navigation visibility
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Determine start destination based on authentication state
    val startDestination = when (authState) {
        is AuthState.Authenticated -> Screen.Home.route
        else -> Screen.Login.route
    }
    
    // Handle authentication state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // User is authenticated, navigate to main app
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                // User is not authenticated, navigate to login
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> {
                // Loading or error states - handled by individual screens
            }
        }
    }
    
    // Main screens that should show bottom navigation
    val mainScreenRoutes = listOf(
        Screen.Home.route,
        Screen.Learn.route,
        Screen.Quiz.route,
        Screen.Game.route,
        Screen.Profile.route
    )
    
    // Show bottom navigation only for authenticated users on main screens
    val showBottomNav = authState is AuthState.Authenticated && 
                       currentRoute in mainScreenRoutes
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute, fromRoute ->
                        navController.navigateToMainScreen(targetRoute)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = if (showBottomNav) Modifier.padding(innerPadding) else Modifier.fillMaxSize(),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            // Authentication screens
            composable(
                route = Screen.Login.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        // Navigation handled by LaunchedEffect in SakuraFlashcardApp
                    }
                )
            }
            
            composable(
                route = Screen.Register.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                RegisterScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onRegistrationSuccess = {
                        // Navigate to onboarding after registration
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // Onboarding screen
            composable(
                route = Screen.Onboarding.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { 1000 },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                UserOnboardingScreen(
                    onComplete = { displayName, age, currentLevel, targetLevel, dailyGoalMinutes ->
                        // TODO: Save user profile to repository
                        // Navigate to Home after completing onboarding
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // Main app screens
            composable(
                route = Screen.Home.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                HomeScreenContainer(
                    onNavigateToFlashcardDeck = { topic, level ->
                        navController.navigate(
                            Screen.FlashcardDeck.createRoute(topic.name, level.name)
                        )
                    },
                    onNavigateToCustomDeck = { deck ->
                        navController.navigate(
                            Screen.CustomDeckFlashcard.createRoute(deck.id)
                        )
                    }
                )
            }
            composable(
                route = Screen.Learn.route,
                enterTransition = { fadeIn(animationSpec = tween(200)) },
                exitTransition = { fadeOut(animationSpec = tween(200)) }
            ) {
                LearnScreen(
                    onCharacterClick = { character ->
                        navController.navigate(
                            Screen.CharacterDetail.createRoute(character.character)
                        )
                    }
                )
            }
            composable(
                route = Screen.Quiz.route,
                enterTransition = { fadeIn(animationSpec = tween(200)) },
                exitTransition = { fadeOut(animationSpec = tween(200)) }
            ) {
                QuizScreen(
                    onTopicSelected = { topic, level ->
                        // Topic selection handled internally
                    },
                    onStartQuiz = { topic, level ->
                        navController.navigate(
                            Screen.QuizSession.createRoute(topic, level)
                        )
                    }
                )
            }
            composable(
                route = Screen.Game.route,
                enterTransition = { fadeIn(animationSpec = tween(200)) },
                exitTransition = { fadeOut(animationSpec = tween(200)) }
            ) {
                GameScreen()
            }
            composable(
                route = Screen.Profile.route,
                enterTransition = { fadeIn(animationSpec = tween(200)) },
                exitTransition = { fadeOut(animationSpec = tween(200)) }
            ) {
                ProfileScreen(
                    onNavigateToEdit = {
                        navController.navigate(Screen.ProfileEdit.route)
                    },
                    onNavigateToCustomDecks = {
                        navController.navigate(Screen.CustomDeckManager.route)
                    },
                    onLogout = {
                        // Logout handled by ProfileViewModel and AuthRepository
                        // Navigation handled by LaunchedEffect in SakuraFlashcardApp
                    }
                )
            }
            
            // Sub-screen routes with consistent animations
            composable(
                route = Screen.CharacterDetail.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { 1000 },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { 1000 },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) { backStackEntry ->
                val characterId = backStackEntry.arguments?.getString("characterId") ?: ""
                CharacterDetailScreen(
                    characterId = characterId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.FlashcardDeck.route,
                enterTransition = {
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val topic = backStackEntry.arguments?.getString("topic") ?: ""
                val level = backStackEntry.arguments?.getString("level") ?: ""
                FlashcardDeckScreen(
                    topic = topic,
                    level = level,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.QuizSession.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(350)
                    ) + fadeIn(animationSpec = tween(350))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(350)
                    ) + fadeOut(animationSpec = tween(350))
                }
            ) { backStackEntry ->
                val topic = backStackEntry.arguments?.getString("topic") ?: ""
                val level = backStackEntry.arguments?.getString("level") ?: ""
                QuizSessionScreen(
                    topic = topic,
                    level = level,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToResults = { resultId ->
                        navController.navigate(
                            Screen.QuizResults.createRoute(resultId)
                        )
                    }
                )
            }

            composable(
                route = Screen.QuizResults.route,
                enterTransition = {
                    scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 0.9f,
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) { backStackEntry ->
                val resultId = backStackEntry.arguments?.getString("resultId") ?: ""
                QuizResultsScreen(
                    resultId = resultId,
                    onNavigateBack = {
                        navController.popBackStack(Screen.Quiz.route, inclusive = false)
                    },
                    onRetakeQuiz = { topic, level ->
                        navController.navigate(Screen.QuizSession.createRoute(topic, level)) {
                            popUpTo(Screen.Quiz.route)
                        }
                    }
                )
            }
            composable(
                route = Screen.GameSession.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(350)
                    ) + fadeIn(animationSpec = tween(350))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(350)
                    ) + fadeOut(animationSpec = tween(350))
                }
            ) {
                // Placeholder for Game Session Screen
                PlaceholderScreen("Game Session Screen")
            }
            composable(
                route = Screen.ProfileEdit.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { 1000 },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { 1000 },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) { backStackEntry ->
                // Get parent entry to share ViewModel with ProfileScreen
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.Profile.route)
                }
                val sharedViewModel: ProfileViewModel = hiltViewModel(parentEntry)
                
                ProfileEditScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    viewModel = sharedViewModel
                )
            }
            composable(
                route = Screen.CustomDeckManager.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { 1000 },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { 1000 },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                CustomDeckManagerScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.CustomDeckFlashcard.route,
                arguments = listOf(
                    androidx.navigation.navArgument("deckId") { 
                        type = androidx.navigation.NavType.StringType 
                    }
                ),
                enterTransition = {
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                CustomDeckFlashcardScreen(
                    deckId = deckId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(screenName: String) {
    androidx.compose.material3.Text(
        text = "Welcome to $screenName",
        modifier = Modifier.fillMaxSize()
    )
}