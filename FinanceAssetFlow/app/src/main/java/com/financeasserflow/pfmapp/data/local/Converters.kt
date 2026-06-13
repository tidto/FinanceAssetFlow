package com.financeasserflow.pfmapp.data.local

import androidx.room.TypeConverter
import com.financeasserflow.pfmapp.data.model.AssetCategory
import com.financeasserflow.pfmapp.data.model.AssetType
import com.financeasserflow.pfmapp.data.model.ChangeType

class Converters {
    @TypeConverter
    fun fromAssetType(value: AssetType): String = value.name

    @TypeConverter
    fun toAssetType(value: String): AssetType = AssetType.valueOf(value)

    @TypeConverter
    fun fromAssetCategory(value: AssetCategory): String = value.name

    @TypeConverter
    fun toAssetCategory(value: String): AssetCategory = AssetCategory.valueOf(value)

    @TypeConverter
    fun fromChangeType(value: ChangeType): String = value.name

    @TypeConverter
    fun toChangeType(value: String): ChangeType = ChangeType.valueOf(value)
}

