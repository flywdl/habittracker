package com.habittracker.app.domain.model

import java.time.LocalDate

data class Habit(
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderTime: String? = null,
    val isActive: Boolean = true
)

data class HabitRecord(
    val id: Long = 0,
    val habitId: Long,
    val date: String, // Format: yyyy-MM-dd
    val completed: Boolean = true
)

data class HabitWithStats(
    val habit: Habit,
    val currentStreak: Int,
    val totalCompleted: Int,
    val completionRate: Float,
    val todayCompleted: Boolean
)
