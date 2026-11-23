package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val stringKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object ImageGenerator : Screen("image_generator", "nav_image", Icons.Filled.Image, Icons.Outlined.Image)
    data object VideoGenerator : Screen("video_generator", "nav_video", Icons.Filled.Videocam, Icons.Outlined.Videocam)
    data object PromptBuilder : Screen("prompt_builder", "nav_builder", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    data object Creations : Screen("creations", "nav_creations", Icons.Filled.Collections, Icons.Outlined.Collections)
    data object Profile : Screen("profile", "nav_profile", Icons.Filled.Person, Icons.Outlined.Person)

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(
                ImageGenerator,
                VideoGenerator,
                PromptBuilder,
                Creations,
                Profile
            )
    }
}
