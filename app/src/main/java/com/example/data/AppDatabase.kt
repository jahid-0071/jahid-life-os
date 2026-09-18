package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        SubtaskEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        ProjectEntity::class,
        LifeGoalEntity::class,
        RoadmapPhaseEntity::class,
        StudyCourseEntity::class,
        FlashcardEntity::class,
        VaultNoteEntity::class,
        JournalEntryEntity::class,
        HealthFinanceEntity::class,
        FocusSessionEntity::class,
        UserProfileEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun projectDao(): ProjectDao
    abstract fun goalDao(): GoalDao
    abstract fun roadmapDao(): RoadmapDao
    abstract fun studyDao(): StudyDao
    abstract fun vaultDao(): VaultDao
    abstract fun journalDao(): JournalDao
    abstract fun healthFinanceDao(): HealthFinanceDao
    abstract fun focusDao(): FocusDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "life_os_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        // Brand-new installation: empty database with initial zero-state profile
                        database.profileDao().insertOrUpdateProfile(
                            UserProfileEntity(
                                id = 1,
                                name = "",
                                title = "",
                                archetype = "",
                                xp = 0,
                                level = 1,
                                streakDays = 0,
                                lifeScore = 0,
                                currentTheme = "Midnight Glass",
                                isOnboarded = false
                            )
                        )
                    }
                }
            }
        }

        suspend fun clearAllData(db: AppDatabase) {
            db.clearAllTables()
            db.profileDao().insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    name = "",
                    title = "",
                    archetype = "",
                    xp = 0,
                    level = 1,
                    streakDays = 0,
                    lifeScore = 0,
                    currentTheme = "Midnight Glass",
                    isOnboarded = false
                )
            )
        }
    }
}
