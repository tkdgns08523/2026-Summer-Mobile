package kr.hnu.ice.projectapplication.util

import kr.hnu.ice.projectapplication.model.Item

/** 아이템 상점 기본 카탈로그 (최초 실행 시 DB에 시드) */
object ItemCatalog {

    const val TYPE_HAT = "hat"
    const val TYPE_ACCESSORY = "accessory"
    const val TYPE_BACKGROUND = "background"

    // TODO: 꾸미기 테스트용으로 전부 0원 처리. 테스트 끝나면 원래 가격으로 되돌릴 것.
    fun defaultItems(userId: Long): List<Item> = listOf(
        Item(userId = userId, name = "밀짚모자", type = TYPE_HAT, emoji = "👒", price = 0),
        Item(userId = userId, name = "왕관", type = TYPE_HAT, emoji = "👑", price = 0),
        Item(userId = userId, name = "야구모자", type = TYPE_HAT, emoji = "🧢", price = 0),
        Item(userId = userId, name = "리본", type = TYPE_ACCESSORY, emoji = "🎀", price = 0),
        Item(userId = userId, name = "목도리", type = TYPE_ACCESSORY, emoji = "🧣", price = 0),
        Item(userId = userId, name = "안경", type = TYPE_ACCESSORY, emoji = "🕶️", price = 0),
        Item(userId = userId, name = "무지개 배경", type = TYPE_BACKGROUND, emoji = "🌈", price = 0),
        Item(userId = userId, name = "별밤 배경", type = TYPE_BACKGROUND, emoji = "🌌", price = 0),
        Item(userId = userId, name = "꽃밭 배경", type = TYPE_BACKGROUND, emoji = "🌼", price = 0)
    )
}
