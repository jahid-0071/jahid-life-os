package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LifeOsViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun FocusScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val secondsRemaining by viewModel.focusSecondsRemaining.collectAsState()
    val totalMinutes by viewModel.focusTimerMinutes.collectAsState()
    val selectedMode by viewModel.selectedFocusMode.collectAsState()
    val distractionCount by viewModel.distractionCount.collectAsState()

    var showBreathingGuide by remember { mutableStateOf(false) }
    var selectedSound by remember { mutableStateOf("Lo-Fi Study") }

    val mins = secondsRemaining / 60
    val secs = secondsRemaining % 60
    val progress = if (totalMinutes > 0) 1f - (secondsRemaining.toFloat() / (totalMinutes * 60)) else 0f

    // Animated Breathing Pulsing Circle
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NEUROLOGICAL FOCUS CHAMBER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = AccentIndigo
                )
                Text(
                    text = "Ultradian Flow Engine",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Mode Switcher (Pomodoro 25, Deep Work 50, Flow 90)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("Pomodoro", "Deep Work", "Flow").forEach { mode ->
                    val isSelected = selectedMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFocusMode(mode) },
                        label = { Text(mode, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentIndigo,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp).testTag("focus_mode_$mode")
                    )
                }
            }
        }

        // Giant Circular Focus Timer
        item {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    // Background Ring
                    drawArc(
                        color = Color(0xFF1E2337),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = stroke
                    )
                    // Active Progress Ring
                    drawArc(
                        brush = Brush.sweepGradient(listOf(AccentIndigo, AccentCyan, AccentIndigo)),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = stroke
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRunning) "FLOW STATE ACTIVE" else "PAUSED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isRunning) AccentEmerald else AccentAmber
                    )
                }
            }
        }

        // Timer Controls
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = { viewModel.resetFocusTimer() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.onSurface)
                }

                Button(
                    onClick = { viewModel.toggleFocusTimer() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) AccentAmber else AccentIndigo
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(48.dp).padding(horizontal = 12.dp).testTag("timer_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "Pause Session" else "Start Flow State",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedIconButton(
                    onClick = { viewModel.logDistraction() },
                    modifier = Modifier.size(48.dp).testTag("log_distraction_button")
                ) {
                    Icon(Icons.Default.NotificationsOff, contentDescription = "Log Distraction", tint = PriorityP1)
                }
            }
        }

        item {
            Text(
                text = "Distraction Counter: $distractionCount logged",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Breathing Exercise Guide
        item {
            GlassCard(
                borderColor = AccentCyan.copy(alpha = 0.4f),
                onClick = { showBreathingGuide = !showBreathingGuide },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = AccentCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "4-7-8 Physiological Breathwork",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = if (showBreathingGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (showBreathingGuide) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .scale(breathScale)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(AccentCyan.copy(alpha = 0.4f), Color.Transparent)))
                                .border(2.dp, AccentCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Breathe",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Inhale (4s) → Hold (7s) → Exhale (8s)\nCalms the nervous system and re-anchors dopamine tone.",
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Ambient Sound Environment selector
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "NEURO-ACOUSTIC SOUNDSCAPE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentIndigo
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Lo-Fi Study", "Deep Rain", "White Noise", "Deep Space").forEach { sound ->
                        val isSelected = selectedSound == sound
                        Surface(
                            onClick = { selectedSound = sound },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) AccentIndigo.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) AccentIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.VolumeUp else Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = if (isSelected) AccentIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sound,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) AccentIndigo else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
