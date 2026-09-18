package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiService
import com.example.data.AppDatabase
import com.example.data.LifeOsRepository
import com.example.data.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NavigationModule(val label: String, val icon: String, val badge: String? = null) {
    DASHBOARD("Control", "dashboard"),
    PLANNER("Planner", "view_timeline"),
    TASKS("Tasks", "task_alt"),
    PROJECTS("Projects", "folder_special"),
    ROADMAPS("Roadmaps", "schema"),
    HABITS("Habits", "repeat"),
    STUDY("Study", "school"),
    FOCUS("Focus", "timer"),
    AI_ASSISTANT("AI OS", "auto_awesome", "Pro"),
    VAULT("Vault", "inventory_2"),
    JOURNAL("Journal", "edit_note"),
    HEALTH_FINANCE("Life Metrics", "monitoring"),
    ANALYTICS("Analytics", "insights")
}

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class LifeOsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = LifeOsRepository(database)

    // Current navigation tab
    private val _currentModule = MutableStateFlow(NavigationModule.DASHBOARD)
    val currentModule: StateFlow<NavigationModule> = _currentModule.asStateFlow()

    fun navigateTo(module: NavigationModule) {
        _currentModule.value = module
    }

    // User Profile
    val userProfile = repository.userProfile.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        UserProfileEntity()
    )

    // Data streams
    val allTasks = repository.allTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSubtasks = repository.allSubtasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allHabits = repository.allHabits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allProjects = repository.allProjects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allGoals = repository.allGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPhases = repository.allRoadmapPhases.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allCourses = repository.allCourses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allFlashcards = repository.allFlashcards.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allNotes = repository.allNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allJournalEntries = repository.allJournalEntries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recentHealthFinance = repository.recentHealthFinanceLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recentFocusSessions = repository.recentFocusSessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    fun setSearchQuery(query: String) { _searchQuery.value = query }

    // Focus Mode Timer State
    private val _focusTimerMinutes = MutableStateFlow(25)
    val focusTimerMinutes = _focusTimerMinutes.asStateFlow()

    private val _focusSecondsRemaining = MutableStateFlow(25 * 60)
    val focusSecondsRemaining = _focusSecondsRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private val _selectedFocusMode = MutableStateFlow("Pomodoro") // Pomodoro, Deep Work, Flow
    val selectedFocusMode = _selectedFocusMode.asStateFlow()

    private val _distractionCount = MutableStateFlow(0)
    val distractionCount = _distractionCount.asStateFlow()

    private var timerJob: Job? = null

    fun setFocusMode(mode: String) {
        _selectedFocusMode.value = mode
        val mins = when (mode) {
            "Pomodoro" -> 25
            "Deep Work" -> 50
            "Flow" -> 90
            else -> 25
        }
        _focusTimerMinutes.value = mins
        _focusSecondsRemaining.value = mins * 60
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun toggleFocusTimer() {
        if (_isTimerRunning.value) {
            _isTimerRunning.value = false
            timerJob?.cancel()
        } else {
            _isTimerRunning.value = true
            timerJob = viewModelScope.launch {
                while (_focusSecondsRemaining.value > 0 && _isTimerRunning.value) {
                    delay(1000)
                    _focusSecondsRemaining.value -= 1
                }
                if (_focusSecondsRemaining.value <= 0) {
                    _isTimerRunning.value = false
                    // Record session & grant XP
                    repository.recordFocusSession(
                        FocusSessionEntity(
                            taskTitle = "Deep Focus Block",
                            sessionType = _selectedFocusMode.value,
                            durationMinutes = _focusTimerMinutes.value,
                            distractionsCount = _distractionCount.value
                        )
                    )
                    addXp(_focusTimerMinutes.value * 3)
                }
            }
        }
    }

    fun resetFocusTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        _focusSecondsRemaining.value = _focusTimerMinutes.value * 60
        _distractionCount.value = 0
    }

    fun logDistraction() {
        _distractionCount.value += 1
    }

    // AI Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("ai", "Greetings. I am Life OS Core Intelligence. How may I assist your strategy, roadmap architecture, and goals today?")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking = _isAiThinking.asStateFlow()

    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank()) return
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(ChatMessage("user", prompt))
        _chatMessages.value = currentList
        _isAiThinking.value = true

        viewModelScope.launch {
            val response = GeminiAiService.generateResponse(
                prompt = prompt,
                systemInstruction = "You are Life OS, the world's most sophisticated personal operating system AI. You combine principles from Cal Newport (Deep Work), Tiago Forte (Second Brain), Andrew Huberman (Neurobiology), and elite systems engineering. Give crisp, strategic, actionable insights with high aesthetic typography formatting."
            )
            _isAiThinking.value = false
            val updated = _chatMessages.value.toMutableList()
            updated.add(ChatMessage("ai", response))
            _chatMessages.value = updated
            addXp(20)
        }
    }

    // Quick AI Actions
    fun breakDownGoalWithAi(goalTitle: String) {
        sendAiPrompt("Break down this ambitious goal into strategic phases and atomic daily tasks: '$goalTitle'")
    }

    fun generateRoadmapWithAi(skill: String) {
        sendAiPrompt("Generate a comprehensive, phase-by-phase learning and execution roadmap for: '$skill'. Include core concepts, advanced challenges, and practical verification projects.")
    }

    fun requestProductivityCoaching() {
        sendAiPrompt("Conduct an executive productivity review of my cognitive day. Review my focus, energy allocation, and suggest 3 high-leverage tactical optimizations.")
    }

    // Task Actions
    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskComplete(task)
            if (!task.isCompleted) addXp(50)
        }
    }

    fun addTask(
        title: String,
        description: String,
        category: String,
        taskType: String,
        priority: Int,
        estimatedMinutes: Int,
        timeOfDay: String,
        horizon: String,
        subtasks: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            val taskId = repository.insertTask(
                TaskEntity(
                    title = title,
                    description = description,
                    category = category,
                    taskType = taskType,
                    priority = priority,
                    estimatedMinutes = estimatedMinutes,
                    timeOfDay = timeOfDay,
                    horizon = horizon
                )
            )
            subtasks.forEach { sub ->
                if (sub.isNotBlank()) {
                    repository.insertSubtask(SubtaskEntity(taskId = taskId, title = sub.trim()))
                }
            }
            addXp(30)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun toggleSubtask(subtask: SubtaskEntity) {
        viewModelScope.launch { repository.toggleSubtask(subtask) }
    }

    // Habit Actions
    fun checkInHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.toggleHabitCheckin(habit)
            addXp(25)
        }
    }

    fun addHabit(name: String, targetCount: Int, unit: String, category: String, iconKey: String) {
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(
                    name = name,
                    targetCount = targetCount,
                    unit = unit,
                    category = category,
                    iconKey = iconKey
                )
            )
            addXp(40)
        }
    }

    // Project Actions
    fun addProject(title: String, description: String, category: String, deadline: String, github: String) {
        viewModelScope.launch {
            repository.insertProject(
                ProjectEntity(
                    title = title,
                    description = description,
                    category = category,
                    deadline = deadline,
                    githubRepo = github
                )
            )
            addXp(50)
        }
    }

    fun updateProjectProgress(project: ProjectEntity, progress: Int) {
        viewModelScope.launch {
            repository.updateProject(project.copy(progress = progress.coerceIn(0, 100)))
        }
    }

    // Goal Actions
    fun addGoal(title: String, description: String, horizon: String, targetYear: Int, category: String, metric: String) {
        viewModelScope.launch {
            repository.insertGoal(
                LifeGoalEntity(
                    title = title,
                    description = description,
                    horizon = horizon,
                    targetYear = targetYear,
                    category = category,
                    keyMetric = metric
                )
            )
            addXp(60)
        }
    }

    fun updateGoalProgress(goal: LifeGoalEntity, progress: Int) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(progress = progress.coerceIn(0, 100)))
        }
    }

    // Roadmap Actions
    fun updatePhaseProgress(phase: RoadmapPhaseEntity, delta: Int) {
        viewModelScope.launch {
            val newProg = (phase.progress + delta).coerceIn(0, 100)
            repository.updateRoadmapPhase(phase.copy(progress = newProg, isCompleted = newProg >= 100))
            if (newProg >= 100 && !phase.isCompleted) addXp(150)
        }
    }

    // Study & Flashcard Actions
    private val _activeFlashcardIndex = MutableStateFlow(0)
    val activeFlashcardIndex = _activeFlashcardIndex.asStateFlow()

    private val _isFlashcardFlipped = MutableStateFlow(false)
    val isFlashcardFlipped = _isFlashcardFlipped.asStateFlow()

    fun flipFlashcard() {
        _isFlashcardFlipped.value = !_isFlashcardFlipped.value
    }

    fun nextFlashcard(total: Int) {
        if (total > 0) {
            _activeFlashcardIndex.value = (_activeFlashcardIndex.value + 1) % total
            _isFlashcardFlipped.value = false
            addXp(15)
        }
    }

    fun addCourse(code: String, name: String, grade: String, exam: String, summary: String) {
        viewModelScope.launch {
            repository.insertCourse(
                StudyCourseEntity(courseCode = code, courseName = name, currentGrade = grade, nextExamDate = exam, syllabusSummary = summary)
            )
            addXp(40)
        }
    }

    fun addFlashcard(code: String, q: String, a: String, difficulty: String) {
        viewModelScope.launch {
            repository.insertFlashcard(FlashcardEntity(courseCode = code, question = q, answer = a, difficulty = difficulty))
            addXp(20)
        }
    }

    // Vault Notes
    fun addNote(title: String, content: String, category: String, tags: String) {
        viewModelScope.launch {
            repository.insertNote(VaultNoteEntity(title = title, content = content, category = category, tags = tags))
            addXp(35)
        }
    }

    // Journal
    fun addJournalEntry(mood: Int, energy: Int, wins: String, challenges: String, lessons: String, gratitude: String) {
        viewModelScope.launch {
            repository.insertJournalEntry(
                JournalEntryEntity(
                    dateString = "Today",
                    moodScore = mood,
                    energyScore = energy,
                    wins = wins,
                    challenges = challenges,
                    lessonsLearned = lessons,
                    gratitude = gratitude
                )
            )
            addXp(50)
        }
    }

    // Health & Finance
    fun addWaterGlass() {
        viewModelScope.launch {
            val current = recentHealthFinance.value.firstOrNull() ?: HealthFinanceEntity(dateString = "Today")
            repository.updateHealthFinance(current.copy(waterGlasses = current.waterGlasses + 1))
            addXp(10)
        }
    }

    fun logHealthMetrics(sleep: Float, steps: Int, workout: Int, stress: Int) {
        viewModelScope.launch {
            val current = recentHealthFinance.value.firstOrNull() ?: HealthFinanceEntity(dateString = "Today")
            repository.updateHealthFinance(current.copy(sleepHours = sleep, steps = steps, workoutMinutes = workout, stressLevel = stress))
            addXp(30)
        }
    }

    fun logTransaction(income: Double, expense: Double) {
        viewModelScope.launch {
            val current = recentHealthFinance.value.firstOrNull() ?: HealthFinanceEntity(dateString = "Today")
            repository.updateHealthFinance(
                current.copy(
                    income = current.income + income,
                    expenses = current.expenses + expense,
                    savings = (current.income + income) - (current.expenses + expense)
                )
            )
            addXp(25)
        }
    }

    // Theme & Profile
    fun switchTheme(theme: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(current.copy(currentTheme = theme))
        }
    }

    fun completeOnboarding(
        name: String,
        title: String,
        archetype: String,
        initialGoalTitle: String? = null,
        initialGoalCategory: String = "Life Vision",
        initialRoadmapTitle: String? = null,
        initialRoadmapPhases: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            val profile = (userProfile.value ?: UserProfileEntity()).copy(
                name = name.trim().ifEmpty { "Builder" },
                title = title.trim().ifEmpty { archetype.ifEmpty { "Polymath & Builder" } },
                archetype = archetype.ifEmpty { "High-Leverage Builder" },
                xp = 0,
                level = 1,
                streakDays = 0,
                lifeScore = 0,
                isOnboarded = true
            )
            repository.updateProfile(profile)

            // If user specified an initial goal
            if (!initialGoalTitle.isNullOrBlank()) {
                repository.insertGoal(
                    LifeGoalEntity(
                        title = initialGoalTitle.trim(),
                        description = "Milestone established during onboarding.",
                        horizon = "YEARLY",
                        targetYear = 2026,
                        category = initialGoalCategory,
                        progress = 0,
                        keyMetric = "0% progress"
                    )
                )
            }

            // If user specified or imported a roadmap
            if (!initialRoadmapTitle.isNullOrBlank()) {
                val phases = if (initialRoadmapPhases.isNotEmpty()) {
                    initialRoadmapPhases
                } else {
                    listOf(
                        "Foundations & Principles",
                        "Core Systems & Implementation",
                        "Advanced Specialization & Capstones"
                    )
                }
                phases.forEachIndexed { index, phaseTitle ->
                    repository.insertRoadmapPhase(
                        RoadmapPhaseEntity(
                            roadmapTitle = initialRoadmapTitle.trim(),
                            phaseNumber = index + 1,
                            phaseTitle = phaseTitle.trim(),
                            description = "Initial phase for ${initialRoadmapTitle.trim()}",
                            topicsJson = "",
                            progress = 0,
                            isUnlocked = index == 0,
                            isCompleted = false
                        )
                    )
                }
            }
        }
    }

    fun resetToBrandNewState() {
        viewModelScope.launch {
            repository.clearDatabase()
            _chatMessages.value = listOf(
                ChatMessage("ai", "Welcome to Life OS. I am your strategic AI Copilot. Establish your long-term goals and roadmaps to begin.")
            )
        }
    }

    private fun addXp(amount: Int) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val newXp = current.xp + amount
            val newLevel = (newXp / 300) + 1
            repository.updateProfile(current.copy(xp = newXp, level = newLevel))
        }
    }
}
