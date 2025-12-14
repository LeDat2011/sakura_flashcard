package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sakura_flashcard.ui.accessibility.rememberAccessibilityService
import com.example.sakura_flashcard.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilitySettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccessibilitySettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accessibilityService = rememberAccessibilityService()
    val themeConfig by viewModel.themeConfig.collectAsState()
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Accessibility Settings",
                        modifier = Modifier.semantics {
                            contentDescription = "Accessibility Settings Screen"
                        }
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.semantics {
                            contentDescription = "Go back to previous screen"
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual Accessibility Section
            AccessibilitySection(
                title = "Visual Accessibility",
                description = "Settings for visual impairments and preferences"
            ) {
                // High Contrast Mode
                AccessibilityToggle(
                    title = "High Contrast Mode",
                    description = "Increases contrast for better visibility",
                    checked = themeConfig.useHighContrast,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            viewModel.updateHighContrast(enabled)
                        }
                    }
                )
                
                // Font Scale
                AccessibilitySlider(
                    title = "Font Size",
                    description = "Adjust text size for better readability",
                    value = themeConfig.fontScale,
                    valueRange = 0.85f..2.0f,
                    steps = 11,
                    onValueChange = { scale ->
                        scope.launch {
                            viewModel.updateFontScale(scale)
                        }
                    },
                    valueFormatter = { "${(it * 100).toInt()}%" }
                )
                
                // Focus Indicators
                AccessibilityToggle(
                    title = "Focus Indicators",
                    description = "Show visual focus indicators for keyboard navigation",
                    checked = themeConfig.focusIndicators,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            viewModel.updateFocusIndicators(enabled)
                        }
                    }
                )
            }
            
            // Motion Accessibility Section
            AccessibilitySection(
                title = "Motion & Animation",
                description = "Settings for motion sensitivity and animation preferences"
            ) {
                // Reduce Motion
                AccessibilityToggle(
                    title = "Reduce Motion",
                    description = "Reduces or disables animations and transitions",
                    checked = themeConfig.reduceMotion,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            viewModel.updateReduceMotion(enabled)
                        }
                    }
                )
            }
            
            // Audio Accessibility Section
            AccessibilitySection(
                title = "Audio & Speech",
                description = "Text-to-speech and audio accessibility settings"
            ) {
                // Enable TTS
                AccessibilityToggle(
                    title = "Text-to-Speech",
                    description = "Enable spoken feedback for Japanese content",
                    checked = themeConfig.enableTTS,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            viewModel.updateEnableTTS(enabled)
                        }
                    }
                )
                
                if (themeConfig.enableTTS) {
                    // Speech Rate
                    AccessibilitySlider(
                        title = "Speech Rate",
                        description = "Adjust how fast text is spoken",
                        value = themeConfig.ttsSpeechRate,
                        valueRange = 0.1f..3.0f,
                        steps = 28,
                        onValueChange = { rate ->
                            scope.launch {
                                viewModel.updateTTSSpeechRate(rate)
                                accessibilityService.setSpeechRate(rate)
                            }
                        },
                        valueFormatter = { "${(it * 100).toInt()}%" }
                    )
                    
                    // Speech Pitch
                    AccessibilitySlider(
                        title = "Speech Pitch",
                        description = "Adjust the pitch of spoken text",
                        value = themeConfig.ttsSpeechPitch,
                        valueRange = 0.1f..2.0f,
                        steps = 18,
                        onValueChange = { pitch ->
                            scope.launch {
                                viewModel.updateTTSSpeechPitch(pitch)
                                accessibilityService.setSpeechPitch(pitch)
                            }
                        },
                        valueFormatter = { "${(it * 100).toInt()}%" }
                    )
                    
                    // Auto-speak flashcards
                    AccessibilityToggle(
                        title = "Auto-speak Flashcards",
                        description = "Automatically speak flashcard content when displayed",
                        checked = themeConfig.autoSpeakFlashcards,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                viewModel.updateAutoSpeakFlashcards(enabled)
                            }
                        }
                    )
                    
                    // Announce flashcard sides
                    AccessibilityToggle(
                        title = "Announce Flashcard Sides",
                        description = "Announce whether showing front or back of flashcard",
                        checked = themeConfig.announceFlashcardSides,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                viewModel.updateAnnounceFlashcardSides(enabled)
                            }
                        }
                    )
                    
                    // Test TTS Button
                    Button(
                        onClick = {
                            accessibilityService.speakFlashcardContent(
                                japaneseText = "こんにちは",
                                pronunciation = "konnichiwa",
                                translation = "Hello",
                                explanation = "A common Japanese greeting"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Test text-to-speech with sample Japanese content"
                            }
                    ) {
                        Text("Test Speech")
                    }
                }
            }
            
            // Navigation Accessibility Section
            AccessibilitySection(
                title = "Navigation",
                description = "Keyboard and navigation accessibility settings"
            ) {
                // Keyboard Navigation
                AccessibilityToggle(
                    title = "Keyboard Navigation",
                    description = "Enable keyboard shortcuts and navigation",
                    checked = themeConfig.keyboardNavigation,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            viewModel.updateKeyboardNavigation(enabled)
                        }
                    }
                )
                
                if (themeConfig.keyboardNavigation) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Keyboard Shortcuts",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "• Space: Flip flashcard\n" +
                                "• Left/Right arrows: Navigate\n" +
                                "• Enter: Select/Activate\n" +
                                "• Ctrl+P: Play audio\n" +
                                "• Ctrl+L: Mark as learned\n" +
                                "• Ctrl+N: Mark as not learned",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // System Integration Section
            AccessibilitySection(
                title = "System Integration",
                description = "Information about system accessibility features"
            ) {
                val accessibilityState = rememberAccessibilityState()
                
                AccessibilityInfoItem(
                    title = "Screen Reader",
                    value = if (accessibilityState.isScreenReaderEnabled) "Enabled" else "Disabled",
                    description = "TalkBack or other screen reader services"
                )
                
                AccessibilityInfoItem(
                    title = "System Font Scale",
                    value = "${(accessibilityState.systemFontScale * 100).toInt()}%",
                    description = "Current system font size setting"
                )
                
                AccessibilityInfoItem(
                    title = "Accessibility Services",
                    value = if (accessibilityState.isAccessibilityEnabled) "Active" else "Inactive",
                    description = "System accessibility services status"
                )
            }
        }
    }
}

@Composable
private fun AccessibilitySection(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            content()
        }
    }
}

@Composable
private fun AccessibilityToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$title. $description. Currently ${if (checked) "enabled" else "disabled"}"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.semantics {
                contentDescription = if (checked) "Disable $title" else "Enable $title"
            }
        )
    }
}

@Composable
private fun AccessibilitySlider(
    title: String,
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = valueFormatter(value),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "$title slider. Current value: ${valueFormatter(value)}"
                }
        )
    }
}

@Composable
private fun AccessibilityInfoItem(
    title: String,
    value: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$title: $value. $description"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}