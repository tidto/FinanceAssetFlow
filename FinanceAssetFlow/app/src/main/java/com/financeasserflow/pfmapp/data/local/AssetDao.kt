package com.financeasserflow.pfmapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.data.model.AssetEntity
import com.financeasserflow.pfmapp.data.model.AssetHistoryEntity
import com.financeasserflow.pfmapp.data.model.AssetType
import com.financeasserflow.pfmapp.data.model.ChangeType
import com.financeasserflow.pfmapp.data.model.PortfolioTargetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY updatedAt DESC")
    fun observeAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :assetId LIMIT 1")
    fun observeAsset(assetId: Long): Flow<AssetEntity?>

    @Query("SELECT * FROM assets WHERE id = :assetId LIMIT 1")
    suspend fun getAssetOnce(assetId: Long): AssetEntity?

    @Query("SELECT COUNT(*) FROM assets")
    suspend fun countAssets(): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(asset: AssetEntity): Long

    @Update
    suspend fun update(asset: AssetEntity)

    @Delete
    suspend fun delete(asset: AssetEntity)

    @Query("SELECT * FROM assets WHERE assetType = :assetType ORDER BY updatedAt DESC")
    fun observeAssetsByType(assetType: AssetType): Flow<List<AssetEntity>>
}

@Dao
interface AssetHistoryDao {
    @Query("SELECT * FROM asset_histories WHERE assetId = :assetId ORDER BY createdAt DESC")
    fun observeHistories(assetId: Long): Flow<List<AssetHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(history: AssetHistoryEntity): Long

    @Query("DELETE FROM asset_histories WHERE assetId = :assetId")
    suspend fun deleteByAssetId(assetId: Long)
}

@Dao
interface PortfolioTargetDao {
    @Query("SELECT * FROM portfolio_targets ORDER BY category ASC")
    fun observeTargets(): Flow<List<PortfolioTargetEntity>>

    @Query("SELECT * FROM portfolio_targets WHERE category = :category LIMIT 1")
    suspend fun getTargetOnce(category: AssetCategory): PortfolioTargetEntity?

    @Query("SELECT COUNT(*) FROM portfolio_targets")
    suspend fun countTargets(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(target: PortfolioTargetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg targets: PortfolioTargetEntity)
}
