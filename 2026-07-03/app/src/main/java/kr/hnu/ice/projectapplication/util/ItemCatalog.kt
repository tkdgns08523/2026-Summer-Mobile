package kr.hnu.ice.projectapplication.util

import android.graphics.Color
import kr.hnu.ice.projectapplication.model.Item

/** 배경 아이템의 emoji 필드(색상 hex 문자열)를 안전하게 색상으로 변환한다. */
fun parseColorOrNull(hex: String): Int? = try {
    Color.parseColor(hex)
} catch (e: IllegalArgumentException) {
    null
}

/**
 * 아이템 상점 기본 카탈로그 (최초 실행 시 DB에 시드)
 * 펫 종마다 눈/머리 위치가 달라 이모지를 겹쳐 놓는 모자/액세서리는 위치가 계속 어긋나서,
 * 펫 뒤 배경을 단색으로 바꾸는 것으로 단순화했다. emoji 필드는 배경 아이템에 한해
 * 실제 이모지가 아니라 색상 hex 코드를 담는다.
 */
object ItemCatalog {

    const val TYPE_BACKGROUND = "background"

    fun defaultItems(userId: Long): List<Item> = listOf(
        // 기본 색상은 무료로 처음부터 장착돼 있다.
        Item(userId = userId, name = "하늘색", type = TYPE_BACKGROUND, emoji = "#0B84FF", price = 0, isOwned = true, isEquipped = true),
        Item(userId = userId, name = "코랄", type = TYPE_BACKGROUND, emoji = "#FF8A65", price = 30),
        Item(userId = userId, name = "민트", type = TYPE_BACKGROUND, emoji = "#2DD4A7", price = 40),
        Item(userId = userId, name = "레몬", type = TYPE_BACKGROUND, emoji = "#FFC24B", price = 50),
        Item(userId = userId, name = "라벤더", type = TYPE_BACKGROUND, emoji = "#B983FF", price = 60)
    )
}
