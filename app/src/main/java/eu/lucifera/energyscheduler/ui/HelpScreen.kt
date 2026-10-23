package eu.lucifera.energyscheduler.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.res.stringResource
import eu.lucifera.energyscheduler.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onMenuClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.help_title)) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(stringResource(R.string.what_is_chrono), style = MaterialTheme.typography.titleLarge)
                Text(
                    stringResource(R.string.chrono_desc),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item {
                Text(stringResource(R.string.four_types), style = MaterialTheme.typography.titleMedium)
                Text("• ${stringResource(R.string.lion_name)}: ${stringResource(R.string.lion_desc)}")
                Text("• ${stringResource(R.string.bear_name)}: ${stringResource(R.string.bear_desc)}")
                Text("• ${stringResource(R.string.wolf_name)}: ${stringResource(R.string.wolf_desc)}")
                Text("• ${stringResource(R.string.dolphin_name)}: ${stringResource(R.string.dolphin_desc)}")
            }
            item {
                Text(stringResource(R.string.how_to_use), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.use_desc))
            }
        }
    }
}
