package eu.lucifera.energyscheduler.logic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import eu.lucifera.energyscheduler.R
import eu.lucifera.energyscheduler.data.EnergyDatabase
import kotlinx.coroutines.flow.first
import java.time.LocalTime

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = EnergyDatabase.getDatabase(applicationContext)
        val dao = database.energyDao()
        val points = dao.getEnergyPoints().first()

        if (points.isEmpty()) return Result.success()

        val now = LocalTime.now()
        val level = EnergyEngine.getCurrentEnergyLevel(now, points)
        val recommendation = EnergyEngine.getRecommendation(level)

        val title = applicationContext.getString(R.string.notification_title)
        val levelLabel = applicationContext.getString(level.labelRes)
        val recommendationLabel = applicationContext.getString(recommendation.titleRes)
        val message = applicationContext.getString(R.string.notification_message, levelLabel, recommendationLabel)

        sendNotification(title, message)

        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val channelId = "energy_updates"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                applicationContext.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}
