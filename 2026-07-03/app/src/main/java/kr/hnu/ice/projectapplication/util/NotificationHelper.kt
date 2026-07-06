package kr.hnu.ice.projectapplication.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kr.hnu.ice.projectapplication.MainActivity
import kr.hnu.ice.projectapplication.R
import kr.hnu.ice.projectapplication.StretchingActivity

/** 물/스트레칭 알림 채널 생성 및 발송을 담당한다. */
object NotificationHelper {

    const val CHANNEL_ID = "water_buddy_reminders"
    private const val NOTIFICATION_ID_WATER = 1001
    private const val NOTIFICATION_ID_STRETCH = 1002

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
    }

    fun showWaterReminder(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        show(context, NOTIFICATION_ID_WATER, R.string.notification_water_title, R.string.notification_water_text, intent)
    }

    fun showStretchReminder(context: Context) {
        val intent = Intent(context, StretchingActivity::class.java)
        show(context, NOTIFICATION_ID_STRETCH, R.string.notification_stretch_title, R.string.notification_stretch_text, intent)
    }

    private fun show(context: Context, id: Int, titleRes: Int, textRes: Int, intent: Intent) {
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        val pendingIntent = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(titleRes))
            .setContentText(context.getString(textRes))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }
}
