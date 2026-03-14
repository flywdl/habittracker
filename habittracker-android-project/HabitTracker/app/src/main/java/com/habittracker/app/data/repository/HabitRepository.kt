package com.habittracker.app.data.repository

import com.habittracker.app.data.local.HabitDao
import com.habittracker.app.data.local.HabitEntity
import com.habittracker.app.data.local.HabitRecordEntity
import com.habittracker.app.data.local.HabitWithRecords
import com.habittracker.app.domain.model.Habit
import com.habittracker.app.domain.model.HabitRecord
import com.habittracker.app.domain.model.HabitWithStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitRepository(private val habitDao: HabitDao) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getAllHabitsWithStats(): Flow<List<HabitWithStats>> {
        return habitDao.getAllHabitsWithRecords().map { habitsWithRecords ->
            habitsWithRecords.map { habitWithRecords ->
                val habit = habitWithRecords.habit
                val records = habitWithRecords.records
                
                val today = LocalDate.now().format(dateFormatter)
                val todayCompleted = records.any { it.date == today && it.completed }
                
                val sortedDates = records
                    .filter { it.completed }
                    .map { LocalDate.parse(it.date, dateFormatter) }
                    .sortedDescending()
                
                var currentStreak = 0
                var checkDate = LocalDate.now()
                
                if (todayCompleted) {
                    currentStreak = 1
                    checkDate = checkDate.minusDays(1)
                }
                
                for (date in sortedDates) {
                    if (date == checkDate) {
                        currentStreak++
                        checkDate = checkDate.minusDays(1)
                    } else {
                        break
                    }
                }
                
                val totalDays = LocalDate.parse(habit.createdAt.let {
                    java.time.Instant.ofEpochMilli(it)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                }.format(dateFormatter), dateFormatter).let {
                    java.time.temporal.ChronoUnit.DAYS.between(it, LocalDate.now()).toInt() + 1
                }.coerceAtLeast(1)
                
                val completionRate = if (totalDays > 0) {
                    records.count { it.completed }.toFloat() / totalDays
                } else 0f

                HabitWithStats(
                    habit = Habit(
                        id = habit.id,
                        name = habit.name,
                        createdAt = habit.createdAt,
                        reminderTime = habit.reminderTime,
                        isActive = habit.isActive
                    ),
                    currentStreak = currentStreak,
                    totalCompleted = records.count { it.completed },
                    completionRate = completionRate.coerceIn(0f, 1f),
                    todayCompleted = todayCompleted
                )
            }
        }
    }

    suspend fun addHabit(name: String, reminderTime: String?): Long {
        val entity = HabitEntity(
            name = name,
            reminderTime = reminderTime
        )
        return habitDao.insertHabit(entity)
    }

    suspend fun deleteHabit(habitId: Long) {
        val habit = habitDao.getAllHabitsWithRecords().map { habits ->
            habits.find { it.habit.id == habitId }?.habit
        }
        // Get the entity and mark as inactive
        habitDao.getAllHabitsWithRecords().collect { habits ->
            habits.find { it.habit.id == habitId }?.let {
                habitDao.updateHabit(it.habit.copy(isActive = false))
            }
            return@collect
        }
    }

    suspend fun toggleHabitForToday(habitId: Long) {
        val today = LocalDate.now().format(dateFormatter)
        val existingRecord = habitDao.getRecordForDate(habitId, today)
        
        if (existingRecord != null) {
            habitDao.deleteRecordForDate(habitId, today)
        } else {
            habitDao.insertRecord(
                HabitRecordEntity(
                    habitId = habitId,
                    date = today,
                    completed = true
                )
            )
        }
    }
}
