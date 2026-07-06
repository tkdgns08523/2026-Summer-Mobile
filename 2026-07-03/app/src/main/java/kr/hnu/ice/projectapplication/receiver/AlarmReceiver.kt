package kr.hnu.ice.projectapplication.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kr.hnu.ice.projectapplication.util.NotificationHelper
import kr.hnu.ice.projectapplication.util.PreferenceManager
import java.util.Calendar

/** AlarmManager가 주기적으로 발화시키는 물/스트레칭 리마인더 수신자 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferenceManager(context)
        if (!prefs.notificationsEnabled) return

        val nowMinutes = Calendar.getInstance().let {
            it.get(Calendar.HOUR_OF_DAY) * 60 + it.get(Calendar.MINUTE)
        }
        if (!isWithinRange(nowMinutes, prefs.activeStartMinutes, prefs.activeEndMinutes)) return
        if (prefs.dndEnabled && isWithinRange(nowMinutes, prefs.dndStartMinutes, prefs.dndEndMinutes)) return

        when (intent.getStringExtra(EXTRA_TYPE)) {
            TYPE_STRETCH -> if (prefs.stretchNotificationsEnabled) {
                NotificationHelper.showStretchReminder(context)
            }
            else -> NotificationHelper.showWaterReminder(context)
        }
    }

    /** [start, end) 구간에 속하는지 확인 (자정을 넘어가는 구간도 지원) */
    private fun isWithinRange(current: Int, start: Int, end: Int): Boolean {
        return if (start <= end) {
            current in start until end
        } else {
            current >= start || current < end
        }
    }

    companion object {
        const val EXTRA_TYPE = "type"
        const val TYPE_WATER = "water"
        const val TYPE_STRETCH = "stretch"
    }
}
