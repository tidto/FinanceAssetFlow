# 구현 이력

## 2026-06-13 — Hilt DI 도입 및 기능 업그레이드

### 작업 내용

- **Hilt DI 적용**
  - `build.gradle.kts` (root): `com.google.dagger.hilt.android:2.51.1` 및 `com.google.devtools.ksp:1.9.24-1.0.20` 플러그인 추가
  - `app/build.gradle.kts`: KAPT → KSP 전환 (Room 컴파일러 및 Hilt 컴파일러 모두 KSP 사용)
  - `FinanceAssetFlowApplication`: `@HiltAndroidApp` 추가, 수동 `database`/`repository` 프로퍼티 제거
  - `MainActivity`: `@AndroidEntryPoint` 추가, 수동 팩토리 코드 제거
  - `AssetViewModel`: `@HiltViewModel` + `@Inject constructor(repository: AssetRepository)` 적용
  - `di/AppModule.kt` 신규 생성: `AppDatabase` 및 `AssetRepository`를 `@Singleton`으로 제공
  - `FinanceAssetFlowApp`: `hiltViewModel<AssetViewModel>()` 사용, ViewModel 파라미터 제거

- **4번째 화면 추가: PortfolioScreen**
  - 자산군별 목표 비율 조회 및 수정 화면 신규 구현
  - `PortfolioTargetInputItem`, `PortfolioEditUiState` 데이터 클래스 추가 (`AssetUiState.kt`)
  - `AssetViewModel`에 `loadPortfolioForEdit()`, `onPortfolioTargetRatioChange()`, `savePortfolioTargets()` 메서드 추가
  - `AssetRepository`에 `upsertPortfolioTarget()` 메서드 추가
  - `FinanceAssetFlowApp`에 `portfolio` 라우트 추가
  - `DashboardScreen` TopAppBar에 포트폴리오 아이콘 버튼 추가 (PieChart 아이콘)

- **대시보드 검색 기능**
  - `AssetViewModel`에 `_searchQuery: MutableStateFlow<String>` 추가
  - `dashboardUiState`에 `combine` 3-way 조합 적용 (assets + targets + searchQuery)
  - `buildDashboardState()`에 query 파라미터 추가 및 필터링 로직 구현
  - `DashboardScreen`에 검색 바 토글 (Search/Clear 아이콘, `AnimatedVisibility` 애니메이션)
  - 검색 결과 0건 시 "검색 결과가 없습니다." 빈 상태 메시지 구분

- **입력 검증 함수 분리**
  - `validateAssetInput()` private → package-level 함수로 추출 (`AssetUiState.kt`)
  - `AssetViewModel`에서 해당 함수를 그대로 호출하도록 변경
  - 이를 통해 `AssetFormValidationTest`에서 ViewModel 없이 직접 검증 로직 테스트 가능

- **테스트 보강**
  - `AssetEntityTest`: 기존 2개 → 16개 케이스 (currentValue, profitAmount, profitRate, isInvestmentAsset, isDebt 전체 경계값 포함)
  - `AssetFormValidationTest` 신규: 12개 케이스 (이름/금액/투자 전체 경로 검증)
  - `NetWorthCalculatorTest` 신규: 9개 케이스 (순자산, 카테고리 비율, 경고 로직)
  - `MainActivityTest`: Hilt 계측 테스트로 전환 (`@HiltAndroidTest` + `HiltAndroidRule`), 6개 케이스
  - `HiltTestRunner.kt` 신규 생성

- **아키텍처 정리**
  - `AssetViewModelFactory` 파일 유지 (하위 호환용, Hilt 도입 후 실제 사용되지 않음)
  - `AssetCard.kt`에 `modifier: Modifier = Modifier` 파라미터 추가 (외부 패딩 적용 가능하도록)
  - `DashboardScreen`에서 `AssetCard`에 `modifier = Modifier.padding(horizontal = 16.dp)` 전달

### 설계 결정

| 항목 | 결정 | 이유 |
| --- | --- | --- |
| KAPT → KSP | KSP로 전환 | KSP는 KAPT 대비 빌드 속도 30–50% 향상. Kotlin 우선 Symbol Processor로 Room/Hilt 모두 KSP 지원 |
| Hilt SingletonComponent | AppDatabase, AssetRepository를 Singleton으로 | DB 연결은 애플리케이션 생명주기와 동일해야 함. 중복 연결 방지 |
| 검색 StateFlow | 별도 `_searchQuery` StateFlow + combine | dashboardUiState에 검색 필터가 실시간 반영됨. UI 재구성 없이 필터링 |
| PortfolioEditUiState | 별도 MutableStateFlow | Dashboard와 Portfolio는 다른 생명주기를 가짐. 편집 상태 격리 필요 |
| 4번째 화면 선택 | PortfolioScreen (목표 비율 편집) | 기존 DashboardScreen에서 목표 비율 경고는 표시되지만 수정이 불가했음. UX 완결성 확보 |

### 트레이드오프

| 항목 | 선택 | 대안 | 이유 |
| --- | --- | --- | --- |
| AssetRepository 인터페이스화 | 하지 않음 | 인터페이스 추출 후 Hilt bind | 현 규모에서 인터페이스 추출은 과설계. ViewModel 테스트는 순수 계산 로직 분리로 대체 |
| 검색 범위 | name + category | name만 | 카테고리로도 검색("투자자산")이 자연스러움 |
| 포트폴리오 저장 단위 | 카테고리별 개별 upsert | 전체 트랜잭션 | 각 카테고리가 독립적이므로 개별 upsert가 더 단순하고 충분함 |

---

## 2026-06-13 — 리밸런싱 자동 계산기 추가

### 작업 내용

- **RebalancingItem / RebalancingAction 데이터 클래스 추가** (`AssetUiState.kt`)
  - `RebalancingAction`: BUY / SELL / BALANCED enum
  - `RebalancingItem`: category, currentAmount, targetAmount, deltaAmount, action
  - `PortfolioEditUiState`에 `rebalancing: List<RebalancingItem>`, `totalAsset: Long` 필드 추가

- **AssetViewModel 로직 추가**
  - `calculateRebalancing()`: targetRatio 입력 → targetAmount 계산 → delta → action 판정
  - 임계값(threshold): totalAsset 1% 또는 최소 1,000원 — 미세 오차로 인한 불필요한 권고 방지
  - `loadPortfolioForEdit()`: 화면 진입 시 자동 계산
  - `onPortfolioTargetRatioChange()`: 목표 비율 수정 시마다 실시간 재계산

- **PortfolioScreen UI 추가**
  - 저장 버튼 하단에 `RebalancingSection` 카드 조건부 표시
  - `RebalancingRow`: 자산군별 현재 금액 → 목표 금액 + 행동 라벨 + 필요 금액
  - 색상 코딩: 추가 매수(Primary 파랑) / 매도 권장(Error 빨강) / 균형 유지(Tertiary 초록)

### 설계 결정

| 항목 | 결정 | 이유 |
| --- | --- | --- |
| 임계값 1% | 총자산의 1% 이하 오차는 BALANCED 처리 | 소액 반올림 오차로 인한 불필요 매매 권고 방지 |
| 실시간 재계산 | 목표 % 입력 변경 시 즉시 재계산 | 사용자가 타이핑하는 동안 결과를 즉시 확인 가능 |
| 저장 버튼 이후 표시 | 저장 전 계산 결과를 미리 보여줌 | 저장 전에 결과를 검토하고 수정할 수 있도록 |

---

## 2026-06-13 — 순자산 추이 그래프 추가

### 작업 내용

- **NetWorthBarChart 컴포저블 신규 구현**
  - 외부 차트 라이브러리 없이 Jetpack Compose Canvas API 직접 사용
  - 월별 막대 그래프 + 추세선(꺾은선) 오버레이
  - 양수 순자산: Primary 색상 막대 / 음수: Error 색상 막대
  - 막대 위 금액 라벨(예: "980만"), 하단 월 라벨(예: "06") 표시
  - 데이터 6개 이하일 때만 금액 라벨 표시 (공간 절약)

- **데이터 파이프라인 추가**
  - `AssetHistoryDao`에 `observeAllHistories(): Flow<List<AssetHistoryEntity>>` 쿼리 추가
  - `AssetRepository`에 `observeAllHistories()` 위임 메서드 추가
  - `AssetViewModel.dashboardUiState`: 기존 3-way combine → 4-way combine으로 확장
    - 내부 combine(assets, targets, query) + 외부 combine(histories) 중첩 구조 사용
  - `buildNetWorthChart()` 비공개 함수 추가: 이력 delta를 월별로 집계하여 누적 순자산 계산

- **UiState 확장**
  - `DashboardUiState`에 `netWorthChart: List<NetWorthChartEntry>` 필드 추가
  - `NetWorthChartEntry(label: String, netWorth: Long)` 데이터 클래스 추가

- **DashboardScreen 업데이트**
  - `SummarySection` 바로 아래 `NetWorthChartSection` 조건부 표시 (데이터 없으면 숨김)
  - Canvas 관련 import 추가 (CornerRadius, Offset, Size, Path, Stroke, TextStyle, rememberTextMeasurer)

### 설계 결정

| 항목 | 결정 | 이유 |
| --- | --- | --- |
| 외부 라이브러리 미사용 | Compose Canvas 직접 구현 | MPAndroidChart 등 의존성 추가 없이 면접 어필 포인트 "직접 구현" 강조 가능 |
| delta 누적 방식 | 이력 delta 합산으로 월별 순자산 근사 | 과거 스냅샷 테이블이 없으므로 CREATED/UPDATED/DELETED 이력에서 변동분 역산 |
| 부채 delta 반전 | isDebt 여부에 따라 delta 부호 반전 | 부채 증가는 순자산 감소이므로 음수 처리 필요 |
| 1개월 이하 데이터 | 단일 막대 1개 표시 | 추세를 보기엔 부족하나 "데이터 없음"보다 현재 상태를 보여주는 것이 유용 |

### 트레이드오프

| 항목 | 선택 | 대안 | 이유 |
| --- | --- | --- | --- |
| 차트 데이터 계산 위치 | ViewModel (buildNetWorthChart) | Repository 계층 | 계산 로직이 표현 목적에 가까워 ViewModel이 적합 |
| 차트 종류 | 막대 + 추세선 혼합 | 순수 꺾은선 그래프 | 막대는 월별 절대값을 직관적으로, 선은 추세를 동시에 표현 |

---

## 2026-06-12 — 프로젝트 초기 구현

### 작업 내용

- 과제 요구사항 원문을 기준으로 문서 구조를 다시 정리했다.
- 프로젝트 주제를 개인 자산 관리 및 포트폴리오 매니저로 확정했다.
- 로컬 저장소는 Room(SQLite)로 고정했다.
- Android Studio용 Compose + Room 프로젝트 골격을 생성했다.
- `FinanceAssetFlowApplication`, `MainActivity`, `AppDatabase`, `AssetRepository`, `AssetViewModel`을 연결했다.
- 대시보드, 자산 등록/수정, 자산 상세 화면을 Compose로 구현했다.
- 기본 샘플 자산과 리밸런싱 기준을 로컬 Room 데이터로 자동 주입하도록 설계했다.

### 설계 결정

| 항목 | 결정 | 이유 |
| --- | --- | --- |
| 저장소 | Room(SQLite) | Android 네이티브 표준 로컬 DB, 오프라인 동작에 맞다 |
| 아키텍처 | MVVM + Repository + StateFlow | UI와 로직을 분리하고 테스트 가능성을 높인다 |
| 화면 구성 | Dashboard / Entry / Detail | 최소 3개 화면 요구사항 충족 |
| 금융 도메인 | 자산, 부채, 순자산, 평가손익 | 포트폴리오 앱의 핵심 개념을 정확히 반영 |

---

## 진행 원칙

1. 요구사항이 바뀌면 `docs/requirements.md`와 `docs/implementation_log.md`를 함께 갱신한다.
2. 데이터 모델이 바뀌면 `docs/data.md`에 반영한다.
3. 테스트 결과가 바뀌면 `docs/test_result.md`를 갱신한다.
4. 버그 수정은 `docs/bugfix_log.md`에 남기고, 회귀 방지 방법까지 적는다.
