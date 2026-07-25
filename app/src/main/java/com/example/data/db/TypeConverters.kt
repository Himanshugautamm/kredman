package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.*

class AppTypeConverters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = try {
        TransactionType.valueOf(value)
    } catch (e: Exception) {
        TransactionType.EXPENSE
    }

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String = method.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = try {
        PaymentMethod.valueOf(value)
    } catch (e: Exception) {
        if (value.contains("CREDIT", true)) PaymentMethod.CREDIT_CARD else PaymentMethod.CASH
    }

    @TypeConverter
    fun fromThemeStyle(style: ThemeStyle): String = style.name

    @TypeConverter
    fun toThemeStyle(value: String): ThemeStyle = try {
        ThemeStyle.valueOf(value)
    } catch (e: Exception) {
        ThemeStyle.NOTHING
    }

    @TypeConverter
    fun fromWidgetType(type: WidgetType): String = type.name

    @TypeConverter
    fun toWidgetType(value: String): WidgetType = try {
        WidgetType.valueOf(value)
    } catch (e: Exception) {
        WidgetType.NET_WORTH
    }

    @TypeConverter
    fun fromWidgetSize(size: WidgetSize): String = size.name

    @TypeConverter
    fun toWidgetSize(value: String): WidgetSize = try {
        WidgetSize.valueOf(value)
    } catch (e: Exception) {
        WidgetSize.MEDIUM
    }

    @TypeConverter
    fun fromWidgetStyle(style: WidgetStyle): String = style.name

    @TypeConverter
    fun toWidgetStyle(value: String): WidgetStyle = try {
        WidgetStyle.valueOf(value)
    } catch (e: Exception) {
        WidgetStyle.LUMIA_TILE
    }

    @TypeConverter
    fun fromDashboardPreset(preset: DashboardPreset): String = preset.name

    @TypeConverter
    fun toDashboardPreset(value: String): DashboardPreset = try {
        DashboardPreset.valueOf(value)
    } catch (e: Exception) {
        DashboardPreset.PERSONAL
    }

    @TypeConverter
    fun fromGoalCategory(cat: GoalCategory): String = cat.name

    @TypeConverter
    fun toGoalCategory(value: String): GoalCategory = try {
        GoalCategory.valueOf(value)
    } catch (e: Exception) {
        GoalCategory.CUSTOM
    }

    @TypeConverter
    fun fromLoanType(type: LoanType): String = type.name

    @TypeConverter
    fun toLoanType(value: String): LoanType = try {
        LoanType.valueOf(value)
    } catch (e: Exception) {
        LoanType.PERSONAL
    }

    @TypeConverter
    fun fromAssetCategory(cat: AssetCategory): String = cat.name

    @TypeConverter
    fun toAssetCategory(value: String): AssetCategory = try {
        AssetCategory.valueOf(value)
    } catch (e: Exception) {
        AssetCategory.CUSTOM
    }
}
