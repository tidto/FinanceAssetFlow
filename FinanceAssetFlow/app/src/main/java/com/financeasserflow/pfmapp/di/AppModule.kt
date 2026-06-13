package com.financeasserflow.pfmapp.di

import android.content.Context
import com.financeasserflow.pfmapp.data.local.AppDatabase
import com.financeasserflow.pfmapp.data.repository.AssetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideAssetRepository(database: AppDatabase): AssetRepository =
        AssetRepository(database)
}
