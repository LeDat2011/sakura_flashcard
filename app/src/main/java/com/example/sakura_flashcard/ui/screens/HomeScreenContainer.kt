package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sakura_flashcard.ui.screens.CustomDeck
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.data.model.VocabularyTopic

@Composable
fun HomeScreenContainer(
    onNavigateToFlashcardDeck: (VocabularyTopic, JLPTLevel) -> Unit,
    onNavigateToCustomDeck: (CustomDeck) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val customDecks by profileViewModel.customDecks.collectAsStateWithLifecycle()
    
    // Load user data on first composition
    LaunchedEffect(Unit) {
        // For now, using a mock user ID. In a real app, this would come from authentication
        viewModel.loadUserData("mock-user-id")
    }
    
    when {
        uiState.isLoading -> {
            LoadingScreen()
        }
        uiState.error != null -> {
            ErrorScreen(
                error = uiState.error!!,
                onRetry = { viewModel.refreshData() },
                onDismiss = { viewModel.clearError() }
            )
        }
        else -> {
            HomeScreen(
                user = uiState.user,
                recommendedFlashcards = uiState.recommendedFlashcards,
                customDecks = customDecks,
                onFlashcardInteraction = { flashcard, interactionType ->
                    viewModel.handleFlashcardInteraction(flashcard, interactionType)
                },
                onTopicSelected = { topic ->
                    // Handle topic selection - could show a dialog or navigate
                },
                onLevelSelected = { topic, level ->
                    onNavigateToFlashcardDeck(topic, level)
                },
                onCustomDeckSelected = onNavigateToCustomDeck,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Loading your learning journey...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                    
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}