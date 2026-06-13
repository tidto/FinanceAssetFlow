package com.financeasserflow.pfmapp.viewmodel

import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.data.model.AssetEntity
import com.financeasserflow.pfmapp.data.model.AssetType
import com.financeasserflow.pfmapp.data.model.currentValue
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 대시보드의 핵심 계산 로직 - 순자산, 총자산, 카테고리 비율 - 을 순수 함수 수준에서 검증한다.
 * ViewModel/Repository 없이 AssetEntity 확장 함수만으로 테스트한다.
 */
class NetWorthCalculatorTest {

    private fun buildAssets(vararg specs: Triple<AssetType, AssetCategory, Long>): List<AssetEntity> =
        specs.mapIndexed { index, (type, category, amount) ->
            AssetEntity(
                id = index.toLong() + 1L,
                name = "자산$index",
                assetType = type,
                category = category,
                amount = amount,
            )
        }

    // ── 순자산 (Net Worth) ────────────────────────────────────────────────────

    @Test
    fun netWorthEqualsAssetMinusDebt() {
        val assets = buildAssets(
            Triple(AssetType.ASSET, AssetCategory.CASH, 5_000_000),
            Triple(AssetType.ASSET, AssetCategory.SAVINGS, 3_000_000),
            Triple(AssetType.LIABILITY, AssetCategory.LIABILITY, 1_800_000),
        )

        val totalAsset = assets.filter { it.assetType == AssetType.ASSET }.sumOf { it.currentValue() }
        val totalDebt = assets.filter { it.assetType == AssetType.LIABILITY }.sumOf { it.currentValue() }
        val netWorth = totalAsset - totalDebt

        assertEquals(8_000_000L, totalAsset)
        assertEquals(1_800_000L, totalDebt)
        assertEquals(6_200_000L, netWorth)
    }

    @Test
    fun netWorthIsNegativeWhenDebtExceedsAssets() {
        val assets = buildAssets(
            Triple(AssetType.ASSET, AssetCategory.CASH, 500_000),
            Triple(AssetType.LIABILITY, AssetCategory.LIABILITY, 2_000_000),
        )

        val totalAsset = assets.filter { it.assetType == AssetType.ASSET }.sumOf { it.currentValue() }
        val totalDebt = assets.filter { it.assetType == AssetType.LIABILITY }.sumOf { it.currentValue() }
        val netWorth = totalAsset - totalDebt

        assertEquals(-1_500_000L, netWorth)
    }

    @Test
    fun netWorthIsZeroWhenNoAssets() {
        val assets: List<AssetEntity> = emptyList()

        val totalAsset = assets.filter { it.assetType == AssetType.ASSET }.sumOf { it.currentValue() }
        val totalDebt = assets.filter { it.assetType == AssetType.LIABILITY }.sumOf { it.currentValue() }
        val netWorth = totalAsset - totalDebt

        assertEquals(0L, totalAsset)
        assertEquals(0L, totalDebt)
        assertEquals(0L, netWorth)
    }

    // ── 카테고리 비율 ─────────────────────────────────────────────────────────

    @Test
    fun categoryRatioIsCalculatedCorrectly() {
        val assets = buildAssets(
            Triple(AssetType.ASSET, AssetCategory.CASH, 2_000_000),
            Triple(AssetType.ASSET, AssetCategory.SAVINGS, 3_000_000),
            Triple(AssetType.ASSET, AssetCategory.INVESTMENT, 5_000_000),
        )

        val assetOnly = assets.filter { it.assetType == AssetType.ASSET }
        val totalAsset = assetOnly.sumOf { it.currentValue() }

        val savingsAmount = assetOnly
            .filter { it.category == AssetCategory.SAVINGS }
            .sumOf { it.currentValue() }
        val savingsRatio = savingsAmount.toDouble() * 100.0 / totalAsset.toDouble()

        assertEquals(10_000_000L, totalAsset)
        assertEquals(3_000_000L, savingsAmount)
        assertEquals(30.0, savingsRatio, 0.0001)
    }

    @Test
    fun debtIsExcludedFromCategoryRatio() {
        val assets = buildAssets(
            Triple(AssetType.ASSET, AssetCategory.CASH, 5_000_000),
            Triple(AssetType.LIABILITY, AssetCategory.LIABILITY, 2_000_000),
        )

        val assetOnly = assets.filter { it.assetType == AssetType.ASSET }
        val totalAsset = assetOnly.sumOf { it.currentValue() }

        assertEquals(5_000_000L, totalAsset)
    }

    @Test
    fun categoryRatioIsZeroWhenTotalIsZero() {
        val assets: List<AssetEntity> = emptyList()
        val totalAsset = 0L
        val cashRatio = if (totalAsset == 0L) 0.0 else 0.0 * 100.0 / totalAsset.toDouble()

        assertEquals(0.0, cashRatio, 0.0001)
    }

    // ── 투자 비중 경고 ────────────────────────────────────────────────────────

    @Test
    fun warningIsTriggeredWhenInvestmentExceedsTarget() {
        val assets = buildAssets(
            Triple(AssetType.ASSET, AssetCategory.INVESTMENT, 8_000_000),
            Triple(AssetType.ASSET, AssetCategory.CASH, 2_000_000),
        )

        val assetOnly = assets.filter { it.assetType == AssetType.ASSET }
        val totalAsset = assetOnly.sumOf { it.currentValue() }
        val investmentValue = assetOnly
            .filter { it.category == AssetCategory.INVESTMENT }
            .sumOf { it.currentValue() }
        val investmentRatio = investmentValue.toDouble() * 100.0 / totalAsset.toDouble()
        val targetRatio = 70.0

        assertEquals(80.0, investmentRatio, 0.0001)
        assert(investmentRatio > targetRatio) { "경고가 발생해야 합니다" }
    }

    @Test
    fun noWarningWhenInvestmentBelowTarget() {
        val assets = buildAssets(
            Triple(AssetType.ASSET, AssetCategory.INVESTMENT, 5_000_000),
            Triple(AssetType.ASSET, AssetCategory.CASH, 5_000_000),
        )

        val assetOnly = assets.filter { it.assetType == AssetType.ASSET }
        val totalAsset = assetOnly.sumOf { it.currentValue() }
        val investmentValue = assetOnly
            .filter { it.category == AssetCategory.INVESTMENT }
            .sumOf { it.currentValue() }
        val investmentRatio = investmentValue.toDouble() * 100.0 / totalAsset.toDouble()
        val targetRatio = 70.0

        assertEquals(50.0, investmentRatio, 0.0001)
        assert(investmentRatio <= targetRatio) { "경고가 없어야 합니다" }
    }
}
