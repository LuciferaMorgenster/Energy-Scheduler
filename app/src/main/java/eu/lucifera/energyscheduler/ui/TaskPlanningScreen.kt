package eu.lucifera.energyscheduler.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.lucifera.energyscheduler.data.ActivityCategory
import eu.lucifera.energyscheduler.data.Task
import java.time.LocalTime
import java.time.format.DateTimeFormatter

import androidx.compose.ui.res.stringResource
import eu.lucifera.energyscheduler.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPlanningScreen(
    viewModel: EnergyViewModel,
    onMenuClick: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.planner)) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_task))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_tasks), style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AddTaskDialog(
                onDismiss = { showDialog = false },
                onAdd = { title, category, time ->
                    viewModel.addTask(title, category, time)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    )
                    Text(
                        text = "${task.scheduledTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${stringResource(task.category.titleRes)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAdd: (String, ActivityCategory, LocalTime) -> Unit) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ActivityCategory.CREATE) }
    var hour by remember { mutableStateOf("12") }
    var minute by remember { mutableStateOf("00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_task)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.title)) })
                
                Text(stringResource(R.string.category), style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ActivityCategory.values().forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(stringResource(category.titleRes)) }
                        )
                    }
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.time))
                    OutlinedTextField(value = hour, onValueChange = { hour = it }, modifier = Modifier.width(64.dp))
                    Text(":")
                    OutlinedTextField(value = minute, onValueChange = { minute = it }, modifier = Modifier.width(64.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val time = try {
                    LocalTime.of(hour.toInt(), minute.toInt())
                } catch (e: Exception) {
                    LocalTime.now()
                }
                onAdd(title, selectedCategory, time)
            }) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
