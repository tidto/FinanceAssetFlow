# 개발 체크리스트

## Phase 0. 요구사항 정리

- [x] 과제 요구사항 원문 확인
- [x] 프로젝트 주제 확정
- [x] Room(SQLite) 사용 방향 확정
- [x] 패키지 구조 초안 확정
- [x] 제출 문서 목록 정리

## Phase 1. 프로젝트 골격

- [x] Android Studio 프로젝트 생성
- [x] `com.financeasserflow.pfmapp` 패키지 생성
- [x] `data/local`, `data/model`, `data/repository` 구성
- [x] `ui/screen`, `ui/components`, `ui/theme` 구성
- [x] `viewmodel` 구성
- [x] `di/` 패키지 구성 (Hilt DI)
- [x] Gradle 의존성 추가 (KSP + Hilt + Room)

## Phase 2. 데이터 계층

- [x] Asset Entity 구현 (AssetEntity, AssetHistoryEntity, PortfolioTargetEntity)
- [x] AssetType / AssetCategory / ChangeType 구현
- [x] AssetDao, AssetHistoryDao, PortfolioTargetDao 구현
- [x] TypeConverters 구현
- [x] AppDatabase 구현
- [x] AssetRepository 구현 (upsertPortfolioTarget 포함)

## Phase 3. 상태 관리

- [x] AssetViewModel 구현 (@HiltViewModel @Inject)
- [x] 총자산, 총부채, 순자산 계산
- [x] 투자 평가손익, 수익률 계산
- [x] 입력 검증 상태 관리
- [x] 포트폴리오 목표 편집 상태 관리 (PortfolioEditUiState)
- [x] 대시보드 검색 필터 상태 관리

## Phase 4. UI

- [x] DashboardScreen 구현 (검색 바 + 포트폴리오 네비게이션 버튼)
- [x] AssetEntryScreen 구현
- [x] AssetDetailScreen 구현
- [x] PortfolioScreen 구현 (4번째 화면 — 목표 비율 설정)
- [x] AssetCard 컴포넌트 구현
- [x] Navigation 연결 (4개 화면)
- [x] 오류/빈 상태 UI 구현
- [x] 검색 결과 없음 상태 UI 구현

## Phase 5. DI (Hilt)

- [x] `@HiltAndroidApp` Application 클래스 적용
- [x] `@AndroidEntryPoint` MainActivity 적용
- [x] `@HiltViewModel` + `@Inject` AssetViewModel 적용
- [x] AppModule (AppDatabase, AssetRepository 제공) 구현
- [x] HiltTestRunner 구현 (계측 테스트용)

## Phase 6. 테스트 및 기록

- [x] 단위 테스트 작성 (AssetEntityTest - 16개 케이스)
- [x] 단위 테스트 작성 (AssetFormValidationTest - 12개 케이스)
- [x] 단위 테스트 작성 (NetWorthCalculatorTest - 9개 케이스)
- [x] Compose UI 테스트 작성 (MainActivityTest - 6개 케이스, HiltAndroidTest)
- [x] `docs/test_result.md` 기록
- [x] `docs/bugfix_log.md` 기록
- [x] `docs/implementation_log.md` 갱신

## Phase 8. 기능 확장 (취업 포트폴리오 강화)

- [x] 순자산 추이 막대 그래프 구현 (Compose Canvas, 외부 라이브러리 없음)
  - `NetWorthChartEntry` 데이터 클래스 추가
  - `AssetHistoryDao.observeAllHistories()` 쿼리 추가
  - `AssetRepository.observeAllHistories()` 추가
  - `AssetViewModel.buildNetWorthChart()` 월별 delta 누적 계산
  - `DashboardScreen` — `NetWorthChartSection` + `NetWorthBarChart` 컴포저블 추가
- [x] 리밸런싱 자동 계산기 구현
  - `RebalancingAction` enum, `RebalancingItem` 데이터 클래스 추가
  - `AssetViewModel.calculateRebalancing()` — BUY / SELL / BALANCED 판정 + 임계값 1%
  - `onPortfolioTargetRatioChange()` — 목표 비율 변경 시 실시간 재계산
  - `PortfolioScreen` — `RebalancingSection` + `RebalancingRow` 컴포저블 추가
- [ ] 실시간 주식 시세 API 연동 (예정 — API 키 필요)

## Phase 7. 문서화

- [x] `docs/requirements.md`
- [x] `docs/data.md`
- [x] `docs/seed_data.md`
- [x] `docs/checklist.md` (현재 파일)
- [x] `docs/test_plan.md`
- [x] `docs/test_result.md`
- [x] `docs/subagents.md`
- [x] `docs/implementation_log.md`
- [x] `docs/bugfix_log.md`
- [x] `docs/ui/index.html` 및 화면별 HTML
- [x] `README.md`
