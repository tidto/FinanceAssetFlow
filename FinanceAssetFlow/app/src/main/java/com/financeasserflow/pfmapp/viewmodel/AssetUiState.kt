package com.financeasserflow.pfmapp.viewmodel

import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.data.model.AssetEntity
import com.financeasserflow.pfmapp.data.model.AssetHistoryEntity
import com.financeasserflow.pfmapp.data.model.AssetType
import com.financeasserflow.pfmapp.data.model.currentValue
import com.financeasserflow.pfmapp.data.model.isInvestmentAsset
import com.financeasserflow.pfmapp.data.model.profitAmount
import com.financeasserflow.pfmapp.data.model.profitRate
import java.text.NumberFormat
import java.util.Locale

data class DashboardUiState(
    val totalAsset: Long = 0L,
    val totalDebt: Long = 0L,
    val netWorth: Long = 0L,
    val investmentRatio: Double = 0.0,
    val investmentTargetRatio: Double = 70.0,
    val warningMessage: String? = null,
    val categoryBars: List<CategoryBarUiState> = emptyList(),
    val items: List<AssetListItemUiState> = emptyList(),
    val searchQuery: String = "",
)

data class CategoryBarUiState(
    val category: AssetCategory,
    val amount: Long,
    val ratio: Double,
)

data class AssetListItemUiState(
    val id: Long,
    val name: String,
    val category: String,
    val type: AssetType,
    val amountText: String,
    val returnText: String?,
    val warningText: String?,
)

data class AssetEditorUiState(
    val assetId: Long? = null,
    val name: String = "",
    val assetType: AssetType = AssetType.ASSET,
    val category: AssetCategory = AssetCategory.CASH,
    val amount: String = "",
    val principalAmount: String = "",
    val valuationAmount: String = "",
    val memo: String = "",
    val isSaving: Boolean = false,
    val snackbarMessage: String? = null,
)

data class AssetDetailUiState(
    val asset: AssetEntity? = null,
    val histories: List<AssetHistoryEntity> = emptyList(),
)

data class PortfolioTargetInputItem(
    val category: AssetCategory,
    val currentRatio: Double,
    val currentAmount: Long,
    val targetRatioInput: String,
)

data class PortfolioEditUiState(
    val items: List<PortfolioTargetInputItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val snackbarMessage: String? = null,
)

fun Long.toMoneyText(): String {
    return NumberFormat.getNumberInstance(Locale.KOREA).format(this) + "원"
}

fun Double.toPercentText(): String {
    return String.format(Locale.KOREA, "%.1f%%", this)
}

fun AssetEntity.toListItem(): AssetListItemUiState {
    val currentValue = currentValue()
    val profitText = if (isInvestmentAsset()) {
        val profit = profitAmount()
        val rate = profitRate()
        when {
            profit != null && rate != null -> "${if (profit >= 0) "+" else ""}${profit.toMoneyText()} / ${if (rate >= 0) "+" else ""}${rate.toPercentText()}"
            else -> null
        }
    } else {
        null
    }

    return AssetListItemUiState(
        id = id,
        name = name,
        category = category.label,
        type = assetType,
        amountText = currentValue.toMoneyText(),
        returnText = profitText,
        warningText = null,
    )
}

fun validateAssetInput(state: AssetEditorUiState): String? {
    if (state.name.isBlank()) {
        return "자산명을 입력해 주세요."
    }

    val isInvestment = state.assetType == AssetType.ASSET && state.category == AssetCategory.INVESTMENT
    if (state.assetType == AssetType.LIABILITY) {
        val amount = state.amount.toLongOrNull()
        if (amount == null || amount <= 0L) {
            return "부채 금액은 0보다 큰 숫자로 입력해 주세요."
        }
        return null
    }

    if (isInvestment) {
        val principal = state.principalAmount.toLongOrNull()
        val valuation = state.valuationAmount.toLongOrNull()
        if (principal == null || principal <= 0L) {
            return "매입금액을 0보다 큰 숫자로 입력해 주세요."
        }
        if (valuation == null || valuation <= 0L) {
            return "현재 평가금액을 0보다 큰 숫자로 입력해 주세요."
        }
        return null
    }

    val amount = state.amount.toLongOrNull()
    if (amount == null || amount <= 0L) {
        return "금액은 0보다 큰 숫자로 입력해 주세요."
    }
    return null
}
