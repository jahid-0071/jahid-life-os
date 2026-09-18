package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LifeOsViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun StudyScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val courses by viewModel.allCourses.collectAsState()
    val flashcards by viewModel.allFlashcards.collectAsState()
    val activeIndex by viewModel.activeFlashcardIndex.collectAsState()
    val isFlipped by viewModel.isFlashcardFlipped.collectAsState()

    var selectedTab by remember { mutableStateOf("FLASHCARDS") } // FLASHCARDS, COURSES

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
                    text = "STUDY & ACTIVE RECALL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = AccentAmber
                )
                Text(
                    text = "Spaced Repetition & Coursework",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Mode Switcher Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("FLASHCARDS" to "Active Recall Cards", "COURSES" to "Enrolled Courses & CGPA").forEach { (tab, label) ->
                    val isSelected = selectedTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentAmber,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.Black
                        )
                    )
                }
            }
        }

        if (selectedTab == "FLASHCARDS") {
            if (flashcards.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Style, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No flashcards in study deck (0 cards)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create or import flashcards to activate active recall spaced repetition.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                val currentCard = flashcards.getOrNull(activeIndex.coerceIn(0, flashcards.size - 1))

                item {
                    GlassCard(
                        borderColor = AccentAmber.copy(alpha = 0.5f),
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { viewModel.flipFlashcard() },
                        modifier = Modifier.fillMaxWidth().height(220.dp).testTag("flashcard_box")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentCard?.courseCode ?: "CS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentAmber
                                )
                                Text(
                                    text = "Card ${activeIndex + 1} of ${flashcards.size}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedContent(targetState = isFlipped, label = "card_flip") { flipped ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = if (flipped) "ANSWER" else "QUESTION (Tap to reveal)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = if (flipped) AccentEmerald else AccentAmber
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (flipped) (currentCard?.answer ?: "") else (currentCard?.question ?: ""),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isFlipped) "Tap to hide answer" else "Tap card to flip",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Active Recall Rating Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.nextFlashcard(flashcards.size) },
                            colors = ButtonDefaults.buttonColors(containerColor = PriorityP1.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hard (1d)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.nextFlashcard(flashcards.size) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Good (3d)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Black)
                        }
                        Button(
                            onClick = { viewModel.nextFlashcard(flashcards.size) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Easy (7d)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Courses & CGPA
            val cgpaText = if (courses.isEmpty()) "0.0 / 4.0" else "4.0 / 4.0"
            item {
                GlassCard(borderColor = AccentAmber.copy(alpha = 0.4f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACADEMIC EXCELLENCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentAmber
                            )
                            Text(
                                text = "Cumulative CGPA",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = cgpaText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (courses.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else AccentEmerald
                        )
                    }
                }
            }

            if (courses.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No enrolled courses (0 credits)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add university or independent study courses to track syllabus and exams.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(courses) { course ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AccentAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = course.courseCode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "Grade: ${course.currentGrade}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = course.courseName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Instructor: ${course.instructor} • ${course.creditHours} Credits",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (course.nextExamDate.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = PriorityP2, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Exam Date: ${course.nextExamDate}",
                                fontSize = 11.sp,
                                color = PriorityP2,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
