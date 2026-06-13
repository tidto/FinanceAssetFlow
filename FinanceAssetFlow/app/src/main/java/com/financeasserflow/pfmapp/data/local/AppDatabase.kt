package com.financeasserflow.pfmapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.financeasserflow.pfmapp.data.model.AssetEntity
import com.financeasserflow.pfmapp.data.model.AssetHistoryEntity
import com.financeasserflow.pfmapp.data.model.PortfolioTargetEntity

@Database(
    entities = [
        AssetEntity::class,
        AssetHistoryEntity::class,
        PortfolioTargetEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun assetHistoryDao(): AssetHistoryDao
    abstract fun portfolioTargetDao(): PortfolioTargetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_asset_flow.db",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

