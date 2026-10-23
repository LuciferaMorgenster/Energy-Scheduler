package eu.lucifera.energyscheduler.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import eu.lucifera.energyscheduler.R
import eu.lucifera.energyscheduler.data.EnergyLevel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: EnergyViewModel,
    onMenuClick: () -> Unit
) {
    val status = viewModel.getCurrentStatus()
    val profile by viewModel.userProfile.collectAsState()
    val points by viewModel.energyPoints.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val userName = profile?.name ?: stringResource(R.string.user_default)
            val chronotypeLabel = profile?.chronotype?.let { stringResource(it.titleRes) } ?: "..."
            Text(
                text = stringResource(R.string.greeting, userName, chronotypeLabel),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when(status.level) {
                        EnergyLevel.PEAK -> MaterialTheme.colorScheme.primaryContainer
                        EnergyLevel.AVERAGE -> MaterialTheme.colorScheme.secondaryContainer
                        EnergyLevel.DIP -> MaterialTheme.colorScheme.tertiaryContainer
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.current_energy), style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = stringResource(status.level.labelRes),
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.advice), style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = stringResource(status.recommendation.titleRes),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = stringResource(status.recommendation.descriptionRes),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(stringResource(R.string.planned_tasks), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (tasks.filter { !it.isCompleted }.isEmpty()) {
                Text(stringResource(R.string.no_open_tasks), style = MaterialTheme.typography.bodyMedium)
            } else {
                tasks.filter { !it.isCompleted }.take(3).forEach { task ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(task.title, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                text = task.scheduledTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(stringResource(R.string.day_overview), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(points.sortedBy { it.time }.size) { index ->
                    val point = points.sortedBy { it.time }[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(point.time.toString(), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(point.level.labelRes), style = MaterialTheme.typography.bodyMedium, color = when(point.level) {
                            EnergyLevel.PEAK -> MaterialTheme.colorScheme.primary
                            EnergyLevel.AVERAGE -> MaterialTheme.colorScheme.secondary
                            EnergyLevel.DIP -> MaterialTheme.colorScheme.tertiary
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(stringResource(R.string.copyright), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun Divider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}
