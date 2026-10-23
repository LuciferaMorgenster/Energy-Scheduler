package eu.lucifera.energyscheduler.data

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromChronotype(value: ChronotypeType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toChronotype(value: String?): ChronotypeType? {
        return value?.let { ChronotypeType.valueOf(it) }
    }

    @TypeConverter
    fun fromEnergyLevel(value: EnergyLevel?): String? {
        return value?.name
    }

    @TypeConverter
    fun toEnergyLevel(value: String?): EnergyLevel? {
        return value?.let { EnergyLevel.valueOf(it) }
    }

    @TypeConverter
    fun fromThemeMode(value: ThemeMode?): String? {
        return value?.name
    }

    @TypeConverter
    fun toThemeMode(value: String?): ThemeMode? {
        return value?.let { ThemeMode.valueOf(it) }
    }
}
