package com.habittracker.app.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.habittracker.app.data.local.HabitDatabase
import com.habittracker.app.data.repository.HabitRepository
import com.habittracker.app.domain.model.HabitWithStats
import com.habittracker.app.ui.components.AddHabitDialog
import com.habittracker.app.ui.components.HabitCard
import com.habittracker.app.ui.theme.Primary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = Room.databaseBuilder(
        application,
        HabitDatabase::class.java,
        "habit_database"
    ).build()
    
    private val repository = HabitRepository(database.habitDao())
    
    private val _habits = MutableStateFlow<List<HabitWithStats>>(emptyList())
    val habits: StateFlow<List<HabitWithStats>> = _habits.asStateFlow()
    
    init {
        viewModelScope.launch {
            repository.getAllHabitsWithStats().collect { habitsList ->
                _habits.value = habitsList
            }
        }
    }
    
    fun addHabit(name: String, reminderTime: String?) {
        viewModelScope.launch {
            repository.addHabit(name, reminderTime)
        }
    }
    
    fun toggleHabit(habitId: Long) {
        viewModelScope.launch {
            repository.toggleHabitForToday(habitId)
        }
    }
    
    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel = androidx.lifecycle.viewmodel.compose.rememberViewModel { MainViewModel(LocalContext.current.applicationContext as Application) }
    val habits by viewModel.habits.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("M月d日"))
    val dayOfWeek = LocalDate.now().dayOfWeek.name.let { 
        when(it) {
            "MONDAY" -> "周一"
            "TUESDAY" -> "周二"
            "WEDNESDAY" -> "周三"
            "THURSDAY" -> "周四"
            "FRIDAY" -> "周五"
            "SATURDAY" -> "周六"
            "SUNDAY" -> "周日"
            else -> it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "习惯打卡",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$today $dayOfWeek",
                            style = MaterialTheme.typography.bodySmall,
                            color = Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加习惯",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        if (habits.isEmpty()) {
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(habits, key = { it.habit.id }) { habitWithStats ->
                    HabitCard(
                        habitWithStats = habitWithStats,
                        onToggle = { viewModel.toggleHabit(habitWithStats.habit.id) },
                        onDelete = { viewModel.deleteHabit(habitWithStats.habit.id) }
                    )
                }
            }
        }
        
        if (showAddDialog) {
            AddHabitDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, reminderTime ->
                    viewModel.addHabit(name, reminderTime)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📝",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "还没有习惯",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "点击右下角 + 添加你的第一个习惯",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
