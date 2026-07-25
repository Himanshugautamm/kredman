package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: AssetCategory,
    val currentValue: Double,
    val investedValue: Double = 0.0,
    val symbolCode: String = ""
)
