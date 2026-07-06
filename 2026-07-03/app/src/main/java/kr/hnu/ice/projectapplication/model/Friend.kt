package kr.hnu.ice.projectapplication.model

/** 랭킹 화면에 표시되는 친구 (실제 서버 없이 목업/로컬 데이터로 구성) */
data class Friend(
    val nickname: String,
    val petEmoji: String,
    val achievementRate: Int, // 이번 주 목표 달성률(%)
    val isMe: Boolean = false
)
