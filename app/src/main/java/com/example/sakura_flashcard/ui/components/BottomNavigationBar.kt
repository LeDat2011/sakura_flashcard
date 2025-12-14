package com.example.sakura_flashcard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sakura_flashcard.navigation.Screen
import com.example.sakura_flashcard.ui.theme.AccessibilityUtils

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    onNavigate: (targetRoute: String, fromRoute: String?) -> Unit
) {
    val context = LocalContext.current
    val items = listOf(
        BottomNavItem(Screen.Home, Icons.Rounded.Home, Icons.Rounded.Home, "Home"),
        BottomNavItem(Screen.Learn, Icons.Rounded.MenuBook, Icons.Rounded.MenuBook, "Learn"),
        BottomNavItem(Screen.Quiz, Icons.Rounded.Quiz, Icons.Rounded.Quiz, "Quiz"),
        BottomNavItem(Screen.Game, Icons.Rounded.SportsEsports, Icons.Rounded.SportsEsports, "Game"),
        BottomNavItem(Screen.Profile, Icons.Rounded.Person, Icons.Rounded.Person, "Profile")
    )
    
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    // Get animation duration based on accessibility preferences
    val animationDuration = AccessibilityUtils.getAnimationDuration(context, 300)
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp,
            modifier = Modifier
                .height(80.dp)
                .semantics {
                    contentDescription = "Bottom navigation bar with 5 tabs: Home, Learn, Quiz, Game, and Profile"
                }
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    animationSpec = tween(animationDuration),
                    label = "iconColor"
                )
                
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "scale"
                )
                
                NavigationBarItem(
                    icon = { 
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
                        )
                    },
                    label = { 
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = iconColor
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.screen.route) {
                            onNavigate(item.screen.route, currentRoute)
                        }
                    },
                    modifier = Modifier.semantics {
                        contentDescription = if (isSelected) {
                            "${item.label} tab, currently selected"
                        } else {
                            "${item.label} tab, tap to navigate to ${item.label} screen"
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}