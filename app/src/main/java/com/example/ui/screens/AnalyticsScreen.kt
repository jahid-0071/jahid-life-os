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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LifeOsViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricRing
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val profileState by viewModel.userProfile.collectAsState()
    val profile = profileState ?: com.example.data.model.UserProfileEntity()
    val tasks by viewModel.allTasks.collectAsState()
    val habits by viewModel.allHabits.collectAsState()

    val xpForNextLevel = 300
    val currentLevelXp = profile.xp % xpForNextLevel
    val levelProgress = currentLevelXp.toFloat() / xpForNextLevel

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        item {
            Column {
                Text(
                    text = "SYSTEMS ANALYTICS & GAMIFICATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = AccentCyan
                )
                Text(
                    text = "Performance Mastery & Level Rank",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Level & XP Master Card
        item {
            GlassCard(
                borderColor = AccentIndigo.copy(alpha = 0.5f),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AccentIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "L${profile.level}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.archetype,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${profile.xp} Total XP • ${xpForNextLevel - currentLevelXp} XP to Level ${profile.level + 1}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { levelProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = AccentCyan,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            }
        }

        // Life Score Radar Breakdown
        item {
            val taskRatio = if (tasks.isNotEmpty()) (tasks.count { it.isCompleted }.toFloat() / tasks.size).coerceIn(0f, 1f) else 0f
            val habitRatio = if (habits.isNotEmpty()) (habits.count { it.currentStreak > 0 }.toFloat() / habits.size).coerceIn(0f, 1f) else 0f
            val scoreRatio = (profile.lifeScore / 100f).coerceIn(0f, 1f)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "LIFE SCORE VECTOR BREAKDOWN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
                Spacer(modifier = Modifier.height(12.dp))

                listOf(
                    Triple("Deep Focus & Execution", taskRatio, AccentIndigo),
                    Triple("Cognitive Learning & Study", scoreRatio, AccentCyan),
                    Triple("Atomic Habit Consistency", habitRatio, AccentEmerald),
                    Triple("Physical Vitality & Recovery", 0f, AccentPurple)
                ).forEach { (label, ratio, color) ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "${(ratio * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier.fillMaxWidth().height(5.dp),
                            color = color,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        // Unlocked Badges & Achievements
        item {
            Text(
                text = "SYSTEM MASTERY BADGES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            val badges = listOf(
                Triple("Deep Work Monk", "Completed 50h of uninterrupted flow", profile.xp >= 1000),
                Triple("21-Day Titan", "Maintained unbroken daily habit stack", profile.streakDays >= 21),
                Triple("Systems Architect", "Reach Level 5 in Life OS", profile.level >= 5),
                Triple("Speed Demon", "Execute 10+ tasks in a single day", tasks.count { it.isCompleted } >= 10),
                Triple("Polymath Scholar", "Master 100+ flashcards in Vault", false),
                Triple("Zero Friction", "Completed all daily habits before noon", false)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.forEach { (name, desc, unlocked) ->
                    GlassCard(
                        borderColor = if (unlocked) AccentEmerald.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (unlocked) AccentEmerald.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                shape = CircleShape,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = if (unlocked) Icons.Default.WorkspacePremium else Icons.Default.Lock,
                                    contentDescription = name,
                                    tint = if (unlocked) AccentEmerald else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (unlocked) {
                                Text("UNLOCKED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = AccentEmerald)
                            }
                        }
                    }
                }
            }
        }

        // Theme Engine Selection
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "VISUAL IDENTITY & THEME ENGINE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentIndigo
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Midnight Glass", "Apple Minimal", "Cyberpunk", "Ocean", "Forest").forEach { th ->
                        val isCurrent = profile.currentTheme == th
                        FilledTonalButton(
                            onClick = { viewModel.switchTheme(th) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isCurrent) AccentIndigo else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("theme_btn_$th")
                        ) {
                            Text(
                                text = th.split(" ").first(),
                                fontSize = 10.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { viewModel.resetToBrandNewState() },
                    modifier = Modifier.fillMaxWidth().testTag("reset_clean_install_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRose),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentRose.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset App to Brand-New Installation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
