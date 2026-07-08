package kr.hnu.ice.projectapplication.util

/** 온보딩에서 선택 가능한 펫 종류별 성장 단계 이모지 */
object PetSpecies {
    const val CHICK = 0
    const val PUPPY = 1
    const val KITTEN = 2

    private val STAGES = mapOf(
        CHICK to listOf("🥚", "🐣", "🐤", "🐥", "🐔"),
        PUPPY to listOf("🐶", "🐕", "🐕", "🐕‍🦺", "🐩"),
        KITTEN to listOf("🐱", "😺", "🐈", "🐈", "🐈‍⬛")
    )

    private val LEVEL_THRESHOLDS = listOf(1, 3, 6, 10, 15)

    fun all(): List<Int> = listOf(CHICK, PUPPY, KITTEN)

    /** 온보딩 선택 화면 등에 보여줄 대표(성장 완료) 이모지 */
    fun previewEmoji(species: Int): String = stages(species).last()

    /** 펫 레벨에 따른 성장 단계 이모지 (캐릭터 화면용) */
    fun emojiForLevel(species: Int, level: Int): String {
        val index = LEVEL_THRESHOLDS.indexOfLast { level >= it }.coerceAtLeast(0)
        return stages(species)[index]
    }

    /** 오늘 목표 달성률(%)에 따른 성장 단계 이모지 (홈 화면용) */
    fun emojiForPercent(species: Int, percent: Int): String {
        val index = when {
            percent >= 100 -> 4
            percent >= 75 -> 3
            percent >= 50 -> 2
            percent >= 25 -> 1
            else -> 0
        }
        return stages(species)[index]
    }

    fun collectionStages(species: Int): List<String> = stages(species)
    fun collectionThresholds(): List<Int> = LEVEL_THRESHOLDS

    private fun stages(species: Int): List<String> = STAGES[species] ?: STAGES.getValue(CHICK)
}
