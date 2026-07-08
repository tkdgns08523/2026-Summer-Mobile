package kr.hnu.ice.projectapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 펫 캐릭터 (characters 테이블)
 * Room 예약어 충돌을 피하기 위해 클래스명은 WaterCharacter 를 사용한다.
 */
@Entity(tableName = "characters")
data class WaterCharacter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,         // 이 펫의 소유 계정(users.id) — 계정당 펫 1마리
    val name: String,
    val species: Int = 0,     // 온보딩에서 선택한 펫 종류 (util.PetSpecies 참고)
    val level: Int = 1,
    val exp: Int = 0,
    val mood: Int = 100       // 기분 상태 0~100
) {
    /** 다음 레벨까지 필요한 경험치 */
    val expToNextLevel: Int get() = level * 100

    /** 현재 레벨 진행률(0.0~1.0) */
    val levelProgress: Float
        get() = (exp.toFloat() / expToNextLevel).coerceIn(0f, 1f)
}
