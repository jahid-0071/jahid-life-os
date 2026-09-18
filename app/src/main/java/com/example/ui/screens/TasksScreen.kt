package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.SmartTaskType
import com.example.data.model.SubtaskEntity
import com.example.data.model.TaskEntity
import com.example.ui.LifeOsViewModel
import com.example.ui.NavigationModule
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TasksScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()
    val subtasks by viewModel.allSubtasks.collectAsState()

    var selectedStatusFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, DONE
    var selectedPriorityFilter by remember { mutableStateOf<Int?>(null) }
    var selectedTypeFilter by remember { mutableStateOf<String?>(null) }
    var expandedTaskId by remember { mutableStateOf<Long?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val filteredTasks = tasks.filter { task ->
        val matchesStatus = when (selectedStatusFilter) {
            "ACTIVE" -> !task.isCompleted
            "DONE" -> task.isCompleted
            else -> true
        }
        val matchesPriority = selectedPriorityFilter == null || task.priority == selectedPriorityFilter
        val matchesType = selectedTypeFilter == null || task.taskType == selectedTypeFilter
        matchesStatus && matchesPriority && matchesType
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = AccentIndigo,
                contentColor = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.testTag("add_task_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Task", fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
        ) {
            // Screen Header & Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TASK SYSTEM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = AccentIndigo
                        )
                        Text(
                            text = "Hierarchical Execution Engine",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Text(
                        text = "${tasks.count { it.isCompleted }}/${tasks.size} Done",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentEmerald
                    )
                }
            }

            // Status Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "ACTIVE", "DONE").forEach { status ->
                        val isSelected = selectedStatusFilter == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedStatusFilter = status },
                            label = { Text(status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentIndigo,
                                selectedLabelColor = androidx.compose.ui.graphics.Color.White
                            )
                        )
                    }
                }
            }

            // Smart Task Types Scrollable Filter Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedTypeFilter == null,
                        onClick = { selectedTypeFilter = null },
                        label = { Text("All Types", fontSize = 11.sp) }
                    )
                    SmartTaskType.entries.forEach { type ->
                        val isSelected = selectedTypeFilter == type.name
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTypeFilter = if (isSelected) null else type.name },
                            leadingIcon = {
                                Icon(
                                    imageVector = getTaskTypeIcon(type),
                                    contentDescription = type.label,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            label = { Text(type.label, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Tasks List
            if (filteredTasks.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = "No tasks",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Zero pending tasks in this view.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap '+ New Task' to schedule your next milestone.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(filteredTasks, key = { it.id }) { task ->
                val taskSubtasks = subtasks.filter { it.taskId == task.id }
                val isExpanded = expandedTaskId == task.id

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { expandedTaskId = if (isExpanded) null else task.id }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleTask(task) },
                            modifier = Modifier.size(32.dp).testTag("task_toggle_${task.id}")
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle completion",
                                tint = if (task.isCompleted) AccentEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                            if (task.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = task.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = if (isExpanded) 10 else 1
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteTask(task) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PriorityBadge(priority = task.priority)
                            TaskTypeBadge(typeString = task.taskType)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (taskSubtasks.isNotEmpty()) {
                                Text(
                                    text = "${taskSubtasks.count { it.isDone }}/${taskSubtasks.size} subtasks",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = "${task.estimatedMinutes}m",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Expanded Subtasks View
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "SUBTASKS & CHECKLIST",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = AccentIndigo
                            )

                            taskSubtasks.forEach { sub ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.toggleSubtask(sub) }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (sub.isDone) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                        contentDescription = "Check",
                                        tint = if (sub.isDone) AccentEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = sub.title,
                                        fontSize = 12.sp,
                                        color = if (sub.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (task.aiNotes != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = AccentCyan.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = task.aiNotes,
                                        fontSize = 11.sp,
                                        color = AccentCyan,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.navigateTo(NavigationModule.FOCUS) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Focus", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Launch Deep Focus Session", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, cat, type, prio, est, time, horizon, subtasksList ->
                viewModel.addTask(title, desc, cat, type, prio, est, time, horizon, subtasksList)
                showAddTaskDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Int, Int, String, String, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(SmartTaskType.CODING.name) }
    var selectedPriority by remember { mutableStateOf(2) }
    var estimatedMins by remember { mutableStateOf("45") }
    var subtaskInput by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Engineering") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Strategic Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("task_title_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Notes") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subtaskInput,
                    onValueChange = { subtaskInput = it },
                    label = { Text("Subtasks (comma separated)") },
                    placeholder = { Text("e.g. Plan API, Write test, Bench") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = estimatedMins,
                        onValueChange = { estimatedMins = it },
                        label = { Text("Est. Minutes") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val subtasksList = subtaskInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        onConfirm(
                            title,
                            description,
                            category,
                            selectedType,
                            selectedPriority,
                            estimatedMins.toIntOrNull() ?: 45,
                            "Morning",
                            "DAILY",
                            subtasksList
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
            ) {
                Text("Create Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
