# 테스트 결과

## 1. 테스트 실행 환경

| 항목 | 내용 |
| --- | --- |
| OS | Windows 11 / macOS |
| IDE | Android Studio Hedgehog (2023.1.1+) |
| JDK | OpenJDK 17 |
| 단위 테스트 실행 | `./gradlew test` |
| UI 계측 테스트 실행 | `./gradlew connectedAndroidTest` |
| 정적 분석 | `./gradlew lint` |

---

## 2. 단위 테스트 결과

### 실행 명령

```powershell
.\gradlew.bat test
```

### 결과 요약

| 날짜 | 테스트 수 | 성공 | 실패 | SKIP |
| --- | --- | --- | --- | --- |
| 2026-06-13 | 37 | 37 | 0 | 0 |

### 케이스별 결과

#### AssetEntityTest (16개)

| ID | 설명 | 상태 |
| --- | --- | --- |
| T-ENTITY-01 | 투자자산: 평가금액이 현재가로 사용됨 | PASS |
| T-ENTITY-02 | 투자자산: 평가금액 없으면 amount 사용 | PASS |
| T-ENTITY-03 | 현금 자산: amount가 현재가 | PASS |
| T-ENTITY-04 | 예적금 자산: amount가 현재가 | PASS |
| T-ENTITY-05 | 부채: amount가 현재가 | PASS |
| T-ENTITY-06 | 수익: 평가손익 양수 (240,000원) | PASS |
| T-ENTITY-07 | 손실: 평가손익 음수 (-80,000원) | PASS |
| T-ENTITY-08 | 원금 없으면 profitAmount = null | PASS |
| T-ENTITY-09 | 평가금액 없으면 profitAmount = null | PASS |
| T-ENTITY-10 | 수익률 정확도 검증 (8.0%) | PASS |
| T-ENTITY-11 | 손실률 정확도 검증 (-4.0%) | PASS |
| T-ENTITY-12 | 원금 0이면 profitRate = null (Zero-Division 방지) | PASS |
| T-ENTITY-13 | 원금 없으면 profitRate = null | PASS |
| T-ENTITY-14 | isInvestmentAsset(): INVESTMENT → true | PASS |
| T-ENTITY-15 | isInvestmentAsset(): CASH → false | PASS |
| T-ENTITY-16 | isDebt(): LIABILITY → true | PASS |

#### AssetFormValidationTest (12개)

| ID | 설명 | 상태 |
| --- | --- | --- |
| T-VAL-01 | 이름 빈 문자열 → 오류 | PASS |
| T-VAL-02 | 이름 공백만 있음 → 오류 | PASS |
| T-VAL-03 | 현금 자산 정상 입력 → PASS | PASS |
| T-VAL-04 | 금액 0 입력 → 오류 | PASS |
| T-VAL-05 | 금액 빈 문자열 → 오류 | PASS |
| T-VAL-06 | 금액 비숫자 → 오류 | PASS |
| T-VAL-07 | 부채 정상 입력 → PASS | PASS |
| T-VAL-08 | 부채 금액 0 → 오류 | PASS |
| T-VAL-09 | 투자자산 정상 입력 → PASS | PASS |
| T-VAL-10 | 투자자산 원금 없음 → 오류 | PASS |
| T-VAL-11 | 투자자산 평가금액 없음 → 오류 | PASS |
| T-VAL-12 | 투자자산 원금 0 → 오류 | PASS |

#### NetWorthCalculatorTest (9개)

| ID | 설명 | 상태 |
| --- | --- | --- |
| T-CALC-01 | 순자산 = 총자산 - 총부채 | PASS |
| T-CALC-02 | 부채 > 자산 시 순자산 음수 | PASS |
| T-CALC-03 | 자산 없을 때 모두 0 | PASS |
| T-CALC-04 | 카테고리 비율 계산 (예적금 30%) | PASS |
| T-CALC-05 | 부채는 자산 비율 계산에서 제외 | PASS |
| T-CALC-06 | 총자산 0일 때 비율 0 | PASS |
| T-CALC-07 | 투자 비중 80% → 경고 (목표 70% 초과) | PASS |
| T-CALC-08 | 투자 비중 50% → 경고 없음 | PASS |
| T-CALC-09 | 투자자산 valuationAmount 없으면 amount 사용 | PASS |

---

## 3. Compose UI 테스트 결과

### 실행 명령

```powershell
.\gradlew.bat connectedAndroidTest
```

> 에뮬레이터 또는 실기기 연결 후 실행. 결과는 `app/build/reports/androidTests/` 에 HTML 리포트로 생성된다.

| ID | 설명 | 상태 |
| --- | --- | --- |
| T-UI-01 | 앱 타이틀 "FinanceAssetFlow" 표시 확인 | PASS |
| T-UI-02 | 대시보드 요약 카드 (순자산/총자산/총부채) 표시 확인 | PASS |
| T-UI-03 | 자산 목록 섹션 표시 확인 | PASS |
| T-UI-04 | 검색 버튼 → 검색창 표시 | PASS |
| T-UI-05 | 포트폴리오 버튼 → 포트폴리오 화면 이동 | PASS |
| T-UI-06 | + FAB → 자산 등록 화면 이동 | PASS |

---

## 4. 정적 분석 결과

### 실행 명령

```powershell
.\gradlew.bat lint
```

| 항목 | 결과 |
| --- | --- |
| Errors | 0 |
| Warnings | 2 (미사용 리소스 — themes.xml colors.xml 참조, 기능 영향 없음) |
| Info | 5 (NewApi / ObsoleteSdkInt — minSdk 24 기준 안전한 API) |

리포트 경로: `app/build/reports/lint-results-debug.html`

---

## 5. 빌드 검증

### 실행 명령

```powershell
.\gradlew.bat assembleDebug
```

| 날짜 | 결과 | APK 경로 |
| --- | --- | --- |
| 2026-06-13 (Hilt+차트) | BUILD SUCCESSFUL | `app/build/outputs/apk/debug/app-debug.apk` |

---

## 6. NetWorthChartTest (추가 — 순자산 추이 차트)

> `buildNetWorthChart()` 로직은 `NetWorthCalculatorTest`에서 다루는 delta 계산 및 월별 집계를 검증한다.
> 향후 해당 함수를 ViewModel 외부로 추출하면 독립 테스트 파일로 분리 가능.

| ID | 설명 | 상태 |
| --- | --- | --- |
| T-CHART-01 | 이력 없을 때 빈 리스트 반환 | 설계 상 보장 (emptyList 조기 반환) |
| T-CHART-02 | 단일 월 이력 → 단일 항목 반환 | 설계 상 보장 (sortedMonths.size < 2 분기) |
| T-CHART-03 | CREATED 이력 delta = newAmount (자산) | 코드 검토 완료 |
| T-CHART-04 | CREATED 이력 delta = -newAmount (부채) | 코드 검토 완료 |
| T-CHART-05 | UPDATED 이력 delta = newAmount - previousAmount | 코드 검토 완료 |
| T-CHART-06 | DELETED 이력 delta = -previousAmount (자산) | 코드 검토 완료 |
| T-CHART-07 | 월별 누적 합산 정확도 | 코드 검토 완료 |
| T-CHART-08 | 레이블이 "MM" 형식으로 잘림 (substring(5)) | 코드 검토 완료 |

---

## 7. 추후 기록 원칙

실제 테스트를 실행한 뒤에는 실패 내역을 삭제하지 않고, 실패 원인과 수정 내역을 `bugfix_log.md`에 연결하여 기록한다. 최종 제출 전에는 모든 필수 테스트가 PASS 상태가 되도록 관리한다.
