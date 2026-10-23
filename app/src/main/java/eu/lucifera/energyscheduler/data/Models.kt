package eu.lucifera.energyscheduler.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import eu.lucifera.energyscheduler.R
import java.time.LocalTime

enum class EnergyLevel(val labelRes: Int) {
    PEAK(R.string.peak), 
    AVERAGE(R.string.average), 
    DIP(R.string.dip)
}

enum class ActivityCategory(val titleRes: Int, val descriptionRes: Int) {
    CREATE(R.string.cat_create, R.string.create),
    PLAN(R.string.cat_plan, R.string.plan),
    MOVE(R.string.cat_move, R.string.move),
    RELAX(R.string.cat_relax, R.string.relax)
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: ActivityCategory,
    val scheduledTime: LocalTime,
    val isCompleted: Boolean = false
)

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 0,
    val language: String = "system",
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Entity(tableName = "energy_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 0,
    val name: String,
    val chronotype: ChronotypeType
)

enum class ChronotypeType(val titleRes: Int) {
    LION(R.string.lion_name), 
    BEAR(R.string.bear_name), 
    WOLF(R.string.wolf_name), 
    DOLPHIN(R.string.dolphin_name), 
    CUSTOM(R.string.unknown)
}

@Entity(tableName = "energy_points")
data class EnergyPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: LocalTime,
    val level: EnergyLevel
)
