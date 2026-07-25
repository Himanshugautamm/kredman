package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "User",
    val age: Int = 25,
    val gender: String = "Male",
    val country: String = "United States",
    val currencyCode: String = "USD",
    val currencySymbol: String = "$",
    val salaryDate: Int = 1,
    val monthlyIncome: Double = 5000.0,
    val financialGoal: String = "Save $10,000 & Track Expenses",
    val themeStyle: ThemeStyle = ThemeStyle.NOTHING,
    val activePreset: DashboardPreset = DashboardPreset.PERSONAL,
    val isPinEnabled: Boolean = false,
    val pinCode: String = "",
    val isBiometricEnabled: Boolean = false,
    val isFirstLaunchCompleted: Boolean = false,
    val profileImageUri: String? = null
)
