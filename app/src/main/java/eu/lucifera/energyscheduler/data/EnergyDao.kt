package eu.lucifera.energyscheduler.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EnergyDao {
    @Query("SELECT * FROM energy_profiles LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Query("SELECT * FROM energy_points ORDER BY time ASC")
    fun getEnergyPoints(): Flow<List<EnergyPoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnergyPoints(points: List<EnergyPoint>)

    @Query("DELETE FROM energy_points")
    suspend fun clearEnergyPoints()

    @Query("SELECT * FROM user_settings WHERE id = 0")
    fun getUserSettings(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettings)

    @Query("SELECT * FROM tasks ORDER BY scheduledTime ASC")
    fun getTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, completed: Boolean)
}
