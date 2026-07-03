# 모바일 프로그래밍 - 벤치마킹 보고서
 
## 1. 벤치마킹 대상 앱
 
**앱 이름:** Plant Nanny (플랜트 나니)  
**개발사:** Fourdesire (SPARKFUL)  
**플랫폼:** Android / iOS  
**카테고리:** 건강 및 피트니스  
 
Plant Nanny는 물 마시기 리마인더 앱으로, 사용자가 물을 마실 때마다 가상의 식물이 함께 자라는 게이미피케이션 요소를 결합한 수분 섭취 추적 앱이다.
 
---
 
## 2. 마음에 드는 점 (벤치마킹 포인트)
 
### 2-1. 게이미피케이션 (Gamification)
단순히 물 마시기를 기록하는 것이 아니라, 물을 마시면 귀여운 식물이 성장하는 시스템이다. 식물을 돌본다는 재미 요소가 있어 사용자가 꾸준히 앱을 사용하게 만든다.
 
### 2-2. 직관적인 원터치 UI
물을 마실 때마다 물방울 버튼을 한 번 터치하면 끝이다. 복잡한 입력 없이 간편하게 기록할 수 있다.
 
### 2-3. 수집 요소
50개 이상의 다양한 식물을 수집할 수 있어 장기적인 사용 동기를 부여한다.
 
### 2-4. 위젯 지원
7가지 위젯으로 홈 화면에서 물 마시기 진행 상황과 식물 상태를 바로 확인할 수 있다.
 
### 2-5. 시각적 통계
월별 그래프로 지난달 대비 물 마시기 기록을 쉽게 비교할 수 있어 자기 관리에 효과적이다.
 
---
 
## 3. 벤치마킹 기획 — "물친구 (WaterBuddy)"
 
Plant Nanny의 식물 성장 콘셉트를 참고하되, **캐릭터(펫) 육성 + 스트레칭 통합**으로 차별화한다.
 
### 차별화 포인트
 
| 구분 | Plant Nanny | 물친구 (WaterBuddy) |
|------|-------------|---------------------|
| 성장 대상 | 식물 | 귀여운 펫 캐릭터 |
| 기록 방식 | 물 마시기만 | 물 마시기 + 스트레칭 |
| 소셜 기능 | 없음 | 친구 랭킹 시스템 |
| 알림 | 물 마시기 알림 | 물 + 스트레칭 통합 알림 |
| 꾸미기 | 화분 선택 | 펫 아이템 커스터마이징 |
 
---
 
## 4. 화면(모듈) 구성 — 총 10개
 
### 모듈 1: SplashActivity (스플래시 화면)
- 앱 로고 + 로딩 애니메이션
- 1~2초 후 자동으로 다음 화면으로 이동
- 첫 실행 여부 판단 → 초기 설정 or 메인으로 분기
### 모듈 2: OnboardingActivity (초기 설정 화면)
- 닉네임 입력
- 체중(kg) 입력
- 활동량 선택 (적음 / 보통 / 많음)
- 기상 시간 / 취침 시간 설정
- 위 정보 기반으로 하루 권장 물 섭취량 자동 계산
- 첫 번째 펫 캐릭터 선택
### 모듈 3: MainActivity (메인 화면)
- 펫 캐릭터 표시 (상태에 따라 표정 변화)
- 오늘 목표 대비 진행률 (프로그레스 바 / 원형 게이지)
- 물 마시기 버튼 (원터치 기록)
- 물 양 선택 (100ml / 200ml / 300ml / 500ml)
- 하단 네비게이션 바 (메인 / 통계 / 스트레칭 / 랭킹 / 마이페이지)
### 모듈 4: DrinkHistoryActivity (물 기록 히스토리)
- 오늘 마신 물 시간별 목록 (RecyclerView)
- 각 기록별 삭제/수정 가능
- 남은 양 표시
- 달력 형태로 날짜별 달성 여부 표시 (달성: 초록, 미달성: 회색)
### 모듈 5: StatisticsActivity (통계/그래프 화면)
- 일간 섭취량 막대 그래프
- 주간 평균 섭취량 꺾은선 그래프
- 월간 요약 (달성일 수 / 평균 섭취량)
- 지난달 대비 비교
- MPAndroidChart 라이브러리 활용
### 모듈 6: CharacterActivity (캐릭터 꾸미기)
- 현재 펫 상태 표시 (레벨, 경험치, 기분)
- 보유 아이템 목록
- 아이템 장착/해제
- 아이템 상점 (물 마시기 달성으로 얻은 코인으로 구매)
- 펫 도감 (수집한 캐릭터 목록)
### 모듈 7: StretchingActivity (스트레칭 가이드)
- 스트레칭 동작 목록 (목 / 어깨 / 허리 / 손목 등)
- 동작별 이미지 + 설명
- 스트레칭 타이머 (CountDownTimer)
- 완료 시 보상 (코인 / 경험치)
- 오늘 스트레칭 횟수 표시
### 모듈 8: RankingActivity (친구/랭킹 화면)
- 이번 주 물 마시기 달성률 랭킹
- 친구 추가 (닉네임 검색)
- 친구 목록 + 각 친구의 펫 캐릭터 미리보기
- 주간 랭킹 1위 보상
### 모듈 9: AlarmSettingActivity (알림 설정)
- 알림 ON/OFF 토글
- 알림 간격 설정 (30분 / 1시간 / 2시간)
- 시작 시간 ~ 종료 시간 (TimePicker)
- 스트레칭 알림 별도 ON/OFF
- 알림 소리 선택
- 방해금지 시간대 설정
### 모듈 10: MyPageActivity (마이페이지)
- 프로필 정보 (닉네임, 체중, 활동량) 수정
- 하루 목표량 수동 변경
- 누적 통계 요약 (총 물 마신 양, 연속 달성일, 키운 캐릭터 수)
- 앱 정보 / 버전
- 데이터 초기화
- 로그아웃
---
 
## 5. Android Studio 프로젝트 구조
 
```
WaterBuddy/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/waterbuddy/
│   │   │   │   │
│   │   │   │   ├── activity/                    # 액티비티 (화면)
│   │   │   │   │   ├── SplashActivity.java
│   │   │   │   │   ├── OnboardingActivity.java
│   │   │   │   │   ├── MainActivity.java
│   │   │   │   │   ├── DrinkHistoryActivity.java
│   │   │   │   │   ├── StatisticsActivity.java
│   │   │   │   │   ├── CharacterActivity.java
│   │   │   │   │   ├── StretchingActivity.java
│   │   │   │   │   ├── RankingActivity.java
│   │   │   │   │   ├── AlarmSettingActivity.java
│   │   │   │   │   └── MyPageActivity.java
│   │   │   │   │
│   │   │   │   ├── adapter/                     # RecyclerView 어댑터
│   │   │   │   │   ├── DrinkHistoryAdapter.java
│   │   │   │   │   ├── ItemShopAdapter.java
│   │   │   │   │   ├── StretchingAdapter.java
│   │   │   │   │   └── RankingAdapter.java
│   │   │   │   │
│   │   │   │   ├── model/                       # 데이터 모델 클래스
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── DrinkRecord.java
│   │   │   │   │   ├── Character.java
│   │   │   │   │   ├── Item.java
│   │   │   │   │   ├── Stretching.java
│   │   │   │   │   └── Friend.java
│   │   │   │   │
│   │   │   │   ├── database/                    # 로컬 DB
│   │   │   │   │   ├── AppDatabase.java
│   │   │   │   │   ├── DrinkRecordDao.java
│   │   │   │   │   └── UserDao.java
│   │   │   │   │
│   │   │   │   ├── receiver/                    # 알림 관련
│   │   │   │   │   └── AlarmReceiver.java
│   │   │   │   │
│   │   │   │   └── util/                        # 유틸리티
│   │   │   │       ├── WaterCalculator.java
│   │   │   │       ├── NotificationHelper.java
│   │   │   │       └── PreferenceManager.java
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/                      # XML 레이아웃
│   │   │   │   │   ├── activity_splash.xml
│   │   │   │   │   ├── activity_onboarding.xml
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_drink_history.xml
│   │   │   │   │   ├── activity_statistics.xml
│   │   │   │   │   ├── activity_character.xml
│   │   │   │   │   ├── activity_stretching.xml
│   │   │   │   │   ├── activity_ranking.xml
│   │   │   │   │   ├── activity_alarm_setting.xml
│   │   │   │   │   ├── activity_mypage.xml
│   │   │   │   │   ├── item_drink_record.xml
│   │   │   │   │   ├── item_shop.xml
│   │   │   │   │   ├── item_stretching.xml
│   │   │   │   │   └── item_ranking.xml
│   │   │   │   │
│   │   │   │   ├── drawable/                    # 이미지/아이콘
│   │   │   │   │   ├── ic_pet_happy.png
│   │   │   │   │   ├── ic_pet_thirsty.png
│   │   │   │   │   ├── ic_water_drop.png
│   │   │   │   │   ├── ic_stretching.png
│   │   │   │   │   ├── bg_splash.xml
│   │   │   │   │   └── bg_progress_circle.xml
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── styles.xml
│   │   │   │   │   └── dimens.xml
│   │   │   │   │
│   │   │   │   ├── menu/
│   │   │   │   │   └── bottom_nav_menu.xml
│   │   │   │   │
│   │   │   │   └── xml/
│   │   │   │       └── backup_rules.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── test/                                # 테스트
│   │
│   └── build.gradle
│
├── build.gradle
└── settings.gradle
```
 
---
 
## 6. 사용 기술 스택
 
| 구분 | 기술 |
|------|------|
| 언어 | Java |
| IDE | Android Studio |
| 최소 SDK | API 26 (Android 8.0) |
| 로컬 DB | Room Database (SQLite) |
| 차트 | MPAndroidChart |
| 알림 | AlarmManager + NotificationCompat |
| UI 컴포넌트 | RecyclerView, BottomNavigationView, ViewPager2, CardView |
| 이미지 | Glide |
| 설정 저장 | SharedPreferences |
| 애니메이션 | Lottie (캐릭터 애니메이션) |
 
---
 
## 7. 핵심 기능 흐름도
 
```
[앱 실행]
    │
    ├── 첫 실행 → [OnboardingActivity] → 정보 입력 → [MainActivity]
    │
    └── 재실행 → [SplashActivity] → [MainActivity]
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
              [물 마시기 버튼]      [하단 네비게이션]        [알림 수신]
                    │                     │                     │
              DB에 기록 저장        화면 이동 (5개 탭)     앱 열기 → 메인
              캐릭터 경험치 +             │
              진행률 업데이트             ├── 통계
                                        ├── 스트레칭
                                        ├── 랭킹
                                        └── 마이페이지
```
 
---
 
## 8. 데이터베이스 설계 (Room)
 
### users 테이블
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | INTEGER (PK) | 사용자 ID |
| nickname | TEXT | 닉네임 |
| weight | REAL | 체중 (kg) |
| activity_level | INTEGER | 활동량 (1: 적음, 2: 보통, 3: 많음) |
| daily_goal | INTEGER | 하루 목표량 (ml) |
| wake_time | TEXT | 기상 시간 |
| sleep_time | TEXT | 취침 시간 |
 
### drink_records 테이블
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | INTEGER (PK) | 기록 ID |
| amount | INTEGER | 섭취량 (ml) |
| timestamp | INTEGER | 기록 시간 (Unix timestamp) |
| date | TEXT | 날짜 (yyyy-MM-dd) |
 
### characters 테이블
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | INTEGER (PK) | 캐릭터 ID |
| name | TEXT | 캐릭터 이름 |
| level | INTEGER | 레벨 |
| exp | INTEGER | 경험치 |
| mood | INTEGER | 기분 상태 (0~100) |
| is_active | INTEGER | 현재 사용 중 여부 |
 
### items 테이블
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | INTEGER (PK) | 아이템 ID |
| name | TEXT | 아이템 이름 |
| type | TEXT | 아이템 유형 (hat / accessory / background) |
| price | INTEGER | 가격 (코인) |
| is_owned | INTEGER | 보유 여부 |
| is_equipped | INTEGER | 장착 여부 |
 
---
