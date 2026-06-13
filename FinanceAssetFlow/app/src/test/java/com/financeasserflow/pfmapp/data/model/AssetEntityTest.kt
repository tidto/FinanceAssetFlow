package com.financeasserflow.pfmapp.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AssetEntityTest {

    // ── currentValue ──────────────────────────────────────────────────────────

    @Test
    fun investmentAssetUsesValuationAsCurrentValue() {
        val asset = AssetEntity(
            name = "삼성전자",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 3_000_000,
            principalAmount = 3_000_000,
            valuationAmount = 3_240_000,
        )

        assertEquals(3_240_000L, asset.currentValue())
    }

    @Test
    fun investmentAssetWithoutValuationFallsBackToAmount() {
        val asset = AssetEntity(
            name = "비상장주식",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 2_000_000,
            principalAmount = 2_000_000,
            valuationAmount = null,
        )

        assertEquals(2_000_000L, asset.currentValue())
    }

    @Test
    fun nonInvestmentAssetUsesAmountAsCurrentValue() {
        val asset = AssetEntity(
            name = "입출금통장",
            assetType = AssetType.ASSET,
            category = AssetCategory.CASH,
            amount = 1_250_000,
        )

        assertEquals(1_250_000L, asset.currentValue())
    }

    @Test
    fun savingsAssetUsesAmountAsCurrentValue() {
        val asset = AssetEntity(
            name = "정기예금",
            assetType = AssetType.ASSET,
            category = AssetCategory.SAVINGS,
            amount = 5_000_000,
        )

        assertEquals(5_000_000L, asset.currentValue())
    }

    @Test
    fun liabilityAssetUsesAmountAsCurrentValue() {
        val asset = AssetEntity(
            name = "학자금대출",
            assetType = AssetType.LIABILITY,
            category = AssetCategory.LIABILITY,
            amount = 1_800_000,
        )

        assertEquals(1_800_000L, asset.currentValue())
    }

    // ── profitAmount ─────────────────────────────────────────────────────────

    @Test
    fun profitAmountIsPositiveWhenGain() {
        val asset = AssetEntity(
            name = "삼성전자",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 3_000_000,
            principalAmount = 3_000_000,
            valuationAmount = 3_240_000,
        )

        assertEquals(240_000L, asset.profitAmount())
    }

    @Test
    fun profitAmountIsNegativeWhenLoss() {
        val asset = AssetEntity(
            name = "S&P500 ETF",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 1_920_000,
            principalAmount = 2_000_000,
            valuationAmount = 1_920_000,
        )

        assertEquals(-80_000L, asset.profitAmount())
    }

    @Test
    fun profitAmountIsNullWhenNoPrincipal() {
        val asset = AssetEntity(
            name = "무원금주식",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 1_000_000,
            principalAmount = null,
            valuationAmount = 1_200_000,
        )

        assertNull(asset.profitAmount())
    }

    @Test
    fun profitAmountIsNullWhenNoValuation() {
        val asset = AssetEntity(
            name = "미평가주식",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 1_000_000,
            principalAmount = 1_000_000,
            valuationAmount = null,
        )

        assertNull(asset.profitAmount())
    }

    // ── profitRate ────────────────────────────────────────────────────────────

    @Test
    fun profitRateIsCalculatedCorrectly() {
        val asset = AssetEntity(
            name = "삼성전자",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 3_000_000,
            principalAmount = 3_000_000,
            valuationAmount = 3_240_000,
        )

        assertEquals(8.0, asset.profitRate()!!, 0.0001)
    }

    @Test
    fun profitRateIsNegativeWhenLoss() {
        val asset = AssetEntity(
            name = "S&P500 ETF",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 1_920_000,
            principalAmount = 2_000_000,
            valuationAmount = 1_920_000,
        )

        assertEquals(-4.0, asset.profitRate()!!, 0.0001)
    }

    @Test
    fun profitRateIsNullWhenPrincipalIsZero() {
        val asset = AssetEntity(
            name = "무상증자",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 100_000,
            principalAmount = 0,
            valuationAmount = 100_000,
        )

        assertNull(asset.profitRate())
    }

    @Test
    fun profitRateIsNullWhenNoPrincipal() {
        val asset = AssetEntity(
            name = "무원금",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 1_000_000,
            principalAmount = null,
            valuationAmount = 1_000_000,
        )

        assertNull(asset.profitRate())
    }

    // ── isInvestmentAsset ─────────────────────────────────────────────────────

    @Test
    fun isInvestmentAssetReturnsTrueForInvestmentCategory() {
        val asset = AssetEntity(
            name = "주식",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            amount = 1_000_000,
        )

        assertTrue(asset.isInvestmentAsset())
    }

    @Test
    fun isInvestmentAssetReturnsFalseForCash() {
        val asset = AssetEntity(
            name = "현금",
            assetType = AssetType.ASSET,
            category = AssetCategory.CASH,
            amount = 500_000,
        )

        assertFalse(asset.isInvestmentAsset())
    }

    @Test
    fun isInvestmentAssetReturnsFalseForLiability() {
        val asset = AssetEntity(
            name = "대출",
            assetType = AssetType.LIABILITY,
            category = AssetCategory.LIABILITY,
            amount = 1_000_000,
        )

        assertFalse(asset.isInvestmentAsset())
    }

    // ── isDebt ────────────────────────────────────────────────────────────────

    @Test
    fun isDebtReturnsTrueForLiability() {
        val asset = AssetEntity(
            name = "대출",
            assetType = AssetType.LIABILITY,
            category = AssetCategory.LIABILITY,
            amount = 1_000_000,
        )

        assertTrue(asset.isDebt())
    }

    @Test
    fun isDebtReturnsFalseForAsset() {
        val asset = AssetEntity(
            name = "예금",
            assetType = AssetType.ASSET,
            category = AssetCategory.SAVINGS,
            amount = 5_000_000,
        )

        assertFalse(asset.isDebt())
    }
}
