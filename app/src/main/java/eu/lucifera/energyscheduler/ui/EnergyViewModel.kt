package eu.lucifera.energyscheduler.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eu.lucifera.energyscheduler.data.ChronotypeType
import eu.lucifera.energyscheduler.data.EnergyDatabase
import eu.lucifera.energyscheduler.data.EnergyPoint
import eu.lucifera.energyscheduler.data.UserProfile
import eu.lucifera.energyscheduler.logic.EnergyEngine
import eu.lucifera.energyscheduler.data.Task
import eu.lucifera.energyscheduler.data.ActivityCategory
import eu.lucifera.energyscheduler.data.ThemeMode
import eu.lucifera.energyscheduler.data.UserSettings
import eu.lucifera.energyscheduler.logic.NotificationWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Locale

class EnergyViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = EnergyDatabase.getDatabase(application).energyDao()

    val userProfile: StateFlow<UserProfile?> = dao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val energyPoints: StateFlow<List<EnergyPoint>> = dao.getEnergyPoints()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettings> = dao.getUserSettings()
        .map { it ?: UserSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val tasks: StateFlow<List<Task>> = dao.getTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(title: String, category: ActivityCategory, time: LocalTime) {
        viewModelScope.launch {
            dao.insertTask(Task(title = title, category = category, scheduledTime = time))
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            dao.updateTaskStatus(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task.id)
        }
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            val current = userSettings.value
            dao.insertSettings(current.copy(language = languageCode))
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            val current = userSettings.value
            dao.insertSettings(current.copy(themeMode = mode))
        }
    }

    fun setChronotype(name: String, type: ChronotypeType) {
        viewModelScope.launch {
            dao.insertProfile(UserProfile(name = name, chronotype = type))
            val defaultPoints = EnergyEngine.getDefaultPointsForChronotype(type)
            if (defaultPoints.isNotEmpty()) {
                dao.clearEnergyPoints()
                dao.insertEnergyPoints(defaultPoints)
            }
            scheduleNotifications()
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            dao.insertProfile(current.copy(name = newName))
        }
    }

    private fun scheduleNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            "energy_notifier",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun getCurrentStatus(): EnergyStatus {
        val points = energyPoints.value
        val now = LocalTime.now()
        val level = EnergyEngine.getCurrentEnergyLevel(now, points)
        val recommendation = EnergyEngine.getRecommendation(level)
        return EnergyStatus(level, recommendation)
    }
}

data class EnergyStatus(
    val level: eu.lucifera.energyscheduler.data.EnergyLevel,
    val recommendation: eu.lucifera.energyscheduler.data.ActivityCategory
)
