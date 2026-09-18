package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.LifeOsViewModel
import com.example.ui.NavigationModule
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val tasks by viewModel.allTasks.collectAsState()
    val subtasks by viewModel.allSubtasks.collectAsState()
    val habits by viewModel.allHabits.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val goals by viewModel.allGoals.collectAsState()
    val healthFinance by viewModel.recentHealthFinance.collectAsState()
    val focusSessions by viewModel.recentFocusSessions.collectAsState()
    val todayMetrics = healthFinance.firstOrNull() ?: HealthFinanceEntity()

    val activeTasks = tasks.filter { !it.isCompleted }
    val topFocusTask = activeTasks.minByOrNull { it.priority } ?: tasks.firstOrNull()
    val todayCompletedCount = tasks.count { it.isCompleted }
    val totalTaskCount = tasks.size
    val completionRate = if (totalTaskCount > 0) (todayCompletedCount.toFloat() / totalTaskCount) else 0f

    val lifeScore = profile?.lifeScore ?: 0
    val streakDays = profile?.streakDays ?: 0
    val deepWorkMinutes = focusSessions.sumOf { it.durationMinutes }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // Today's Focus Card (Mission Control Primary)
        item {
            GlassCard(
                borderColor = AccentIndigo.copy(alpha = 0.5f),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AccentIndigo.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Primary Focus",
                                tint = AccentIndigo,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TODAY'S MISSION CONTROL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = AccentIndigo
                        )
                    }
                    Text(
                        text = "$lifeScore% Life Score",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (lifeScore > 0) AccentEmerald else TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (topFocusTask != null) {
                    Text(
                        text = topFocusTask.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = topFocusTask.description.ifEmpty { "High-leverage milestone requiring deep cognitive flow." },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PriorityBadge(priority = topFocusTask.priority)
                            TaskTypeBadge(typeString = topFocusTask.taskType)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.navigateTo(NavigationModule.FOCUS) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("focus_start_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Focus", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalIconButton(
                                onClick = { viewModel.toggleTask(topFocusTask) },
                                modifier = Modifier.size(34.dp).testTag("complete_focus_task_button")
                            ) {
                                Icon(
                                    imageVector = if (topFocusTask.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Complete",
                                    tint = if (topFocusTask.isCompleted) AccentEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No Active Priority Task",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your task queue is clear. Tap to add your first high-impact focus task.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.navigateTo(NavigationModule.TASKS) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Executive Metrics Grid: Life Score, Deep Work, Habits, Tasks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Life Score Ring
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(NavigationModule.ANALYTICS) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MetricRing(
                            progress = (lifeScore / 100f).coerceIn(0f, 1f),
                            primaryColor = AccentCyan,
                            size = 54.dp
                        ) {
                            Text(
                                text = "$lifeScore",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Life Score",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lifeScore > 0) "Active" else "0%",
                            fontSize = 10.sp,
                            color = if (lifeScore > 0) AccentEmerald else TextSecondary
                        )
                    }
                }

                // Deep Work Hours
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(NavigationModule.FOCUS) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MetricRing(
                            progress = (deepWorkMinutes / 240f).coerceIn(0f, 1f),
                            primaryColor = AccentIndigo,
                            size = 54.dp
                        ) {
                            Text(
                                text = if (deepWorkMinutes > 0) "${String.format(java.util.Locale.US, "%.1f", deepWorkMinutes / 60f)}h" else "0h",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Deep Work",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Target: 4.0h",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Habits Streak
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(NavigationModule.HABITS) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MetricRing(
                            progress = (streakDays / 30f).coerceIn(0f, 1f),
                            primaryColor = AccentPurple,
                            size = 54.dp
                        ) {
                            Text(
                                text = "${streakDays}d",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Streak",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (streakDays > 0) "Consistent" else "0d Streak",
                            fontSize = 10.sp,
                            color = AccentPurple
                        )
                    }
                }
            }
        }

        // Daily Habits Quick-Check Row
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "DAILY HABIT STACK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "View All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentIndigo,
                        modifier = Modifier.clickable { viewModel.navigateTo(NavigationModule.HABITS) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (habits.isEmpty()) {
                    Surface(
                        onClick = { viewModel.navigateTo(NavigationModule.HABITS) },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("habit_empty_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("No habits configured yet (0d Streak)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Tap to build your daily consistency rituals and atomic habits.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        habits.take(6).forEach { habit ->
                            Surface(
                                onClick = { viewModel.checkInHabit(habit) },
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.width(115.dp).testTag("habit_quick_${habit.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        color = Color(android.graphics.Color.parseColor(habit.colorHex)).copy(alpha = 0.2f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = getHabitIcon(habit.iconKey),
                                            contentDescription = habit.name,
                                            tint = Color(android.graphics.Color.parseColor(habit.colorHex)),
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = habit.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${habit.currentStreak}d Streak",
                                        fontSize = 10.sp,
                                        color = AccentEmerald,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // AI Strategic Guidance Banner
        item {
            GlassCard(
                borderColor = AccentCyan.copy(alpha = 0.4f),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Coach",
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI EXECUTIVE DIRECTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val directiveText = when {
                    activeTasks.isNotEmpty() -> "Prioritize your highest-leverage task: '${activeTasks.first().title}'. Maintain ultradian deep work blocks."
                    goals.isNotEmpty() -> "Channel your energy towards: '${goals.first().title}'. Break it down into tactical daily priorities."
                    else -> "Welcome to Life OS. Your database is clear and ready. Architect your roadmaps and goals to activate AI strategic guidance."
                }
                Text(
                    text = directiveText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = { viewModel.requestProductivityCoaching(); viewModel.navigateTo(NavigationModule.AI_ASSISTANT) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Conduct Review →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    }
                }
            }
        }

        // Top Priorities (P1 / P2 Task List)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "TOP STRATEGIC PRIORITIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Manage (${tasks.count { !it.isCompleted }})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentIndigo,
                    modifier = Modifier.clickable { viewModel.navigateTo(NavigationModule.TASKS) }
                )
            }
        }

        if (activeTasks.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.navigateTo(NavigationModule.TASKS) }
                ) {
                    Text(
                        text = "No pending tasks in queue (0% progress)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Schedule your daily priorities or capture a quick inbox item.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(activeTasks.take(4)) { task ->
            val taskSubtasks = subtasks.filter { it.taskId == task.id }
            val doneSubCount = taskSubtasks.count { it.isDone }

            GlassCard(
                onClick = { viewModel.toggleTask(task) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleTask(task) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Toggle",
                            tint = if (task.isCompleted) AccentEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PriorityBadge(priority = task.priority)
                            TaskTypeBadge(typeString = task.taskType)
                            if (taskSubtasks.isNotEmpty()) {
                                Text(
                                    text = "$doneSubCount/${taskSubtasks.size} subtasks",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "• ${task.estimatedMinutes}m",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Active Projects Overview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ACTIVE PROJECTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "All Projects",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentIndigo,
                    modifier = Modifier.clickable { viewModel.navigateTo(NavigationModule.PROJECTS) }
                )
            }
        }

        item {
            if (projects.isEmpty()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.navigateTo(NavigationModule.PROJECTS) }
                ) {
                    Text(
                        text = "No active projects (0% completed)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to initialize a multi-phase project initiative.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    projects.forEach { project ->
                        GlassCard(
                            modifier = Modifier.width(220.dp),
                            onClick = { viewModel.navigateTo(NavigationModule.PROJECTS) }
                        ) {
                            Text(
                                text = project.category.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentIndigo
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = project.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { project.progress / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = AccentIndigo,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${project.progress}% Complete",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = project.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentEmerald
                                )
                            }
                        }
                    }
                }
            }
        }

        // Long Term Vision & Goals
        item {
            GlassCard(
                borderColor = AccentPurple.copy(alpha = 0.4f),
                onClick = { viewModel.navigateTo(NavigationModule.PLANNER) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "NORTH STAR & 2026 VISION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentPurple
                    )
                    Text(
                        text = "Life Planner →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentPurple
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (goals.isEmpty()) {
                    Text(
                        text = "No vision goals defined yet (0%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to architect your long-term horizons and North Star milestones.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    goals.take(2).forEach { goal ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = goal.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${goal.progress}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentPurple
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { goal.progress / 100f },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = AccentPurple,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}
