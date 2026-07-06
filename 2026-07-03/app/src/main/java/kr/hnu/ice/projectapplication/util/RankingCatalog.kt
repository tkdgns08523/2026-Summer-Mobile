package kr.hnu.ice.projectapplication.util

import kr.hnu.ice.projectapplication.model.Friend
import kotlin.math.abs

/**
 * 서버가 없는 상태에서 랭킹 화면을 채우기 위한 목업 친구 데이터.
 * 사용자가 직접 추가한 닉네임은 이름 해시로 고정된 달성률을 부여해 매번 같은 값이 나오게 한다.
 */
object RankingCatalog {

    private val mockFriends = listOf(
        Friend("물마시는곰돌이", "🐻", 92),
        Friend("촉촉한하마", "🦛", 78),
        Friend("수분왕도치", "🦔", 65),
        Friend("목마른펭귄", "🐧", 54),
        Friend("촉촉냥이", "🐱", 41)
    )

    fun mockFriends(): List<Friend> = mockFriends

    fun friendFromNickname(nickname: String): Friend {
        val rate = 30 + abs(nickname.hashCode()) % 65
        return Friend(nickname, "🐣", rate)
    }
}
