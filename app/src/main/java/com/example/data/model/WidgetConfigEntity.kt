package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_configs")
data class WidgetConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val widgetType: WidgetType,
    val positionIndex: Int,
    val size: WidgetSize = WidgetSize.MEDIUM,
    val style: WidgetStyle = WidgetStyle.LUMIA_TILE,
    val isHidden: Boolean = false,
    val layoutPreset: DashboardPreset = DashboardPreset.PERSONAL
)
