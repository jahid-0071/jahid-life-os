package com.example.ui.screens

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
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun RoadmapsScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val phases by viewModel.allPhases.collectAsState()
    var roadmapTopicInput by remember { mutableStateOf("") }
    var showAiRoadmapDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ROADMAP ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = AccentCyan
                    )
                    Text(
                        text = "Systems & AI Mastery",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Button(
                    onClick = { showAiRoadmapDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("ai_generate_roadmap_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = androidx.compose.ui.graphics.Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Build", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Black)
                }
            }
        }

        // Master Progress Card
        item {
            val totalPhases = phases.size
            val completedPhases = phases.count { it.isCompleted }
            val avgProgress = if (totalPhases > 0) phases.map { it.progress }.average().toInt() else 0
            val activeRoadmapTitle = if (phases.isNotEmpty()) phases.first().roadmapTitle else "No Curriculum Configured"

            GlassCard(
                borderColor = AccentCyan.copy(alpha = 0.4f),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ACTIVE CURRICULUM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Text(
                            text = activeRoadmapTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$completedPhases of $totalPhases Phases Completed",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "$avgProgress%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentCyan
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { avgProgress / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = AccentCyan,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            }
        }

        if (phases.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showAiRoadmapDialog = true }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("No roadmap curriculum active (0% progress)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Tap 'AI Build' or tap here to generate an authoritative learning roadmap.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Phases List
        items(phases) { phase ->
            val topics = phase.topicsJson.split(",").map { it.trim() }.filter { it.isNotBlank() }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (phase.isCompleted) AccentEmerald.copy(alpha = 0.2f) else AccentCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "PHASE 0${phase.phaseNumber}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (phase.isCompleted) AccentEmerald else AccentCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${phase.progress}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (phase.isCompleted) AccentEmerald else AccentCyan
                        )
                        if (phase.isCompleted) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = AccentEmerald, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = phase.phaseTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (phase.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = phase.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (topics.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "CORE SKILLS & TOPICS:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    topics.forEach { topic ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = topic, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (phase.isCompleted) "Phase Mastered" else "In Progress",
                        fontSize = 11.sp,
                        color = if (phase.isCompleted) AccentEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalButton(
                            onClick = { viewModel.updatePhaseProgress(phase, 15) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+15% Progress", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAiRoadmapDialog) {
        AlertDialog(
            onDismissRequest = { showAiRoadmapDialog = false },
            title = { Text("Generate AI Roadmap", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter any domain or specialization. Life OS AI will architect an authoritative mastery roadmap with verified phase milestones.")
                    OutlinedTextField(
                        value = roadmapTopicInput,
                        onValueChange = { roadmapTopicInput = it },
                        label = { Text("e.g. Distributed Databases, Rust Kernel Dev, Quantum ML") },
                        modifier = Modifier.fillMaxWidth().testTag("ai_roadmap_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (roadmapTopicInput.isNotBlank()) {
                            viewModel.generateRoadmapWithAi(roadmapTopicInput)
                            showAiRoadmapDialog = false
                            viewModel.navigateTo(NavigationModule.AI_ASSISTANT)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                ) {
                    Text("Architect Roadmap", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiRoadmapDialog = false }) { Text("Cancel") }
            }
        )
    }
}
