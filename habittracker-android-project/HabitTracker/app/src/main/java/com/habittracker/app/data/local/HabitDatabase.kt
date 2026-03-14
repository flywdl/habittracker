package com.habittracker.app.data.local

import androidx.room.*

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderTime: String? = null,
    val isActive: Boolean = true
)

@Entity(tableName = "habit_records")
data class HabitRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String,
    val completed: Boolean = true
)

data class HabitWithRecords(
    @Embedded val habit: HabitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val records: List<HabitRecordEntity>
)

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllHabits(): kotlinx.coroutines.flow.Flow<List<HabitEntity>>

    @Transaction
    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllHabitsWithRecords(): kotlinx.coroutines.flow.Flow<List<HabitWithRecords>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HabitRecordEntity): Long

    @Query("SELECT * FROM habit_records WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getRecordForDate(habitId: Long, date: String): HabitRecordEntity?

    @Query("DELETE FROM habit_records WHERE habitId = :habitId AND date = :date")
    suspend fun deleteRecordForDate(habitId: Long, date: String)
}

@Database(
    entities = [HabitEntity::class, HabitRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}
