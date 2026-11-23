package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppTopBar
import com.example.ui.localization.AppLanguage
import com.example.ui.navigation.Screen
import com.example.ui.screens.CreationsScreen
import com.example.ui.screens.ImageGeneratorScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PromptBuilderScreen
import com.example.ui.screens.VideoGeneratorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val language by viewModel.language.collectAsState()
            val creditsRemaining by viewModel.creditsRemaining.collectAsState()
            val context = LocalContext.current
            val snackbarHostState = remember { SnackbarHostState() }

            var currentRoute by remember { mutableStateOf(Screen.ImageGenerator.route) }

            // Listen to toast events
            LaunchedEffect(Unit) {
                viewModel.toastEvent.collectLatest { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            // Proper Android back button handling
            BackHandler(enabled = currentRoute != Screen.ImageGenerator.route) {
                currentRoute = Screen.ImageGenerator.route
            }

            val layoutDirection = if (language == AppLanguage.URDU) LayoutDirection.Rtl else LayoutDirection.Ltr

            MyApplicationTheme(themeMode = themeMode) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            AppTopBar(
                                language = language,
                                creditsRemaining = creditsRemaining,
                                onLanguageToggle = {
                                    val next = if (language == AppLanguage.URDU) AppLanguage.ENGLISH else AppLanguage.URDU
                                    viewModel.setLanguage(next)
                                },
                                onSettingsClick = {
                                    currentRoute = Screen.Profile.route
                                }
                            )
                        },
                        bottomBar = {
                            AppBottomBar(
                                currentRoute = currentRoute,
                                language = language,
                                onNavigateToRoute = { route ->
                                    currentRoute = route
                                }
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        val modifier = Modifier.padding(innerPadding)
                        when (currentRoute) {
                            Screen.ImageGenerator.route -> {
                                ImageGeneratorScreen(
                                    viewModel = viewModel,
                                    onNavigateToVideo = { currentRoute = Screen.VideoGenerator.route },
                                    modifier = modifier
                                )
                            }
                            Screen.VideoGenerator.route -> {
                                VideoGeneratorScreen(
                                    viewModel = viewModel,
                                    modifier = modifier
                                )
                            }
                            Screen.PromptBuilder.route -> {
                                PromptBuilderScreen(
                                    viewModel = viewModel,
                                    onNavigateToImage = { currentRoute = Screen.ImageGenerator.route },
                                    onNavigateToVideo = { currentRoute = Screen.VideoGenerator.route },
                                    modifier = modifier
                                )
                            }
                            Screen.Creations.route -> {
                                CreationsScreen(
                                    viewModel = viewModel,
                                    onNavigateToCreate = { currentRoute = Screen.ImageGenerator.route },
                                    modifier = modifier
                                )
                            }
                            Screen.Profile.route -> {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    modifier = modifier
                                )
                            }
                            else -> {
                                ImageGeneratorScreen(
                                    viewModel = viewModel,
                                    onNavigateToVideo = { currentRoute = Screen.VideoGenerator.route },
                                    modifier = modifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
