package eu.lucifera.energyscheduler.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserProfile::class, EnergyPoint::class, UserSettings::class, Task::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class EnergyDatabase : RoomDatabase() {
    abstract fun energyDao(): EnergyDao

    companion object {
        @Volatile
        private var INSTANCE: EnergyDatabase? = null

        fun getDatabase(context: Context): EnergyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EnergyDatabase::class.java,
                    "energy_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
