package com.example.ui.screens

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
import com.example.data.model.LifeGoalEntity
import com.example.ui.LifeOsViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun LifePlannerScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.allGoals.collectAsState()
    val tasks by viewModel.allTasks.collectAsState()

    var selectedHorizon by remember { mutableStateOf("DAILY") } // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY, MULTI_YEAR
    var showAddGoalDialog by remember { mutableStateOf(false) }

    val horizonGoals = goals.filter {
        when (selectedHorizon) {
            "QUARTERLY" -> it.horizon == "QUARTERLY"
            "YEARLY" -> it.horizon == "YEARLY"
            "MULTI_YEAR" -> it.horizon == "MULTI_YEAR" || it.horizon == "VISION"
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddGoalDialog = true },
                containerColor = AccentPurple,
                contentColor = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.testTag("add_goal_fab")
            ) {
                Icon(Icons.Default.Flag, contentDescription = "Add Goal")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Set Goal", fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "TIME HORIZON PLANNER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = AccentPurple
                    )
                    Text(
                        text = "Life Architecture & Trajectory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Horizon Selector Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "DAILY" to "Daily Timeline",
                        "WEEKLY" to "Weekly Board",
                        "MONTHLY" to "Monthly Goals",
                        "QUARTERLY" to "Quarterly OKRs",
                        "YEARLY" to "2026 Master Plan",
                        "MULTI_YEAR" to "5-10 Yr Vision"
                    ).forEach { (key, label) ->
                        val isSelected = selectedHorizon == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedHorizon = key },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple,
                                selectedLabelColor = androidx.compose.ui.graphics.Color.White
                            )
                        )
                    }
                }
            }

            // Daily Horizon: Morning, Afternoon, Evening, Night
            if (selectedHorizon == "DAILY") {
                item {
                    Text(
                        text = "HOURLY TIMELINE & ENERGY RHYTHM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                listOf(
                    Triple("Morning (06:00 - 12:00)", "Peak Focus & Deep Work", AccentIndigo),
                    Triple("Afternoon (12:00 - 17:00)", "Execution, Meetings, Coding", AccentCyan),
                    Triple("Evening (17:00 - 21:00)", "Fitness, Reading & Habit Stack", AccentEmerald),
                    Triple("Night (21:00 - 23:00)", "Reflection, Journal, Restoration", AccentPurple)
                ).forEach { (period, desc, color) ->
                    val periodTasks = tasks.filter {
                        when {
                            period.startsWith("Morning") -> it.timeOfDay == "Morning"
                            period.startsWith("Afternoon") -> it.timeOfDay == "Afternoon"
                            period.startsWith("Evening") -> it.timeOfDay == "Evening"
                            else -> it.timeOfDay == "Night"
                        }
                    }

                    item {
                        GlassCard(borderColor = color.copy(alpha = 0.4f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = period,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    color = color.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${periodTasks.size} tasks",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (periodTasks.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                periodTasks.forEach { t ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (t.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (t.isCompleted) AccentEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = t.title,
                                            fontSize = 12.sp,
                                            color = if (t.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Goals for Quarterly, Yearly, Multi-Year
                if (horizonGoals.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No goals set for $selectedHorizon (0% progress)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap 'Set Goal' below to define your target metrics and trajectory.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(horizonGoals) { goal ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = goal.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentPurple
                            )
                            Text(
                                text = "Target: ${goal.targetYear}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = goal.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (goal.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = goal.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { goal.progress / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = AccentPurple,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = goal.keyMetric.ifEmpty { "${goal.progress}% completed" },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(
                                    onClick = { viewModel.updateGoalProgress(goal, goal.progress + 10) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("+10%", fontSize = 11.sp, color = AccentPurple)
                                }
                                TextButton(
                                    onClick = { viewModel.breakDownGoalWithAi(goal.title); viewModel.navigateTo(com.example.ui.NavigationModule.AI_ASSISTANT) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("AI Breakdown →", fontSize = 11.sp, color = AccentCyan)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddGoalDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var goalDesc by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Career") }
        var metric by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            title = { Text("Set Strategic Goal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Goal Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = goalDesc,
                        onValueChange = { goalDesc = it },
                        label = { Text("Key Objective Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = metric,
                        onValueChange = { metric = it },
                        label = { Text("Key Metric (e.g. 10k Stars, Sub 2:20)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (goalTitle.isNotBlank()) {
                            viewModel.addGoal(goalTitle, goalDesc, selectedHorizon, 2026, category, metric)
                            showAddGoalDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Text("Save Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) { Text("Cancel") }
            }
        )
    }
}
