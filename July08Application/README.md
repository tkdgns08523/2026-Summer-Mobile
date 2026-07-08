# 2026-07-08 수업 내용 정리

1. picker, pickerdialog 두 개의 모듈을 새로 구성하여 `DatePickerDialog`, `TimePickerDialog` 사용법을 익혔다.
2. picker 모듈: 버튼 클릭 시 날짜/시간 선택 다이얼로그를 띄우고, 선택 결과를 TextView에 표시하도록 구현했다.
3. pickerdialog 모듈: 날짜/시간 선택 다이얼로그 외에 `AlertDialog`(확인/취소 버튼, 확인만 있는 알림창)를 함께 구성했다.
4. `findViewById` 대신 `ViewBinding`을 적용하는 방법을 익혔다 (`buildFeatures { viewBinding = true }` 설정 후 `ActivityXxxBinding.inflate(layoutInflater)` 사용).
5. 코드 작성 중 발생한 오류(바인딩 미초기화, import 누락, 중괄호 미닫힘 등)를 찾아 수정하며 컴파일 오류 디버깅 방법을 익혔다.
