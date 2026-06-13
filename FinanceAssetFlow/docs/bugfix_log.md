# 버그 수정 이력

## BUG-001: AssetViewModelFactory — Hilt 도입 후 Activity에서 수동 생성 잔존

| 항목 | 내용 |
| --- | --- |
| 버그 ID | BUG-001 |
| 발견 일자 | 2026-06-13 |
| 영향 범위 | MainActivity, FinanceAssetFlowApp |
| 재현 조건 | Hilt 플러그인 추가 후 `application as FinanceAssetFlowApplication` 코드가 남아 있는 상태에서 앱 실행 |
| 기대 결과 | Hilt가 ViewModel을 주입하여 앱이 정상 실행됨 |
| 실제 결과 | `ClassCastException`: HiltTestApplication을 FinanceAssetFlowApplication으로 캐스팅 실패. 실 기기에서도 Hilt 이중 초기화 경고 발생 |
| 원인 | `@HiltAndroidApp` 추가 후에도 `MainActivity`에서 `application as FinanceAssetFlowApplication`로 수동으로 repository를 꺼내 `AssetViewModelFactory`에 넘기는 코드가 남아 있었음 |
| 수정 내용 | `FinanceAssetFlowApplication`에서 수동 `database`/`repository` 프로퍼티 제거. `MainActivity`에서 `@AndroidEntryPoint` 추가 및 수동 팩토리 코드 제거. `FinanceAssetFlowApp`에서 `hiltViewModel()` 사용으로 전환 |
| 회귀 방지 | `HiltTestRunner`를 `testInstrumentationRunner`로 등록하여 계측 테스트 환경에서도 Hilt 주입이 정상 작동함을 확인 |

---

## BUG-002: 대시보드 검색 시 검색 결과 0건인데 "자산 목록" 레이블 그대로 표시

| 항목 | 내용 |
| --- | --- |
| 버그 ID | BUG-002 |
| 발견 일자 | 2026-06-13 |
| 영향 범위 | DashboardScreen |
| 재현 조건 | 검색창에 존재하지 않는 이름 입력 후 결과 확인 |
| 기대 결과 | "검색 결과 (0건)"으로 레이블 변경 |
| 실제 결과 | "자산 목록" 레이블 그대로 유지 |
| 원인 | `DashboardScreen`의 섹션 헤더 Row가 `searchQuery`를 구독하지 않고 고정 문자열 사용 |
| 수정 내용 | `collectAsStateWithLifecycle()`로 `searchQuery`를 구독하여 레이블을 `"검색 결과 (N건)"` 또는 `"자산 목록"`으로 동적 표시 |
| 회귀 방지 | `onNodeWithText("검색 결과")` Compose 테스트로 검색 상태를 검증 |

---

## BUG-003: 투자자산 분류 시 amount 필드에 valuationAmount가 아닌 principalAmount가 중복 저장

| 항목 | 내용 |
| --- | --- |
| 버그 ID | BUG-003 |
| 발견 일자 | 2026-06-13 |
| 영향 범위 | AssetRepository.saveAsset |
| 재현 조건 | 투자자산 등록 후 대시보드에서 총자산 확인 |
| 기대 결과 | 총자산에 valuationAmount(현재 평가금액) 반영 |
| 실제 결과 | amount에 principalAmount가 그대로 저장되어 총자산이 실제보다 낮게 표시됨 |
| 원인 | `saveAsset()`에서 `normalized = asset.copy(amount = asset.currentValue())`로 normalize 하는 로직이 `currentValue()` 확장 함수를 올바르게 호출하고 있었으나, `AssetEditorUiState.toEntity()`에서 투자자산의 `amount`에 `valuationAmount.toLong()`을 할당하지 않은 케이스가 있었음 |
| 수정 내용 | `AssetEditorUiState.toEntity()`에서 `isInvestment == true`이면 `amount = valuationAmount.toLong()`으로 명시적 할당. `AssetRepository.saveAsset()`에서도 `normalized.copy(amount = normalized.currentValue())`로 double-check |
| 회귀 방지 | `AssetEntityTest.investmentAssetUsesValuationAsCurrentValue()` 테스트로 회귀 방지 |

---

## BUG-004: PortfolioTargetEntity upsert 시 기존 목표 비율 덮어씌워지지 않음

| 항목 | 내용 |
| --- | --- |
| 버그 ID | BUG-004 |
| 발견 일자 | 2026-06-13 |
| 영향 범위 | PortfolioTargetDao, PortfolioScreen |
| 재현 조건 | 포트폴리오 목표 화면에서 기존 목표 비율 수정 후 저장 |
| 기대 결과 | 수정된 비율로 DB에 업데이트됨 |
| 실제 결과 | 새 row가 INSERT되어 같은 category가 2개 이상 존재 |
| 원인 | `PortfolioTargetEntity`의 `category`에 `UNIQUE` 인덱스가 설정되어 있으나, 저장 시 `id`를 0으로 설정하지 않아 기존 PK와 다른 row로 삽입됨 |
| 수정 내용 | `PortfolioTargetDao`에 `@Insert(onConflict = OnConflictStrategy.REPLACE)` 적용 완료 확인. `upsertPortfolioTarget()` 호출 시 `id = 0` (신규 upsert 방식) 으로 전달하도록 `PortfolioScreen` 저장 로직 통일 |
| 회귀 방지 | PortfolioTargetDao에 UNIQUE 인덱스가 `category`에 설정되어 있으므로 REPLACE 전략으로 자동 처리됨 |

---

## 예상 리스크 및 예방 계획

### R-01. 투자자산 현재 가치 계산 오류

- **위험:** `amount`, `principalAmount`, `valuationAmount` 중 어떤 값을 총자산에 반영할지 혼동
- **예방:** `currentValue()` 확장 함수 단일 진입점 사용. `AssetEntityTest`로 케이스 고정

### R-02. 부채가 자산 비율에 포함되는 오류

- **위험:** 부채가 분류별 자산 비율 계산에 포함되면 대시보드가 금융적으로 잘못 표시됨
- **예방:** `NetWorthCalculatorTest.debtIsExcludedFromCategoryRatio()` 테스트로 검증

### R-03. Flow 갱신 누락

- **위험:** DB 수정 후 화면이 자동 갱신되지 않음
- **예방:** DAO 반환 타입을 Flow로 설계, ViewModel에서 StateFlow로 변환. Hilt를 통해 Singleton Repository 보장
