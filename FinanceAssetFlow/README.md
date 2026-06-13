# FinanceAssetFlow — 개인 자산 관리 앱

FinanceAssetFlow는 Kotlin + Jetpack Compose 기반 Android Native **개인 자산 관리(PFM) 앱**입니다.  
Room(SQLite) 로컬 DB를 사용하여 백엔드 없이 오프라인에서 자산, 부채, 순자산, 투자 평가손익, 포트폴리오 목표 비율을 관리합니다.

---

## 앱 개요

| 항목 | 내용 |
| --- | --- |
| 플랫폼 | Android Native |
| 언어 | Kotlin 1.9.x |
| UI | Jetpack Compose + Material 3 |
| 아키텍처 | MVVM + Repository + Hilt DI |
| 데이터 저장 | Room (SQLite), 로컬 전용 |
| 최소 SDK | API 24 (Android 7.0) |
| 대상 SDK | API 35 |

---

## 주요 기능

### F-01. 자산 CRUD
자산명, 분류(현금/예적금/투자/기타), 금액을 생성·조회·수정·삭제한다.  
부채는 별도 항목으로 관리하며 순자산 계산에 자동 반영된다.

### F-02. 다중 화면 구성 (4개 화면)
| 화면 | 경로 | 설명 |
| --- | --- | --- |
| DashboardScreen | `/` | 총자산·부채·순자산·투자비중·자산 목록, 검색 기능 |
| AssetEntryScreen | `/entry` | 자산 등록 및 수정 폼 |
| AssetDetailScreen | `/detail/{id}` | 자산 상세 + 변경 이력 |
| PortfolioScreen | `/portfolio` | 자산군별 목표 비율 설정 (4번째 화면) |

### F-03. 입력 검증 및 UI 피드백
- 숫자 키보드 강제 (`KeyboardType.Number`)
- 필수 입력 미입력 / 0 이하 금액 → Snackbar 오류 메시지
- 투자자산: 원금·평가금액 모두 입력 필수

### F-04. Room 기반 데이터 영속화
오프라인 환경에서도 모든 CRUD 동작. `Flow` 기반 실시간 UI 갱신.

### F-05. Agent 협업 기록
설계 결정, 트레이드오프, 버그 수정 이력을 `docs/` 폴더에 마크다운으로 문서화.

---

## 기술 스택

```
Kotlin 1.9.x
Jetpack Compose (Material 3)
Navigation Compose 2.8.0
Room 2.6.1 (KSP)
Hilt 2.51.1 (KSP)
ViewModel + StateFlow (MVVM)
Coroutines 1.8.1
Turbine 1.1.0 (Flow 테스트)
```

---

## 패키지 구조

```
com.financeasserflow.pfmapp
├── di/
│   └── AppModule.kt            # Hilt 모듈 (AppDatabase, AssetRepository 제공)
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── AssetDao.kt
│   │   └── Converters.kt
│   ├── model/
│   │   ├── Asset.kt            # AssetEntity, AssetHistoryEntity, PortfolioTargetEntity
│   │   └── AssetType.kt        # AssetType, AssetCategory, ChangeType enums
│   └── repository/
│       └── AssetRepository.kt
├── ui/
│   ├── screen/
│   │   ├── DashboardScreen.kt  # 검색 + 포트폴리오 네비게이션
│   │   ├── AssetEntryScreen.kt
│   │   ├── AssetDetailScreen.kt
│   │   └── PortfolioScreen.kt  # 목표 비율 설정 (4번째 화면)
│   ├── components/
│   │   └── AssetCard.kt
│   ├── theme/
│   └── FinanceAssetFlowApp.kt  # NavHost (4개 라우트)
├── viewmodel/
│   ├── AssetViewModel.kt       # @HiltViewModel, 검색·포트폴리오 상태 포함
│   └── AssetUiState.kt         # 모든 UiState + validateAssetInput()
├── FinanceAssetFlowApplication.kt  # @HiltAndroidApp
└── MainActivity.kt                 # @AndroidEntryPoint
```

---

## 빌드 방법

### 요구사항

- Android Studio Hedgehog (2023.1.1) 이상
- JDK 17
- Android SDK API 35

### 빌드 및 설치

```bash
# 디버그 APK 빌드
./gradlew assembleDebug

# 에뮬레이터/기기에 설치
./gradlew installDebug
```

Windows PowerShell:
```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

---

## 테스트 실행 방법

### 단위 테스트 (JVM, 에뮬레이터 불필요)

```bash
./gradlew test
```

테스트 파일:
- `AssetEntityTest` — 엔티티 확장 함수 16개 케이스
- `AssetFormValidationTest` — 입력 검증 로직 12개 케이스
- `NetWorthCalculatorTest` — 순자산·비율·경고 로직 9개 케이스

### Compose UI 계측 테스트 (에뮬레이터 또는 실기기 필요)

```bash
./gradlew connectedAndroidTest
```

테스트 파일: `MainActivityTest` — 6개 Compose UI 케이스 (Hilt 주입 포함)

### 정적 분석 (Lint)

```bash
./gradlew lint
```

리포트: `app/build/reports/lint-results-debug.html`

---

## 문서 목록 (`docs/`)

| 파일 | 설명 |
| --- | --- |
| `requirements.md` | 요구사항 명세 (사용자 시나리오, 기능/비기능) |
| `data.md` | 데이터 모델, ERD, 테이블 및 관계 설명 |
| `seed_data.md` | 초기 샘플 데이터 설계 |
| `checklist.md` | Phase별 개발 체크리스트 |
| `test_plan.md` | 테스트 전략 및 케이스 목록 |
| `test_result.md` | 테스트 실행 결과 기록 |
| `subagents.md` | Agent 역할 분담 및 협업 방식 |
| `implementation_log.md` | 구현 이력, 설계 결정, 트레이드오프 |
| `bugfix_log.md` | 버그 재현·원인·수정·회귀 방지 기록 |
| `ui/index.html` | 화면 목업 인덱스 |

---

## 금융 도메인 설계 포인트

### 순자산 (Net Worth)
```
순자산 = 총자산 - 총부채
```
대시보드 최상단에 표시. 일반 가계부의 단순 잔액 합산과 다르게 부채를 명시적으로 차감.

### 투자자산 평가손익
```
평가손익 = 평가금액 - 매입금액
수익률(%) = (평가손익 / 매입금액) × 100
```
주식·ETF 등 투자자산은 `principalAmount`(매입가)와 `valuationAmount`(현재 평가가)를 분리 저장.

### 포트폴리오 리밸런싱 경고
투자자산 비중이 설정한 목표 비율을 초과하면 대시보드에 경고 배너가 표시된다.  
PortfolioScreen에서 카테고리별 목표 비율을 직접 수정할 수 있다.

### 이력 추적 (AssetHistoryEntity)
자산 생성·수정·삭제·평가금액 변경 시 자동으로 이력이 기록된다. 자산 상세 화면에서 전체 변경 이력 조회 가능.
