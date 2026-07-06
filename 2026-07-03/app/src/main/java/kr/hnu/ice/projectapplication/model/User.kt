package kr.hnu.ice.projectapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 사용자 정보 (users 테이블)
 * 온보딩에서 입력받은 프로필과 계산된 하루 목표량을 저장한다.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 한 기기에 여러 계정(멀티 프로필)이 존재할 수 있음
    val nickname: String,
    val weight: Float,            // 체중(kg)
    val activityLevel: Int,       // 1: 적음, 2: 보통, 3: 많음
    val dailyGoal: Int,           // 하루 목표량(ml)
    val wakeTime: String,         // 기상 시간 "HH:mm"
    val sleepTime: String         // 취침 시간 "HH:mm"
)
