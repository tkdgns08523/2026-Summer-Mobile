package kr.hnu.ice.projectapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 물 섭취 기록 (drink_records 테이블)
 */
@Entity(tableName = "drink_records")
data class DrinkRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,       // 이 기록의 소유 계정(users.id)
    val amount: Int,        // 섭취량(ml)
    val timestamp: Long,    // 기록 시각(Unix millis)
    val date: String        // 날짜 "yyyy-MM-dd" (일별 집계용)
)
