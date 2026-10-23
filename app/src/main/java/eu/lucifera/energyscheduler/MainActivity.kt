package eu.lucifera.energyscheduler

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import eu.lucifera.energyscheduler.ui.*
import eu.lucifera.energyscheduler.ui.theme.EnergySchedulerTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: EnergyViewModel = viewModel()
            val profile by viewModel.userProfile.collectAsState()
            val settings by viewModel.userSettings.collectAsState()

            // Apply language from settings
            LaunchedEffect(settings.language) {
                if (settings.language == "system") {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                } else {
                    val appLocales = LocaleListCompat.forLanguageTags(settings.language)
                    AppCompatDelegate.setApplicationLocales(appLocales)
                }
            }

            val darkTheme = when (settings.themeMode) {
                eu.lucifera.energyscheduler.data.ThemeMode.LIGHT -> false
                eu.lucifera.energyscheduler.data.ThemeMode.DARK -> true
                eu.lucifera.energyscheduler.data.ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            EnergySchedulerTheme(darkTheme = darkTheme) {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { }
                )

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = currentRoute != "setup",
                    drawerContent = {
                        ModalDrawerSheet {
                            Text(stringResource(R.string.app_name), modifier = androidx.compose.ui.Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                            HorizontalDivider()
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                                label = { Text(stringResource(R.string.dashboard)) },
                                selected = currentRoute == "dashboard",
                                onClick = {
                                    navController.navigate("dashboard")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.List, contentDescription = null) },
                                label = { Text(stringResource(R.string.planner)) },
                                selected = currentRoute == "planner",
                                onClick = {
                                    navController.navigate("planner")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                label = { Text(stringResource(R.string.settings)) },
                                selected = currentRoute == "settings",
                                onClick = {
                                    navController.navigate("settings")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null) },
                                label = { Text(stringResource(R.string.help)) },
                                selected = currentRoute == "help",
                                onClick = {
                                    navController.navigate("help")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                                label = { Text(stringResource(R.string.info)) },
                                selected = currentRoute == "info",
                                onClick = {
                                    navController.navigate("info")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))
                            Text(
                                stringResource(R.string.copyright),
                                modifier = androidx.compose.ui.Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                ) {
                    val startDestination = if (profile == null) "setup" else "dashboard"

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("setup") {
                            ChronotypeSetupScreen { name, type ->
                                viewModel.setChronotype(name, type)
                                navController.navigate("dashboard") {
                                    popUpTo("setup") { inclusive = true }
                                }
                            }
                        }
                        composable("dashboard") {
                            DashboardScreen(viewModel) {
                                scope.launch { drawerState.open() }
                            }
                        }
                        composable("planner") {
                            TaskPlanningScreen(viewModel) {
                                scope.launch { drawerState.open() }
                            }
                        }
                        composable("settings") {
                            SettingsScreen(
                                viewModel = viewModel,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onResetProfile = { navController.navigate("setup") }
                            )
                        }
                        composable("help") {
                            HelpScreen {
                                scope.launch { drawerState.open() }
                            }
                        }
                        composable("info") {
                            InfoScreen {
                                scope.launch { drawerState.open() }
                            }
                        }
                    }
                }
            }
        }
    }
}
