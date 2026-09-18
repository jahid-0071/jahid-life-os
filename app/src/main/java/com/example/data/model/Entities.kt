package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SmartTaskType(val label: String, val icon: String) {
    QUICK("Quick Task", "flash_on"),
    DEEP_WORK("Deep Work", "psychology"),
    STUDY("Study", "menu_book"),
    CODING("Coding", "terminal"),
    READING("Reading", "auto_stories"),
    PROJECT("Project", "account_tree"),
    HABIT("Habit Task", "check_circle"),
    FITNESS("Fitness", "fitness_center"),
    RESEARCH("Research", "science"),
    EXAM("Exam Prep", "quiz"),
    MEETING("Meeting", "group"),
    PERSONAL("Personal", "person")
}

enum class PriorityLevel(val level: Int, val label: String, val colorHex: String) {
    P1(1, "P1 Urgent", "#FF3B30"),
    P2(2, "P2 High", "#FF9500"),
    P3(3, "P3 Medium", "#FFCC00"),
    P4(4, "P4 Normal", "#34C759"),
    P5(5, "P5 Low", "#5856D6")
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General",
    val taskType: String = SmartTaskType.DEEP_WORK.name,
    val priority: Int = 2,
    val difficulty: String = "Medium",
    val estimatedMinutes: Int = 45,
    val actualMinutes: Int = 0,
    val energyLevel: String = "High",
    val status: String = "TODO", // BACKLOG, TODO, IN_PROGRESS, DONE
    val dueDate: String = "",
    val timeOfDay: String = "Morning", // Morning, Afternoon, Evening, Night
    val horizon: String = "DAILY", // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY, VISION
    val goalId: Long? = null,
    val projectId: Long? = null,
    val tags: String = "",
    val completionPercent: Int = 0,
    val moodBefore: String? = null,
    val moodAfter: String? = null,
    val aiNotes: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(tableName = "subtasks")
data class SubtaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val title: String,
    val isDone: Boolean = false
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconKey: String = "water", // water, read, gym, code, meditate, sleep, walk, pray, study
    val frequency: String = "DAILY", // DAILY, WEEKLY, MONTHLY
    val targetCount: Int = 1,
    val unit: String = "times",
    val timeOfDay: String = "Morning",
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val category: String = "Health",
    val colorHex: String = "#0A84FF",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateString: String, // YYYY-MM-DD
    val count: Int = 1,
    val isDone: Boolean = true
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val status: String = "In Progress", // Planning, In Progress, Review, Completed
    val progress: Int = 0, // 0 - 100
    val deadline: String = "",
    val githubRepo: String = "",
    val category: String = "Engineering",
    val colorHex: String = "#6366F1",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "life_goals")
data class LifeGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val horizon: String = "YEARLY", // QUARTERLY, YEARLY, MULTI_YEAR, VISION
    val targetYear: Int = 2026,
    val category: String = "Career",
    val progress: Int = 0,
    val keyMetric: String = ""
)

@Entity(tableName = "roadmap_phases")
data class RoadmapPhaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roadmapTitle: String, // e.g. "Software Engineering Mastery"
    val phaseNumber: Int,
    val phaseTitle: String,
    val description: String = "",
    val topicsJson: String = "", // comma-separated or list of topics
    val progress: Int = 0,
    val isUnlocked: Boolean = true,
    val isCompleted: Boolean = false
)

@Entity(tableName = "study_courses")
data class StudyCourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseCode: String,
    val courseName: String,
    val creditHours: Int = 3,
    val currentGrade: String = "A",
    val instructor: String = "",
    val nextExamDate: String = "",
    val syllabusSummary: String = ""
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseCode: String,
    val question: String,
    val answer: String,
    val difficulty: String = "Medium", // Easy, Medium, Hard
    val reviewCount: Int = 0,
    val lastReviewed: Long = 0
)

@Entity(tableName = "vault_notes")
data class VaultNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Wiki", // Wiki, Code, Research, Book, Idea
    val tags: String = "",
    val isPinned: Boolean = false,
    val isBookmarked: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String,
    val moodScore: Int = 4, // 1 to 5
    val energyScore: Int = 4, // 1 to 5
    val wins: String = "",
    val challenges: String = "",
    val lessonsLearned: String = "",
    val gratitude: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "health_finance_logs")
data class HealthFinanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String = "Today",
    val sleepHours: Float = 0f,
    val waterGlasses: Int = 0,
    val steps: Int = 0,
    val workoutMinutes: Int = 0,
    val calories: Int = 0,
    val stressLevel: Int = 0,
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val savings: Double = 0.0
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskTitle: String,
    val sessionType: String = "Pomodoro", // Pomodoro, Deep Work, Flow
    val durationMinutes: Int = 25,
    val distractionsCount: Int = 0,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val title: String = "",
    val archetype: String = "",
    val xp: Int = 0,
    val level: Int = 1,
    val streakDays: Int = 0,
    val lifeScore: Int = 0,
    val currentTheme: String = "Midnight Glass",
    val isOnboarded: Boolean = false
)
