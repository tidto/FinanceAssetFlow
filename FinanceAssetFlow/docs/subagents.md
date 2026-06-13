# AI Agent 협업 기록

## 1. 역할 분담

| 역할 | 담당 관점 | 기록 문서 |
| --- | --- | --- |
| 설계 Agent | 요구사항 정리, 데이터 모델, 화면 흐름 | `requirements.md`, `data.md` |
| 구현 Agent | Kotlin 코드, Room, Compose, ViewModel, Hilt DI | `implementation_log.md` |
| 테스트 Agent | 단위 테스트, UI 테스트, lint | `test_plan.md`, `test_result.md` |
| 유지보수 Agent | 버그 재현, 원인 분석, 회귀 방지 | `bugfix_log.md` |

## 2. 협업 방식

1. 설계가 먼저 확정되면 코드로 옮긴다.
2. 구현 중 설계가 바뀌면 이유를 남긴다.
3. 버그는 재현 조건과 수정 내역을 함께 기록한다.
4. 테스트 결과는 성공 여부뿐 아니라 실패 원인도 남긴다.

## 3. 주요 협업 포인트

### KAPT → KSP 전환
에이전트가 Hilt 2.51 도입 시 KSP 기반 컴파일러를 추천. Room 컴파일러도 함께 KSP 전환하여 빌드 속도 개선.

### 4번째 화면(PortfolioScreen) 추가
대시보드에 리밸런싱 경고만 있고 목표 비율 수정 화면이 없는 문제를 에이전트가 지적. PortfolioScreen 신규 추가로 UX 완결성 확보.

### validateAssetInput() 분리
ViewModel 내 private 검증 함수를 package-level 공개 함수로 추출. AssetFormValidationTest에서 ViewModel 없이 직접 테스트 가능.

### 대시보드 검색 기능
자산 증가 시 목록 탐색 불편함을 에이전트가 예측하여 검색 StateFlow 및 combine 파이프라인 적용 제안.

## 4. 버그 발견 및 수정 기여

| 버그 | 에이전트 발견 시점 | 수정 방법 |
| --- | --- | --- |
| HiltAndroidApp과 수동 팩토리 충돌 | Hilt 도입 작업 중 | MainActivity, Application 클래스 동시 수정 |
| 투자자산 amount 필드 중복 저장 | AssetRepository 리팩토링 중 | currentValue()로 정규화 |
| PortfolioTarget 중복 upsert | PortfolioScreen 구현 중 | REPLACE 전략 + id=0 사용 |
| 검색 결과 레이블 갱신 누락 | DashboardScreen 구현 중 | collectAsStateWithLifecycle 동적 표시 |

자세한 내용은 `docs/bugfix_log.md` 참조.

## 5. 현재 상태

- 주제: 개인 자산 관리 및 포트폴리오 매니저
- 저장소: Room(SQLite) + Hilt DI
- 구조: `com.financeasserflow.pfmapp`
- 화면: 대시보드 / 자산 등록·수정 / 자산 상세 / 포트폴리오 목표 설정 (4개)

