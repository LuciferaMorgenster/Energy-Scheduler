package eu.lucifera.energyscheduler.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.lucifera.energyscheduler.R
import eu.lucifera.energyscheduler.data.ChronotypeType

import androidx.compose.ui.res.stringResource

import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronotypeSetupScreen(
    onChronotypeSelected: (String, ChronotypeType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val userDefault = stringResource(R.string.user_default)
    
    val chronotypes = listOf(
        ChronotypeInfo(ChronotypeType.LION, stringResource(R.string.lion_name), stringResource(R.string.lion_desc)),
        ChronotypeInfo(ChronotypeType.BEAR, stringResource(R.string.bear_name), stringResource(R.string.bear_desc)),
        ChronotypeInfo(ChronotypeType.WOLF, stringResource(R.string.wolf_name), stringResource(R.string.wolf_desc)),
        ChronotypeInfo(ChronotypeType.DOLPHIN, stringResource(R.string.dolphin_name), stringResource(R.string.dolphin_desc))
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.setup_title)) }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.ic_energy_app),
                    contentDescription = "App Icon",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    stringResource(R.string.welcome),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.setup_desc),
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    placeholder = { Text(userDefault) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(chronotypes) { info ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            onChronotypeSelected(if (name.isBlank()) userDefault else name, info.type) 
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(info.displayName, style = MaterialTheme.typography.titleLarge)
                        Text(info.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

data class ChronotypeInfo(
    val type: ChronotypeType,
    val displayName: String,
    val description: String
)
