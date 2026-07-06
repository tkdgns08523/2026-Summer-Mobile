package kr.hnu.ice.projectapplication.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kr.hnu.ice.projectapplication.receiver.AlarmReceiver

/** 물/스트레칭 알림을 위한 반복 알람을 등록/해제한다. */
object AlarmScheduler {

    private const val REQUEST_CODE_WATER = 2001
    private const val REQUEST_CODE_STRETCH = 2002

    fun reschedule(context: Context, intervalMinutes: Int, stretchEnabled: Boolean) {
        val intervalMillis = intervalMinutes * 60_000L
        schedule(context, AlarmReceiver.TYPE_WATER, REQUEST_CODE_WATER, intervalMillis)
        if (stretchEnabled) {
            schedule(context, AlarmReceiver.TYPE_STRETCH, REQUEST_CODE_STRETCH, intervalMillis)
        } else {
            cancel(context, REQUEST_CODE_STRETCH)
        }
    }

    fun cancelAll(context: Context) {
        cancel(context, REQUEST_CODE_WATER)
        cancel(context, REQUEST_CODE_STRETCH)
    }

    private fun schedule(context: Context, type: String, requestCode: Int, intervalMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).putExtra(AlarmReceiver.EXTRA_TYPE, type)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
    }

    private fun cancel(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
