package com.financeasserflow.pfmapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assets",
    indices = [
        Index(value = ["name"]),
        Index(value = ["assetType"]),
        Index(value = ["category"]),
    ],
)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val assetType: AssetType,
    val category: AssetCategory,
    val amount: Long,
    val principalAmount: Long? = null,
    val valuationAmount: Long? = null,
    val memo: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "asset_histories",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["changeType"]),
    ],
)
data class AssetHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val assetId: Long,
    val changeType: ChangeType,
    val previousAmount: Long? = null,
    val newAmount: Long? = null,
    val memo: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "portfolio_targets",
    indices = [
        Index(value = ["category"], unique = true),
    ],
)
data class PortfolioTargetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val category: AssetCategory,
    val targetRatio: Double,
    val note: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
)

/**
 * 일반 자산은 amount 하나로 현재 금액을 계산하고,
 * 투자자산은 평가금액이 있으면 그것을, 없으면 amount를 현재 금액으로 본다.
 */
fun AssetEntity.currentValue(): Long {
    return if (assetType == AssetType.ASSET && category == AssetCategory.INVESTMENT) {
        valuationAmount ?: amount
    } else {
        amount
    }
}

fun AssetEntity.profitAmount(): Long? {
    val principal = principalAmount ?: return null
    val valuation = valuationAmount ?: return null
    return valuation - principal
}

fun AssetEntity.profitRate(): Double? {
    val principal = principalAmount ?: return null
    if (principal == 0L) return null
    val profit = profitAmount() ?: return null
    return profit.toDouble() * 100.0 / principal.toDouble()
}

fun AssetEntity.isInvestmentAsset(): Boolean {
    return assetType == AssetType.ASSET && category == AssetCategory.INVESTMENT
}

fun AssetEntity.isDebt(): Boolean {
    return assetType == AssetType.LIABILITY
}

