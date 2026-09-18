package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LifeOsViewModel
import com.example.ui.NavigationModule
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchDialog(
    viewModel: LifeOsViewModel,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val tasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val goals by viewModel.allGoals.collectAsState()
    val notes by viewModel.allNotes.collectAsState()

    val filteredTasks = if (query.isBlank()) emptyList() else tasks.filter { it.title.contains(query, ignoreCase = true) }
    val filteredProjects = if (query.isBlank()) emptyList() else projects.filter { it.title.contains(query, ignoreCase = true) }
    val filteredGoals = if (query.isBlank()) emptyList() else goals.filter { it.title.contains(query, ignoreCase = true) }
    val filteredNotes = if (query.isBlank()) emptyList() else notes.filter { it.title.contains(query, ignoreCase = true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = AccentCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Command Palette & Search", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search tasks, projects, notes, goals...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("command_palette_input")
                )

                if (query.isBlank()) {
                    Text(
                        text = "QUICK COMMANDS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf(
                        Triple("Launch 25m Focus Block", Icons.Default.Timer, NavigationModule.FOCUS),
                        Triple("Consult AI Intelligence", Icons.Default.AutoAwesome, NavigationModule.AI_ASSISTANT),
                        Triple("Open Time Horizon Planner", Icons.Default.ViewTimeline, NavigationModule.PLANNER),
                        Triple("Log Daily Retrospective", Icons.Default.EditNote, NavigationModule.JOURNAL)
                    ).forEach { (label, icon, module) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.navigateTo(module)
                                    onDismiss()
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredTasks) { t ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.navigateTo(NavigationModule.TASKS)
                                        onDismiss()
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.TaskAlt, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Task: ${t.title}", fontSize = 12.sp, maxLines = 1)
                            }
                        }

                        items(filteredProjects) { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.navigateTo(NavigationModule.PROJECTS)
                                        onDismiss()
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Project: ${p.title}", fontSize = 12.sp, maxLines = 1)
                            }
                        }

                        items(filteredGoals) { g ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.navigateTo(NavigationModule.PLANNER)
                                        onDismiss()
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Flag, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Goal: ${g.title}", fontSize = 12.sp, maxLines = 1)
                            }
                        }

                        items(filteredNotes) { n ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.navigateTo(NavigationModule.VAULT)
                                        onDismiss()
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Vault: ${n.title}", fontSize = 12.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
