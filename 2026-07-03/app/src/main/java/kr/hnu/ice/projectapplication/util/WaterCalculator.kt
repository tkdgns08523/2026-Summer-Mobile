package kr.hnu.ice.projectapplication.util

/**
 * 하루 권장 물 섭취량 계산기.
 *
 * 기본 공식: 체중(kg) × 33ml 에 활동량 계수를 곱한다.
 * (일반적으로 통용되는 근사 공식으로, 의료적 처방이 아님)
 */
object WaterCalculator {

    const val ACTIVITY_LOW = 1
    const val ACTIVITY_NORMAL = 2
    const val ACTIVITY_HIGH = 3

    /**
     * @param weight 체중(kg)
     * @param activityLevel 활동량 (1: 적음, 2: 보통, 3: 많음)
     * @return 하루 권장량(ml), 50ml 단위로 반올림
     */
    fun recommendDailyGoal(weight: Float, activityLevel: Int): Int {
        val base = weight * 33f
        val factor = when (activityLevel) {
            ACTIVITY_LOW -> 1.0f
            ACTIVITY_HIGH -> 1.2f
            else -> 1.1f // 보통
        }
        val raw = base * factor
        return roundTo50((raw).toInt()).coerceIn(1000, 5000)
    }

    private fun roundTo50(value: Int): Int = ((value + 25) / 50) * 50
}
