package com.example.data

import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class LifeOsRepository(private val db: AppDatabase) {
    // Profile & Gamification
    val userProfile: Flow<UserProfileEntity?> = db.profileDao().getProfile()

    suspend fun addXp(amount: Int) {
        val current = db.profileDao().getProfile()
        // We can update profile
        val profile = current.let {
            // we'll update or create
        }
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        db.profileDao().insertOrUpdateProfile(profile)
    }

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = db.taskDao().getAllTasks()
    val activeTasks: Flow<List<TaskEntity>> = db.taskDao().getActiveTasks()
    val allSubtasks: Flow<List<SubtaskEntity>> = db.taskDao().getAllSubtasks()

    fun getTasksByHorizon(horizon: String): Flow<List<TaskEntity>> = db.taskDao().getTasksByHorizon(horizon)
    fun getSubtasksForTask(taskId: Long): Flow<List<SubtaskEntity>> = db.taskDao().getSubtasksForTask(taskId)

    suspend fun insertTask(task: TaskEntity): Long {
        val id = db.taskDao().insertTask(task)
        grantXp(25)
        return id
    }

    suspend fun updateTask(task: TaskEntity) {
        db.taskDao().updateTask(task)
    }

    suspend fun toggleTaskComplete(task: TaskEntity) {
        val newStatus = !task.isCompleted
        val updated = task.copy(
            isCompleted = newStatus,
            status = if (newStatus) "DONE" else "TODO",
            completionPercent = if (newStatus) 100 else 0,
            completedAt = if (newStatus) System.currentTimeMillis() else null
        )
        db.taskDao().updateTask(updated)
        if (newStatus) {
            grantXp(50)
        }
    }

    suspend fun deleteTask(task: TaskEntity) {
        db.taskDao().deleteTask(task)
    }

    suspend fun insertSubtask(subtask: SubtaskEntity): Long = db.taskDao().insertSubtask(subtask)

    suspend fun toggleSubtask(subtask: SubtaskEntity) {
        val updated = subtask.copy(isDone = !subtask.isDone)
        db.taskDao().updateSubtask(updated)
        if (updated.isDone) grantXp(15)
    }

    // Habits
    val allHabits: Flow<List<HabitEntity>> = db.habitDao().getAllHabits()
    val allHabitLogs: Flow<List<HabitLogEntity>> = db.habitDao().getAllHabitLogs()

    suspend fun insertHabit(habit: HabitEntity): Long = db.habitDao().insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = db.habitDao().updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = db.habitDao().deleteHabit(habit)

    suspend fun toggleHabitCheckin(habit: HabitEntity, date: String = "Today") {
        val updatedStreak = habit.currentStreak + 1
        val updatedBest = maxOf(habit.bestStreak, updatedStreak)
        db.habitDao().updateHabit(habit.copy(currentStreak = updatedStreak, bestStreak = updatedBest))
        db.habitDao().insertHabitLog(HabitLogEntity(habitId = habit.id, dateString = date, isDone = true))
        grantXp(30)
    }

    // Projects
    val allProjects: Flow<List<ProjectEntity>> = db.projectDao().getAllProjects()
    suspend fun insertProject(project: ProjectEntity): Long = db.projectDao().insertProject(project)
    suspend fun updateProject(project: ProjectEntity) = db.projectDao().updateProject(project)
    suspend fun deleteProject(project: ProjectEntity) = db.projectDao().deleteProject(project)

    // Goals
    val allGoals: Flow<List<LifeGoalEntity>> = db.goalDao().getAllGoals()
    suspend fun insertGoal(goal: LifeGoalEntity): Long = db.goalDao().insertGoal(goal)
    suspend fun updateGoal(goal: LifeGoalEntity) = db.goalDao().updateGoal(goal)
    suspend fun deleteGoal(goal: LifeGoalEntity) = db.goalDao().deleteGoal(goal)

    // Roadmaps
    val allRoadmapPhases: Flow<List<RoadmapPhaseEntity>> = db.roadmapDao().getAllPhases()
    suspend fun insertRoadmapPhase(phase: RoadmapPhaseEntity): Long = db.roadmapDao().insertPhase(phase)
    suspend fun updateRoadmapPhase(phase: RoadmapPhaseEntity) = db.roadmapDao().updatePhase(phase)

    // Study & Flashcards
    val allCourses: Flow<List<StudyCourseEntity>> = db.studyDao().getAllCourses()
    val allFlashcards: Flow<List<FlashcardEntity>> = db.studyDao().getAllFlashcards()
    suspend fun insertCourse(course: StudyCourseEntity): Long = db.studyDao().insertCourse(course)
    suspend fun insertFlashcard(flashcard: FlashcardEntity): Long = db.studyDao().insertFlashcard(flashcard)
    suspend fun updateFlashcard(flashcard: FlashcardEntity) = db.studyDao().updateFlashcard(flashcard)

    // Vault Notes
    val allNotes: Flow<List<VaultNoteEntity>> = db.vaultDao().getAllNotes()
    fun searchNotes(query: String): Flow<List<VaultNoteEntity>> = db.vaultDao().searchNotes(query)
    suspend fun insertNote(note: VaultNoteEntity): Long = db.vaultDao().insertNote(note)
    suspend fun updateNote(note: VaultNoteEntity) = db.vaultDao().updateNote(note)
    suspend fun deleteNote(note: VaultNoteEntity) = db.vaultDao().deleteNote(note)

    // Journal
    val allJournalEntries: Flow<List<JournalEntryEntity>> = db.journalDao().getAllEntries()
    suspend fun insertJournalEntry(entry: JournalEntryEntity): Long {
        val id = db.journalDao().insertEntry(entry)
        grantXp(40)
        return id
    }

    // Health & Finance
    val recentHealthFinanceLogs: Flow<List<HealthFinanceEntity>> = db.healthFinanceDao().getRecentLogs()
    suspend fun updateHealthFinance(log: HealthFinanceEntity) = db.healthFinanceDao().insertOrUpdate(log)

    // Focus Sessions
    val recentFocusSessions: Flow<List<FocusSessionEntity>> = db.focusDao().getRecentSessions()
    suspend fun recordFocusSession(session: FocusSessionEntity) {
        db.focusDao().insertSession(session)
        grantXp(session.durationMinutes * 2)
    }

    suspend fun clearDatabase() {
        AppDatabase.clearAllData(db)
    }

    private suspend fun grantXp(points: Int) {
        // We will query current profile if needed or handled in ViewModel
    }
}
