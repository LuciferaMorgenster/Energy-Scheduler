package eu.lucifera.energyscheduler.logic

import eu.lucifera.energyscheduler.data.ActivityCategory
import eu.lucifera.energyscheduler.data.ChronotypeType
import eu.lucifera.energyscheduler.data.EnergyLevel
import eu.lucifera.energyscheduler.data.EnergyPoint
import java.time.LocalTime

object EnergyEngine {

    fun getCurrentEnergyLevel(currentTime: LocalTime, points: List<EnergyPoint>): EnergyLevel {
        if (points.isEmpty()) return EnergyLevel.AVERAGE

        // Sort points by time
        val sortedPoints = points.sortedBy { it.time }

        // Find the points surrounding the current time
        val nextPoint = sortedPoints.find { it.time >= currentTime }
            ?: sortedPoints.first() // Wrap around to the start of the next day

        val previousPoint = sortedPoints.findLast { it.time <= currentTime }
            ?: sortedPoints.last() // Wrap around from the end of the previous day

        // For simplicity in this version, we return the level of the closest previous point
        // In a future version, we could interpolate.
        return previousPoint.level
    }

    fun getRecommendation(level: EnergyLevel): ActivityCategory {
        return when (level) {
            EnergyLevel.PEAK -> ActivityCategory.CREATE
            EnergyLevel.AVERAGE -> ActivityCategory.PLAN
            EnergyLevel.DIP -> ActivityCategory.RELAX
        }
    }

    fun getDefaultPointsForChronotype(type: ChronotypeType): List<EnergyPoint> {
        return when (type) {
            ChronotypeType.LION -> listOf(
                EnergyPoint(time = LocalTime.of(6, 0), level = EnergyLevel.AVERAGE),
                EnergyPoint(time = LocalTime.of(8, 0), level = EnergyLevel.PEAK),
                EnergyPoint(time = LocalTime.of(13, 0), level = EnergyLevel.DIP),
                EnergyPoint(time = LocalTime.of(16, 0), level = EnergyLevel.AVERAGE),
                EnergyPoint(time = LocalTime.of(21, 0), level = EnergyLevel.DIP)
            )
            ChronotypeType.BEAR -> listOf(
                EnergyPoint(time = LocalTime.of(7, 0), level = EnergyLevel.AVERAGE),
                EnergyPoint(time = LocalTime.of(10, 0), level = EnergyLevel.PEAK),
                EnergyPoint(time = LocalTime.of(15, 0), level = EnergyLevel.DIP),
                EnergyPoint(time = LocalTime.of(18, 0), level = EnergyLevel.AVERAGE),
                EnergyPoint(time = LocalTime.of(22, 0), level = EnergyLevel.DIP)
            )
            ChronotypeType.WOLF -> listOf(
                EnergyPoint(time = LocalTime.of(8, 0), level = EnergyLevel.DIP),
                EnergyPoint(time = LocalTime.of(12, 0), level = EnergyLevel.AVERAGE),
                EnergyPoint(time = LocalTime.of(17, 0), level = EnergyLevel.AVERAGE),
                EnergyPoint(time = LocalTime.of(20, 0), level = EnergyLevel.PEAK),
                EnergyPoint(time = LocalTime.of(0, 0), level = EnergyLevel.DIP)
            )
            ChronotypeType.DOLPHIN -> listOf(
                EnergyPoint(time = LocalTime.of(7, 0), level = EnergyLevel.DIP),
                EnergyPoint(time = LocalTime.of(10, 0), level = EnergyLevel.AVERAGE),
                EnergyPoint(time = LocalTime.of(13, 0), level = EnergyLevel.DIP),
                EnergyPoint(time = LocalTime.of(16, 0), level = EnergyLevel.AVERAGE),
                EnergyPoint(time = LocalTime.of(19, 0), level = EnergyLevel.PEAK),
                EnergyPoint(time = LocalTime.of(23, 0), level = EnergyLevel.DIP)
            )
            ChronotypeType.CUSTOM -> emptyList()
        }
    }
}
