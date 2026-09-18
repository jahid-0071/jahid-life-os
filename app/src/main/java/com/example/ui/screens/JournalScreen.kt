package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LifeOsViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun JournalScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.allJournalEntries.collectAsState()
    var showNewEntryDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewEntryDialog = true },
                containerColor = AccentPurple,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_journal_fab")
            ) {
                Icon(Icons.Default.EditNote, contentDescription = "Reflect")
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Reflection", fontWeight = FontWeight.Bold)
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
                        text = "COGNITIVE JOURNAL & REFLECTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = AccentPurple
                    )
                    Text(
                        text = "Daily Retrospective & Mental Clarity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (entries.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showNewEntryDialog = true }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("No journal reflections recorded yet", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Tap 'New Reflection' to log today's mood, cognitive insights, and key wins.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            items(entries) { entry ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = entry.dateString,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentPurple
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = AccentEmerald.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Mood: ${entry.moodScore}/5",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentEmerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = AccentCyan.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Energy: ${entry.energyScore}/5",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    if (entry.wins.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "KEY WINS & BREAKTHROUGHS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald
                        )
                        Text(
                            text = entry.wins,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (entry.lessonsLearned.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "LESSONS LEARNED & CORRECTIONS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentAmber
                        )
                        Text(
                            text = entry.lessonsLearned,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (entry.gratitude.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "GRATITUDE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentPurple
                        )
                        Text(
                            text = entry.gratitude,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showNewEntryDialog) {
        var mood by remember { mutableStateOf(5) }
        var energy by remember { mutableStateOf(4) }
        var wins by remember { mutableStateOf("") }
        var challenges by remember { mutableStateOf("") }
        var lessons by remember { mutableStateOf("") }
        var gratitude by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewEntryDialog = false },
            title = { Text("Daily Retrospective", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mood Score (1-5):")
                        Row {
                            (1..5).forEach { score ->
                                FilledTonalIconButton(
                                    onClick = { mood = score },
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = if (mood == score) AccentEmerald else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier.size(32.dp).padding(2.dp)
                                ) {
                                    Text("$score", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(value = wins, onValueChange = { wins = it }, label = { Text("What were today's top wins?") }, maxLines = 2, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = lessons, onValueChange = { lessons = it }, label = { Text("What lesson or friction did you observe?") }, maxLines = 2, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = gratitude, onValueChange = { gratitude = it }, label = { Text("What are you deeply grateful for?") }, maxLines = 2, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addJournalEntry(mood, energy, wins, challenges, lessons, gratitude)
                        showNewEntryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Text("Save Reflection")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewEntryDialog = false }) { Text("Cancel") }
            }
        )
    }
}
