package kr.hnu.ice.projectapplication.model

/** 스트레칭 동작 (정적 목록, DB에 저장하지 않음) */
data class Stretching(
    val id: Int,
    val title: String,
    val bodyPart: String,
    val emoji: String,
    val description: String,
    val durationSeconds: Int
)
