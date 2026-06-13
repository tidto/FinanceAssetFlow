package com.financeasserflow.pfmapp.data.repository

import androidx.room.withTransaction
import com.financeasserflow.pfmapp.data.local.AppDatabase
import com.financeasserflow.pfmapp.data.local.AssetDao
import com.financeasserflow.pfmapp.data.local.AssetHistoryDao
import com.financeasserflow.pfmapp.data.local.PortfolioTargetDao
import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.data.model.AssetEntity
import com.financeasserflow.pfmapp.data.model.AssetHistoryEntity
import com.financeasserflow.pfmapp.data.model.AssetType
import com.financeasserflow.pfmapp.data.model.ChangeType
import com.financeasserflow.pfmapp.data.model.PortfolioTargetEntity
import com.financeasserflow.pfmapp.data.model.currentValue
import com.financeasserflow.pfmapp.data.model.isInvestmentAsset
import kotlinx.coroutines.flow.Flow

class AssetRepository(
    private val database: AppDatabase,
) {
    private val assetDao: AssetDao = database.assetDao()
    private val historyDao: AssetHistoryDao = database.assetHistoryDao()
    private val targetDao: PortfolioTargetDao = database.portfolioTargetDao()

    fun observeAssets(): Flow<List<AssetEntity>> = assetDao.observeAssets()

    fun observeAsset(assetId: Long): Flow<AssetEntity?> = assetDao.observeAsset(assetId)

    fun observeHistories(assetId: Long): Flow<List<AssetHistoryEntity>> = historyDao.observeHistories(assetId)

    fun observeAllHistories(): Flow<List<AssetHistoryEntity>> = historyDao.observeAllHistories()

    fun observeTargets(): Flow<List<PortfolioTargetEntity>> = targetDao.observeTargets()

    suspend fun getAssetOnce(assetId: Long): AssetEntity? = assetDao.getAssetOnce(assetId)

    suspend fun ensureSeedData() {
        if (targetDao.countTargets() == 0L) {
            targetDao.insertAll(*defaultTargets().toTypedArray())
        }
        if (assetDao.countAssets() == 0L) {
            defaultAssets().forEach { saveAsset(it) }
        }
    }

    suspend fun saveAsset(asset: AssetEntity) {
        database.withTransaction {
            val now = System.currentTimeMillis()
            val existing = if (asset.id == 0L) null else assetDao.getAssetOnce(asset.id)
            val normalized = asset.copy(
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
                amount = asset.currentValue(),
            )

            if (existing == null) {
                val newId = assetDao.insert(normalized)
                historyDao.insert(
                    AssetHistoryEntity(
                        assetId = newId,
                        changeType = ChangeType.CREATED,
                        previousAmount = null,
                        newAmount = normalized.currentValue(),
                        memo = normalized.memo,
                        createdAt = now,
                    ),
                )
            } else {
                val isValuationChange = existing.valuationAmount != normalized.valuationAmount
                assetDao.update(normalized)
                historyDao.insert(
                    AssetHistoryEntity(
                        assetId = normalized.id,
                        changeType = if (isValuationChange && normalized.isInvestmentAsset()) {
                            ChangeType.VALUATION_UPDATED
                        } else {
                            ChangeType.UPDATED
                        },
                        previousAmount = existing.currentValue(),
                        newAmount = normalized.currentValue(),
                        memo = normalized.memo,
                        createdAt = now,
                    ),
                )
            }
        }
    }

    suspend fun deleteAsset(assetId: Long) {
        database.withTransaction {
            val existing = assetDao.getAssetOnce(assetId) ?: return@withTransaction
            val now = System.currentTimeMillis()
            historyDao.insert(
                AssetHistoryEntity(
                    assetId = existing.id,
                    changeType = ChangeType.DELETED,
                    previousAmount = existing.currentValue(),
                    newAmount = null,
                    memo = existing.memo,
                    createdAt = now,
                ),
            )
            assetDao.delete(existing)
        }
    }

    suspend fun upsertPortfolioTarget(target: PortfolioTargetEntity) {
        targetDao.upsert(target)
    }

    private fun defaultTargets(): List<PortfolioTargetEntity> {
        return listOf(
            PortfolioTargetEntity(
                category = AssetCategory.INVESTMENT,
                targetRatio = 70.0,
                note = "투자자산 비중 경고 기준",
            ),
            PortfolioTargetEntity(
                category = AssetCategory.CASH,
                targetRatio = 20.0,
                note = "현금성 자산 기본 비중",
            ),
            PortfolioTargetEntity(
                category = AssetCategory.SAVINGS,
                targetRatio = 30.0,
                note = "예적금 권장 비중",
            ),
        )
    }

    private fun defaultAssets(): List<AssetEntity> {
        return listOf(
            AssetEntity(
                name = "토스뱅크 입출금",
                assetType = AssetType.ASSET,
                category = AssetCategory.CASH,
                amount = 1_250_000,
                memo = "생활비 통장",
            ),
            AssetEntity(
                name = "국민은행 정기예금",
                assetType = AssetType.ASSET,
                category = AssetCategory.SAVINGS,
                amount = 5_000_000,
                memo = "만기 2026년 예금",
            ),
            AssetEntity(
                name = "삼성전자 주식",
                assetType = AssetType.ASSET,
                category = AssetCategory.INVESTMENT,
                amount = 3_240_000,
                principalAmount = 3_000_000,
                valuationAmount = 3_240_000,
                memo = "평가금액 수동 갱신",
            ),
            AssetEntity(
                name = "S&P500 ETF",
                assetType = AssetType.ASSET,
                category = AssetCategory.INVESTMENT,
                amount = 1_920_000,
                principalAmount = 2_000_000,
                valuationAmount = 1_920_000,
                memo = "해외 ETF",
            ),
            AssetEntity(
                name = "학자금 대출",
                assetType = AssetType.LIABILITY,
                category = AssetCategory.LIABILITY,
                amount = 1_800_000,
                memo = "매월 상환 예정",
            ),
        )
    }
}
