2026-07-02

- 오늘 학습: 이 리포지토리는 멀티 모듈 Android 프로젝트(app, gravityexam, relative, dogcatshow, formtag)를 포함함.
- 각 모듈의 MainActivity는 enableEdgeToEdge와 WindowInsets를 사용해 시스템 바 패딩을 처리하는 공통 패턴을 사용함을 확인함.
- dogcatshow 모듈에서는 ViewBinding과 버튼 클릭으로 간단한 UI 상태 전환(강아지/고양이 표시)을 구현한 것을 확인함.
- Gradle 설정(settings.gradle.kts, build.gradle.kts)은 pluginManagement와 플러그인/레포지토리 구성이 되어 있음(라이브러리 참조 사용).
- 패키지 네임스페이스는 kr.hnu.ice.*로 통일되어 있으며, 유닛/인스트루먼트 테스트 파일도 포함되어 있음.
