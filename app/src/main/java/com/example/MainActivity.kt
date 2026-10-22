package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.CaseZeroTheme
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CaseZeroViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            CaseZeroTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CaseZeroApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CaseZeroApp(viewModel: CaseZeroViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    // Handle Android system back gesture / button
    BackHandler(enabled = currentScreen !in listOf(Screen.Splash, Screen.Home)) {
        viewModel.popBack()
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            Screen.Splash -> {
                SplashScreen(
                    onSplashFinished = {
                        if (isOnboardingCompleted) {
                            viewModel.navigateTo(Screen.Home)
                        } else {
                            viewModel.navigateTo(Screen.Onboarding)
                        }
                    }
                )
            }
            Screen.Onboarding -> {
                OnboardingScreen(
                    onFinished = {
                        viewModel.repository.setOnboardingCompleted(true)
                        viewModel.navigateTo(Screen.Home)
                    }
                )
            }
            Screen.Home -> {
                HomeScreen(viewModel = viewModel)
            }
            Screen.CaseList -> {
                CaseListScreen(viewModel = viewModel)
            }
            Screen.CaseOverview -> {
                CaseOverviewScreen(viewModel = viewModel)
            }
            Screen.CaseTimeline -> {
                CaseTimelineScreen(viewModel = viewModel)
            }
            Screen.EvidenceGallery -> {
                EvidenceGalleryScreen(viewModel = viewModel)
            }
            Screen.InvestigationBoard -> {
                InvestigationBoardScreen(viewModel = viewModel)
            }
            Screen.Interrogation -> {
                InterrogationScreen(viewModel = viewModel)
            }
            Screen.FinalAccusation -> {
                FinalAccusationScreen(viewModel = viewModel)
            }
            Screen.CaseResults -> {
                CaseResultsScreen(viewModel = viewModel)
            }
            Screen.DetectiveRank -> {
                DetectiveRankScreen(viewModel = viewModel)
            }
            Screen.DailyChallenge -> {
                DailyChallengeScreen(viewModel = viewModel)
            }
            Screen.Settings -> {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
