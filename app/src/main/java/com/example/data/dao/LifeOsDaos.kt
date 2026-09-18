package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY priority ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE horizon = :horizon ORDER BY priority ASC, createdAt DESC")
    fun getTasksByHorizon(horizon: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY priority ASC, createdAt DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY priority ASC")
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    // Subtasks
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    fun getSubtasksForTask(taskId: Long): Flow<List<SubtaskEntity>>

    @Query("SELECT * FROM subtasks")
    fun getAllSubtasks(): Flow<List<SubtaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubtaskEntity): Long

    @Update
    suspend fun updateSubtask(subtask: SubtaskEntity)

    @Delete
    suspend fun deleteSubtask(subtask: SubtaskEntity)
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY currentStreak DESC, id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    // Habit logs
    @Query("SELECT * FROM habit_logs WHERE dateString = :date")
    fun getLogsForDate(date: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY dateString DESC")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs")
    fun getAllHabitLogs(): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity): Long

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateString = :date")
    suspend fun deleteLog(habitId: Long, date: String)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY progress ASC, id DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM life_goals ORDER BY horizon ASC, targetYear ASC")
    fun getAllGoals(): Flow<List<LifeGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: LifeGoalEntity): Long

    @Update
    suspend fun updateGoal(goal: LifeGoalEntity)

    @Delete
    suspend fun deleteGoal(goal: LifeGoalEntity)
}

@Dao
interface RoadmapDao {
    @Query("SELECT * FROM roadmap_phases ORDER BY phaseNumber ASC")
    fun getAllPhases(): Flow<List<RoadmapPhaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhase(phase: RoadmapPhaseEntity): Long

    @Update
    suspend fun updatePhase(phase: RoadmapPhaseEntity)

    @Delete
    suspend fun deletePhase(phase: RoadmapPhaseEntity)
}

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_courses")
    fun getAllCourses(): Flow<List<StudyCourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: StudyCourseEntity): Long

    @Update
    suspend fun updateCourse(course: StudyCourseEntity)

    @Delete
    suspend fun deleteCourse(course: StudyCourseEntity)

    @Query("SELECT * FROM flashcards WHERE courseCode = :courseCode")
    fun getFlashcardsForCourse(courseCode: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(card: FlashcardEntity): Long

    @Update
    suspend fun updateFlashcard(card: FlashcardEntity)

    @Delete
    suspend fun deleteFlashcard(card: FlashcardEntity)
}

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<VaultNoteEntity>>

    @Query("SELECT * FROM vault_notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<VaultNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: VaultNoteEntity): Long

    @Update
    suspend fun updateNote(note: VaultNoteEntity)

    @Delete
    suspend fun deleteNote(note: VaultNoteEntity)
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE dateString = :date LIMIT 1")
    suspend fun getEntryByDate(date: String): JournalEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: JournalEntryEntity)
}

@Dao
interface HealthFinanceDao {
    @Query("SELECT * FROM health_finance_logs ORDER BY dateString DESC LIMIT 30")
    fun getRecentLogs(): Flow<List<HealthFinanceEntity>>

    @Query("SELECT * FROM health_finance_logs WHERE dateString = :date LIMIT 1")
    suspend fun getLogByDate(date: String): HealthFinanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: HealthFinanceEntity): Long
}

@Dao
interface FocusDao {
    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC LIMIT 50")
    fun getRecentSessions(): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}
