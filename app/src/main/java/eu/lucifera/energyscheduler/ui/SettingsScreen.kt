package eu.lucifera.energyscheduler.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.lucifera.energyscheduler.data.ChronotypeType
import eu.lucifera.energyscheduler.data.ThemeMode

import androidx.compose.ui.res.stringResource
import eu.lucifera.energyscheduler.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: EnergyViewModel,
    onMenuClick: () -> Unit,
    onResetProfile: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val settings by viewModel.userSettings.collectAsState()

    var editedName by remember(profile?.name) { mutableStateOf(profile?.name ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.current_profile), style = MaterialTheme.typography.titleMedium)
            
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = { viewModel.updateName(editedName) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.update_name))
            }

            val chronoLabel = profile?.chronotype?.let { stringResource(it.titleRes) } ?: stringResource(R.string.unknown)
            Text(stringResource(R.string.chrono_label, chronoLabel))

            Button(
                onClick = onResetProfile,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(stringResource(R.string.reset_chrono))
            }

            Divider()

            Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)
            LanguageSelector(
                selectedLanguage = settings.language,
                onLanguageSelected = { viewModel.setLanguage(it) }
            )

            Divider()

            Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
            ThemeSelector(
                selectedMode = settings.themeMode,
                onModeSelected = { viewModel.setThemeMode(it) }
            )
        }
    }
}

@Composable
fun LanguageSelector(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    val languages = listOf(
        "system" to stringResource(R.string.lang_system),
        "nl" to "Nederlands",
        "en" to "English",
        "de" to "Deutsch",
        "fr" to "Français",
        "es" to "Español",
        "pt" to "Português",
        "in" to "Indonesian",
        "ar" to "العربية",
        "ru" to "Русский",
        "hi" to "हिन्दी",
        "bn" to "বাংলা",
        "zh" to "中文"
    )
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(languages.find { it.first == selectedLanguage }?.second ?: selectedLanguage)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            languages.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onLanguageSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ThemeSelector(selectedMode: ThemeMode, onModeSelected: (ThemeMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemeOption(stringResource(R.string.theme_light), ThemeMode.LIGHT, selectedMode == ThemeMode.LIGHT) { onModeSelected(ThemeMode.LIGHT) }
        ThemeOption(stringResource(R.string.theme_dark), ThemeMode.DARK, selectedMode == ThemeMode.DARK) { onModeSelected(ThemeMode.DARK) }
        ThemeOption(stringResource(R.string.theme_system), ThemeMode.SYSTEM, selectedMode == ThemeMode.SYSTEM) { onModeSelected(ThemeMode.SYSTEM) }
    }
}

@Composable
fun ThemeOption(label: String, mode: ThemeMode, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) }
    )
}
