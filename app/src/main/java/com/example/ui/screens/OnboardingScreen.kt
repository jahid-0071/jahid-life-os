package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiAiService
import com.example.ui.LifeOsViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var selectedArchetype by remember { mutableStateOf("Software Engineer & Systems Architect") }

    // Tab: 0 = Create Custom, 1 = Import / AI Architect
    var onboardingMode by remember { mutableIntStateOf(0) }

    // Mode 0: Custom Goal & Roadmap
    var goalTitle by remember { mutableStateOf("") }
    var goalCategory by remember { mutableStateOf("Engineering") }
    var roadmapTitle by remember { mutableStateOf("") }
    var phase1 by remember { mutableStateOf("Phase 1: Fundamentals & Invariants") }
    var phase2 by remember { mutableStateOf("Phase 2: Core Architecture & Implementation") }
    var phase3 by remember { mutableStateOf("Phase 3: Production Capstone & Verification") }

    // Mode 1: Import / AI Roadmap
    var importTopic by remember { mutableStateOf("") }
    var importNotes by remember { mutableStateOf("") }
    var isGeneratingAi by remember { mutableStateOf(false) }
    var generatedPhases by remember { mutableStateOf<List<String>>(emptyList()) }
    var aiErrorMessage by remember { mutableStateOf<String?>(null) }

    val archetypes = listOf(
        "Software Engineer & Systems Architect",
        "AI Researcher & Data Scientist",
        "Startup Founder & Product Leader",
        "Student & Competitive Scholar",
        "Creative & Polymath"
    )

    val goalCategories = listOf(
        "Engineering", "Learning", "Research", "Career", "Health", "Creative"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo & Header
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(AccentIndigo, AccentCyan)))
                    .border(2.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OS",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "WELCOME TO LIFE OS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = AccentCyan
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Personal Operating System",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Clean, empty installation. Create or import your own roadmaps and vision goals to begin.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step 1: User Identity Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AccentIndigo.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "STEP 1: IDENTITY & ARCHETYPE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentIndigo,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name / Handle") },
                    placeholder = { Text("e.g. Alex, Sarah, Devon") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentIndigo,
                        unfocusedBorderColor = GlassBorder,
                        focusedLabelColor = AccentIndigo,
                        unfocusedLabelColor = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Primary Focus Archetype:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    archetypes.forEach { arch ->
                        val isSelected = selectedArchetype == arch
                        Surface(
                            onClick = { selectedArchetype = arch },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) AccentIndigo.copy(alpha = 0.25f) else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) AccentIndigo else GlassBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) AccentCyan else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = arch,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 2: Choice between Create Custom vs Import / AI Architect
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AccentCyan.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "STEP 2: ROADMAP & GOALS SETUP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Selector Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .padding(4.dp)
                ) {
                    Surface(
                        onClick = { onboardingMode = 0 },
                        modifier = Modifier.weight(1f),
                        color = if (onboardingMode == 0) AccentIndigo else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Create Custom",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (onboardingMode == 0) Color.White else TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Surface(
                        onClick = { onboardingMode = 1 },
                        modifier = Modifier.weight(1f),
                        color = if (onboardingMode == 1) AccentCyan else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Import / AI Plan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (onboardingMode == 1) Color(0xFF0F172A) else TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (onboardingMode == 0) {
                    // MODE 0: Custom Goal & Roadmap
                    Text(
                        text = "Primary Vision Goal (0% Initial Progress)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        placeholder = { Text("e.g. Architect Distributed Storage Engine") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_goal_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = GlassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Category:", fontSize = 11.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        goalCategories.forEach { cat ->
                            val isSel = goalCategory == cat
                            FilterChip(
                                selected = isSel,
                                onClick = { goalCategory = cat },
                                label = { Text(cat, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentIndigo,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Learning / Execution Roadmap Title",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = roadmapTitle,
                        onValueChange = { roadmapTitle = it },
                        placeholder = { Text("e.g. Systems & Kernel Programming Mastery") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_roadmap_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = GlassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Roadmap Phases (All start at 0%):", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = phase1,
                        onValueChange = { phase1 = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentCyan, unfocusedBorderColor = GlassBorder)
                    )
                    OutlinedTextField(
                        value = phase2,
                        onValueChange = { phase2 = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentCyan, unfocusedBorderColor = GlassBorder)
                    )
                    OutlinedTextField(
                        value = phase3,
                        onValueChange = { phase3 = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentCyan, unfocusedBorderColor = GlassBorder)
                    )
                } else {
                    // MODE 1: Import or AI Roadmap
                    Text(
                        text = "Import or Architect with AI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Enter any field, syllabus, or skill to generate a structured 4-phase roadmap.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = importTopic,
                        onValueChange = { importTopic = it },
                        label = { Text("Topic or Specialization") },
                        placeholder = { Text("e.g. Distributed Consensus, Bioengineering, Full-Stack AI") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_ai_topic_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = GlassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = importNotes,
                        onValueChange = { importNotes = it },
                        label = { Text("Existing Syllabus / Outline / Goal Notes (Optional)") },
                        placeholder = { Text("Paste your syllabus topics or custom goal description here...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = GlassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (importTopic.isNotBlank() || importNotes.isNotBlank()) {
                                isGeneratingAi = true
                                aiErrorMessage = null
                                coroutineScope.launch {
                                    try {
                                        val prompt = "Create a structured 4-phase learning/execution roadmap for: '${importTopic.ifEmpty { "General Mastery" }}'. Additional context: '$importNotes'. Return ONLY 4 bullet points, one for each phase, prefixed with 'Phase 1:', 'Phase 2:', etc. Keep each line under 10 words."
                                        val response = GeminiAiService.generateResponse(prompt)
                                        val lines = response.lines()
                                            .map { it.trim().removePrefix("-").removePrefix("*").trim() }
                                            .filter { it.isNotBlank() && (it.contains("Phase") || it.contains(":")) }
                                            .take(4)

                                        if (lines.isNotEmpty()) {
                                            generatedPhases = lines
                                        } else {
                                            generatedPhases = listOf(
                                                "Phase 1: Foundations & Fundamentals",
                                                "Phase 2: Core Implementations & Patterns",
                                                "Phase 3: Scalability & Advanced Architecture",
                                                "Phase 4: Capstone Verification & Mastery"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        generatedPhases = listOf(
                                            "Phase 1: Foundations & Core Concepts",
                                            "Phase 2: Practical Projects & Tooling",
                                            "Phase 3: Advanced Architectures",
                                            "Phase 4: Production Realization"
                                        )
                                    } finally {
                                        isGeneratingAi = false
                                    }
                                }
                            }
                        },
                        enabled = !isGeneratingAi,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_generate_ai_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color(0xFF0F172A))
                    ) {
                        if (isGeneratingAi) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF0F172A),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Architecting Roadmap...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate / Import Roadmap", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (generatedPhases.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Generated Curriculum (Ready to initialize with 0% progress):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        generatedPhases.forEach { phaseText ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = phaseText, fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Initialization CTA
            Button(
                onClick = {
                    val finalName = name.ifBlank { "Architect" }
                    val finalArchetype = selectedArchetype

                    if (onboardingMode == 0) {
                        val phases = listOf(phase1, phase2, phase3).filter { it.isNotBlank() }
                        viewModel.completeOnboarding(
                            name = finalName,
                            title = finalArchetype,
                            archetype = finalArchetype,
                            initialGoalTitle = goalTitle.ifBlank { null },
                            initialGoalCategory = goalCategory,
                            initialRoadmapTitle = roadmapTitle.ifBlank { if (goalTitle.isNotBlank()) "$goalTitle Roadmap" else null },
                            initialRoadmapPhases = phases
                        )
                    } else {
                        val finalRoadmap = importTopic.ifBlank { "Learning Roadmap" }
                        val phases = if (generatedPhases.isNotEmpty()) {
                            generatedPhases
                        } else {
                            listOf(
                                "Phase 1: Fundamental Principles",
                                "Phase 2: Core Engineering & Systems",
                                "Phase 3: Advanced Optimization & Capstones"
                            )
                        }
                        viewModel.completeOnboarding(
                            name = finalName,
                            title = finalArchetype,
                            archetype = finalArchetype,
                            initialGoalTitle = if (importTopic.isNotBlank()) "Master $importTopic" else null,
                            initialGoalCategory = "Learning",
                            initialRoadmapTitle = finalRoadmap,
                            initialRoadmapPhases = phases
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_initialize_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentIndigo,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "INITIALIZE LIFE OS",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clean Slate Skip Option
            TextButton(
                onClick = {
                    viewModel.completeOnboarding(
                        name = name.ifBlank { "Architect" },
                        title = selectedArchetype,
                        archetype = selectedArchetype,
                        initialGoalTitle = null,
                        initialRoadmapTitle = null
                    )
                },
                modifier = Modifier.testTag("onboarding_clean_slate_button")
            ) {
                Text(
                    text = "Or start with 100% clean slate (no initial goals or roadmap)",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
