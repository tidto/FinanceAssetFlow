package com.financeasserflow.pfmapp.viewmodel

import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.data.model.AssetType
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AssetFormValidationTest {

    // ── 필수 필드 검증 ────────────────────────────────────────────────────────

    @Test
    fun blankNameShouldReturnError() {
        val state = AssetEditorUiState(
            name = "",
            assetType = AssetType.ASSET,
            category = AssetCategory.CASH,
            amount = "1000000",
        )
        assertNotNull(validateAssetInput(state))
    }

    @Test
    fun whitespaceOnlyNameShouldReturnError() {
        val state = AssetEditorUiState(
            name = "   ",
            assetType = AssetType.ASSET,
            category = AssetCategory.CASH,
            amount = "1000000",
        )
        assertNotNull(validateAssetInput(state))
    }

    // ── 일반 자산 금액 검증 ───────────────────────────────────────────────────

    @Test
    fun validCashAssetShouldPassValidation() {
        val state = AssetEditorUiState(
            name = "입출금통장",
            assetType = AssetType.ASSET,
            category = AssetCategory.CASH,
            amount = "1250000",
        )
        assertNull(validateAssetInput(state))
    }

    @Test
    fun zeroAmountShouldReturnError() {
        val state = AssetEditorUiState(
            name = "빈통장",
            assetType = AssetType.ASSET,
            category = AssetCategory.CASH,
            amount = "0",
        )
        assertNotNull(validateAssetInput(state))
    }

    @Test
    fun emptyAmountShouldReturnError() {
        val state = AssetEditorUiState(
            name = "예금",
            assetType = AssetType.ASSET,
            category = AssetCategory.SAVINGS,
            amount = "",
        )
        assertNotNull(validateAssetInput(state))
    }

    @Test
    fun nonNumericAmountShouldReturnError() {
        val state = AssetEditorUiState(
            name = "예금",
            assetType = AssetType.ASSET,
            category = AssetCategory.SAVINGS,
            amount = "abc",
        )
        assertNotNull(validateAssetInput(state))
    }

    // ── 부채 금액 검증 ────────────────────────────────────────────────────────

    @Test
    fun validLiabilityAssetShouldPassValidation() {
        val state = AssetEditorUiState(
            name = "학자금대출",
            assetType = AssetType.LIABILITY,
            category = AssetCategory.LIABILITY,
            amount = "1800000",
        )
        assertNull(validateAssetInput(state))
    }

    @Test
    fun zeroLiabilityAmountShouldReturnError() {
        val state = AssetEditorUiState(
            name = "대출",
            assetType = AssetType.LIABILITY,
            category = AssetCategory.LIABILITY,
            amount = "0",
        )
        assertNotNull(validateAssetInput(state))
    }

    // ── 투자자산 검증 ─────────────────────────────────────────────────────────

    @Test
    fun validInvestmentAssetShouldPassValidation() {
        val state = AssetEditorUiState(
            name = "삼성전자",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            principalAmount = "3000000",
            valuationAmount = "3240000",
        )
        assertNull(validateAssetInput(state))
    }

    @Test
    fun investmentWithMissingPrincipalShouldReturnError() {
        val state = AssetEditorUiState(
            name = "삼성전자",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            principalAmount = "",
            valuationAmount = "3240000",
        )
        assertNotNull(validateAssetInput(state))
    }

    @Test
    fun investmentWithMissingValuationShouldReturnError() {
        val state = AssetEditorUiState(
            name = "삼성전자",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            principalAmount = "3000000",
            valuationAmount = "",
        )
        assertNotNull(validateAssetInput(state))
    }

    @Test
    fun investmentWithZeroPrincipalShouldReturnError() {
        val state = AssetEditorUiState(
            name = "삼성전자",
            assetType = AssetType.ASSET,
            category = AssetCategory.INVESTMENT,
            principalAmount = "0",
            valuationAmount = "3240000",
        )
        assertNotNull(validateAssetInput(state))
    }
}
