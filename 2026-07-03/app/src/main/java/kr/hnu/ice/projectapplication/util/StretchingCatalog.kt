package kr.hnu.ice.projectapplication.util

import kr.hnu.ice.projectapplication.model.Stretching

/** 스트레칭 동작 정적 카탈로그 */
object StretchingCatalog {

    fun all(): List<Stretching> = listOf(
        Stretching(1, "목 좌우 늘리기", "목", "🙆", "고개를 천천히 좌우로 기울여 목 옆쪽 근육을 늘려주세요.", 20),
        Stretching(2, "목 앞뒤 늘리기", "목", "🙇", "고개를 천천히 숙였다 젖히며 목 앞뒤를 이완해주세요.", 20),
        Stretching(3, "어깨 돌리기", "어깨", "🤸", "양 어깨를 크게 원을 그리듯 앞뒤로 돌려주세요.", 30),
        Stretching(4, "어깨 으쓱하기", "어깨", "🙋", "양 어깨를 귀에 가깝게 올렸다 힘을 빼고 내려주세요.", 20),
        Stretching(5, "허리 비틀기", "허리", "🧘", "의자에 앉은 채로 상체를 좌우로 천천히 비틀어주세요.", 30),
        Stretching(6, "허리 숙여 늘리기", "허리", "🤾", "선 자세에서 상체를 천천히 숙여 허리를 늘려주세요.", 30),
        Stretching(7, "손목 돌리기", "손목", "🤲", "양 손목을 시계 방향, 반시계 방향으로 돌려주세요.", 15),
        Stretching(8, "손목 젖히기", "손목", "✋", "한 손으로 반대 손가락을 잡고 손목을 천천히 젖혀주세요.", 15)
    )
}
