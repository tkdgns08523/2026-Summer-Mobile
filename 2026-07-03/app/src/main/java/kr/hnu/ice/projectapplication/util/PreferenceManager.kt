package kr.hnu.ice.projectapplication.util

import android.content.Context

/**
 * SharedPreferences 래퍼.
 * 한 기기에 여러 로컬 계정(멀티 프로필)이 있을 수 있으므로,
 * 계정별 값(코인/스트레칭 횟수/랭킹 보상)은 activeUserId를 키에 포함해 분리 저장한다.
 * 알림 설정은 기기 전체에 공통으로 적용되는 값이라 계정과 무관하게 저장한다.
 */
class PreferenceManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /** 현재 로그인된 계정의 id. 로그아웃 상태면 NO_USER. */
    var activeUserId: Long
        get() = prefs.getLong(KEY_ACTIVE_USER_ID, NO_USER)
        set(value) = prefs.edit().putLong(KEY_ACTIVE_USER_ID, value).apply()

    val isOnboarded: Boolean
        get() = activeUserId != NO_USER

    var coins: Int
        get() = prefs.getInt(scopedKey(KEY_COINS), 0)
        set(value) = prefs.edit().putInt(scopedKey(KEY_COINS), value).apply()

    fun addCoins(amount: Int) {
        coins += amount
    }

    /** 오늘 완료한 스트레칭 횟수 (날짜가 바뀌면 자동으로 0부터 시작) */
    fun todayStretchCount(): Int {
        val today = DateUtil.today()
        return if (prefs.getString(scopedKey(KEY_STRETCH_DATE), null) == today) {
            prefs.getInt(scopedKey(KEY_STRETCH_COUNT), 0)
        } else 0
    }

    fun incrementTodayStretchCount() {
        val today = DateUtil.today()
        val count = todayStretchCount() + 1
        prefs.edit()
            .putString(scopedKey(KEY_STRETCH_DATE), today)
            .putInt(scopedKey(KEY_STRETCH_COUNT), count)
            .apply()
    }

    var rankRewardClaimedWeek: String?
        get() = prefs.getString(scopedKey(KEY_RANK_REWARD_WEEK), null)
        set(value) = prefs.edit().putString(scopedKey(KEY_RANK_REWARD_WEEK), value).apply()

    /** 계정 삭제(데이터 초기화) 시, 그 계정 전용 값만 지운다. 다른 계정에는 영향 없음. */
    fun clearUserData(userId: Long) {
        prefs.edit()
            .remove(scopedKeyFor(KEY_COINS, userId))
            .remove(scopedKeyFor(KEY_STRETCH_DATE, userId))
            .remove(scopedKeyFor(KEY_STRETCH_COUNT, userId))
            .remove(scopedKeyFor(KEY_RANK_REWARD_WEEK, userId))
            .apply()
    }

    private fun scopedKey(base: String) = scopedKeyFor(base, activeUserId)

    private fun scopedKeyFor(base: String, userId: Long) = "${base}_$userId"

    // ---- 알림 설정 (기기 전체 공통) ----
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ALARM_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ALARM_ENABLED, value).apply()

    var stretchNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_STRETCH_ALARM_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_STRETCH_ALARM_ENABLED, value).apply()

    var alarmIntervalMinutes: Int
        get() = prefs.getInt(KEY_ALARM_INTERVAL, 60)
        set(value) = prefs.edit().putInt(KEY_ALARM_INTERVAL, value).apply()

    var activeStartMinutes: Int
        get() = prefs.getInt(KEY_ACTIVE_START, 8 * 60)
        set(value) = prefs.edit().putInt(KEY_ACTIVE_START, value).apply()

    var activeEndMinutes: Int
        get() = prefs.getInt(KEY_ACTIVE_END, 22 * 60)
        set(value) = prefs.edit().putInt(KEY_ACTIVE_END, value).apply()

    var soundIndex: Int
        get() = prefs.getInt(KEY_SOUND_INDEX, 0)
        set(value) = prefs.edit().putInt(KEY_SOUND_INDEX, value).apply()

    var dndEnabled: Boolean
        get() = prefs.getBoolean(KEY_DND_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_DND_ENABLED, value).apply()

    var dndStartMinutes: Int
        get() = prefs.getInt(KEY_DND_START, 22 * 60)
        set(value) = prefs.edit().putInt(KEY_DND_START, value).apply()

    var dndEndMinutes: Int
        get() = prefs.getInt(KEY_DND_END, 7 * 60)
        set(value) = prefs.edit().putInt(KEY_DND_END, value).apply()

    companion object {
        const val NO_USER = -1L

        private const val PREF_NAME = "waterbuddy_prefs"
        private const val KEY_ACTIVE_USER_ID = "active_user_id"
        private const val KEY_COINS = "coins"
        private const val KEY_STRETCH_DATE = "stretch_date"
        private const val KEY_STRETCH_COUNT = "stretch_count"
        private const val KEY_RANK_REWARD_WEEK = "rank_reward_week"
        private const val KEY_ALARM_ENABLED = "alarm_enabled"
        private const val KEY_STRETCH_ALARM_ENABLED = "stretch_alarm_enabled"
        private const val KEY_ALARM_INTERVAL = "alarm_interval"
        private const val KEY_ACTIVE_START = "active_start"
        private const val KEY_ACTIVE_END = "active_end"
        private const val KEY_SOUND_INDEX = "sound_index"
        private const val KEY_DND_ENABLED = "dnd_enabled"
        private const val KEY_DND_START = "dnd_start"
        private const val KEY_DND_END = "dnd_end"
    }
}
