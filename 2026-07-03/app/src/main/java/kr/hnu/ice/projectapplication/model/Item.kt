package kr.hnu.ice.projectapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 펫 꾸미기 아이템 (items 테이블)
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,        // 이 아이템 소유/장착 상태의 계정(users.id)
    val name: String,
    val type: String,       // hat / accessory / background
    val emoji: String,      // 이미지 대신 사용하는 이모지
    val price: Int,
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false
)
