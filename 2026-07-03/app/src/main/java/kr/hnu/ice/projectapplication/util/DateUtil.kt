package kr.hnu.ice.projectapplication.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 날짜/시간 포맷 유틸 */
object DateUtil {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    private val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREA)

    /** 오늘 날짜 "yyyy-MM-dd" */
    fun today(): String = dateFormat.format(Date())

    /** timestamp(millis) → "yyyy-MM-dd" */
    fun toDate(timestamp: Long): String = dateFormat.format(Date(timestamp))

    /** timestamp(millis) → "오전 9:30" */
    fun toTime(timestamp: Long): String = timeFormat.format(Date(timestamp))

    /** 이번 주를 구분하는 키 (예: "2026-27") — 주간 보상 중복 지급 방지용 */
    fun currentWeekKey(): String = SimpleDateFormat("yyyy-ww", Locale.KOREA).format(Date())
}
