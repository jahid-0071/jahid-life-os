package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.ui.components.getHabitIcon
import com.example.ui.theme.*

@Composable
fun HabitsScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.allHabits.collectAsState()
    var showAddHabitDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddHabitDialog = true },
                containerColor = AccentEmerald,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_habit_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Habit", fontWeight = FontWeight.Bold)
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
                        text = "HABIT SYSTEM & CONSISTENCY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = AccentEmerald
                    )
                    Text(
                        text = "Atomic Compound Rituals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Streak Master Summary Card
            item {
                val maxStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
                val successRate = if (habits.isNotEmpty()) ((habits.count { it.currentStreak > 0 }.toFloat() / habits.size) * 100).toInt() else 0
                val activeDaysCount = maxStreak.coerceIn(0, 7)

                GlassCard(
                    borderColor = AccentEmerald.copy(alpha = 0.4f),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE STREAK ARCHITECTURE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentEmerald
                            )
                            Text(
                                text = if (maxStreak > 0) "$maxStreak-Day Streak" else "0-Day Streak",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$successRate% Consistency Rate",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = if (maxStreak > 0) AccentEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "PAST 7 DAYS ACTIVITY HEATMAP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEachIndexed { idx, day ->
                            val isDayDone = idx < activeDaysCount
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            if (isDayDone) AccentEmerald.copy(alpha = 0.85f) else MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDayDone) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            if (habits.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showAddHabitDialog = true }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("No habits created yet (0d Streak)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Tap 'New Habit' to build daily routines and establish consistent habit loops.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Habit Cards
            items(habits) { habit ->
                val habitColor = Color(android.graphics.Color.parseColor(habit.colorHex))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = habitColor.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = getHabitIcon(habit.iconKey),
                                contentDescription = habit.name,
                                tint = habitColor,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = habit.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Target: ${habit.targetCount} ${habit.unit} • ${habit.timeOfDay}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${habit.currentStreak}d",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentEmerald
                                )
                            }
                            Text(
                                text = "Best: ${habit.bestStreak}d",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        FilledTonalIconButton(
                            onClick = { viewModel.checkInHabit(habit) },
                            modifier = Modifier.size(38.dp).testTag("checkin_habit_${habit.id}")
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Check-in",
                                tint = AccentEmerald
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddHabitDialog) {
        var name by remember { mutableStateOf("") }
        var target by remember { mutableStateOf("1") }
        var unit by remember { mutableStateOf("times") }
        var category by remember { mutableStateOf("Health") }
        var iconKey by remember { mutableStateOf("water") }

        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            title = { Text("Establish New Habit", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Habit Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Daily Target (number)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit (glasses, pages, mins)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Health, Mind, Code)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addHabit(name, target.toIntOrNull() ?: 1, unit, category, iconKey)
                            showAddHabitDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald)
                ) {
                    Text("Save Habit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddHabitDialog = false }) { Text("Cancel") }
            }
        )
    }
}
