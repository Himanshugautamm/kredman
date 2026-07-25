package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateEpochMs DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface SavingsGoalDao {
    @Query("SELECT * FROM savings_goals")
    fun getAllGoals(): Flow<List<SavingsGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoalEntity): Long

    @Delete
    suspend fun deleteGoal(goal: SavingsGoalEntity)
}

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)
}

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets")
    fun getAllAssets(): Flow<List<AssetItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetItemEntity): Long

    @Delete
    suspend fun deleteAsset(asset: AssetItemEntity)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}

@Dao
interface WidgetDao {
    @Query("SELECT * FROM widget_configs WHERE layoutPreset = :preset ORDER BY positionIndex ASC")
    fun getWidgetsForPreset(preset: DashboardPreset): Flow<List<WidgetConfigEntity>>

    @Query("SELECT * FROM widget_configs WHERE layoutPreset = :preset ORDER BY positionIndex ASC")
    suspend fun getWidgetsForPresetOnce(preset: DashboardPreset): List<WidgetConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: WidgetConfigEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgets(widgets: List<WidgetConfigEntity>)

    @Update
    suspend fun updateWidget(widget: WidgetConfigEntity)

    @Delete
    suspend fun deleteWidget(widget: WidgetConfigEntity)

    @Query("DELETE FROM widget_configs WHERE layoutPreset = :preset")
    suspend fun clearPreset(preset: DashboardPreset)
}
