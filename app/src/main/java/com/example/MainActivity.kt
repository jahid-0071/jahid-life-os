package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.LifeOsViewModel
import com.example.ui.NavigationModule
import com.example.ui.components.GlobalSearchDialog
import com.example.ui.components.LifeOsTopBar
import com.example.ui.components.ModuleNavigationTabs
import com.example.ui.screens.*
import com.example.ui.theme.LifeOsTheme

class MainActivity : ComponentActivity() {
    private val viewModel: LifeOsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userProfileState by viewModel.userProfile.collectAsState()
            val userProfile = userProfileState ?: com.example.data.model.UserProfileEntity()
            val currentModule by viewModel.currentModule.collectAsState()
            var showSearchDialog by remember { mutableStateOf(false) }

            LifeOsTheme(
                darkTheme = true,
                themeName = userProfile.currentTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!userProfile.isOnboarded) {
                        OnboardingScreen(viewModel = viewModel)
                    } else {
                        Scaffold(
                            topBar = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .statusBarsPadding()
                                        .background(MaterialTheme.colorScheme.background)
                                ) {
                                    LifeOsTopBar(
                                        profile = userProfile,
                                        onSearchClick = { showSearchDialog = true },
                                        onAiClick = { viewModel.navigateTo(NavigationModule.AI_ASSISTANT) },
                                        onProfileClick = { viewModel.navigateTo(NavigationModule.ANALYTICS) }
                                    )
                                    ModuleNavigationTabs(
                                        selectedModule = currentModule,
                                        onSelectModule = { viewModel.navigateTo(it) }
                                    )
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.background
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (currentModule) {
                                    NavigationModule.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                                    NavigationModule.PLANNER -> LifePlannerScreen(viewModel = viewModel)
                                    NavigationModule.TASKS -> TasksScreen(viewModel = viewModel)
                                    NavigationModule.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                                    NavigationModule.ROADMAPS -> RoadmapsScreen(viewModel = viewModel)
                                    NavigationModule.HABITS -> HabitsScreen(viewModel = viewModel)
                                    NavigationModule.STUDY -> StudyScreen(viewModel = viewModel)
                                    NavigationModule.FOCUS -> FocusScreen(viewModel = viewModel)
                                    NavigationModule.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel)
                                    NavigationModule.VAULT -> VaultScreen(viewModel = viewModel)
                                    NavigationModule.JOURNAL -> JournalScreen(viewModel = viewModel)
                                    NavigationModule.HEALTH_FINANCE -> HealthFinanceScreen(viewModel = viewModel)
                                    NavigationModule.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                                }
                            }
                        }

                        if (showSearchDialog) {
                            GlobalSearchDialog(
                                viewModel = viewModel,
                                onDismiss = { showSearchDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
