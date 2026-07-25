package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class ExpenseRepository(private val db: AppDatabase) {

    val transactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()
    val savingsGoals: Flow<List<SavingsGoalEntity>> = db.savingsGoalDao().getAllGoals()
    val loans: Flow<List<LoanEntity>> = db.loanDao().getAllLoans()
    val assets: Flow<List<AssetItemEntity>> = db.assetDao().getAllAssets()
    val userProfile: Flow<UserProfileEntity?> = db.userProfileDao().getUserProfile()

    fun getWidgetsForPreset(preset: DashboardPreset): Flow<List<WidgetConfigEntity>> {
        return db.widgetDao().getWidgetsForPreset(preset)
    }

    // Calculations
    val totalIncome: Flow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }

    val totalExpenses: Flow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    val totalAssetsValue: Flow<Double> = assets.map { list ->
        list.sumOf { it.currentValue }
    }

    val totalLoansValue: Flow<Double> = loans.map { list ->
        list.sumOf { it.remainingAmount }
    }

    val netWorth: Flow<Double> = combine(
        totalIncome,
        totalExpenses,
        totalAssetsValue,
        totalLoansValue
    ) { inc, exp, ass, loa ->
        (inc - exp) + ass - loa
    }

    val cashBalance: Flow<Double> = transactions.map { list ->
        val cashInc = list.filter { it.type == TransactionType.INCOME && it.paymentMethod == PaymentMethod.CASH }.sumOf { it.amount }
        val cashExp = list.filter { it.type == TransactionType.EXPENSE && it.paymentMethod == PaymentMethod.CASH }.sumOf { it.amount }
        (cashInc - cashExp).coerceAtLeast(0.0) + 150.0
    }

    val bankBalance: Flow<Double> = combine(transactions, assets) { txList, assetList ->
        val bankAssets = assetList.filter { it.category == AssetCategory.BANK }.sumOf { it.currentValue }
        val bankInc = txList.filter { it.type == TransactionType.INCOME && (it.paymentMethod == PaymentMethod.BANK || it.paymentMethod == PaymentMethod.DEBIT_CARD) }.sumOf { it.amount }
        val bankExp = txList.filter { it.type == TransactionType.EXPENSE && (it.paymentMethod == PaymentMethod.BANK || it.paymentMethod == PaymentMethod.DEBIT_CARD) }.sumOf { it.amount }
        (bankAssets + bankInc - bankExp).coerceAtLeast(0.0)
    }

    val upiBalance: Flow<Double> = transactions.map { list ->
        val upiInc = list.filter { it.type == TransactionType.INCOME && it.paymentMethod == PaymentMethod.UPI }.sumOf { it.amount }
        val upiExp = list.filter { it.type == TransactionType.EXPENSE && it.paymentMethod == PaymentMethod.UPI }.sumOf { it.amount }
        (upiInc - upiExp).coerceAtLeast(0.0) + 420.0
    }

    // Actions
    suspend fun addTransaction(transaction: TransactionEntity): Long {
        return db.transactionDao().insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        db.transactionDao().deleteById(id)
    }

    suspend fun addSavingsGoal(goal: SavingsGoalEntity): Long {
        return db.savingsGoalDao().insertGoal(goal)
    }

    suspend fun updateGoalProgress(goal: SavingsGoalEntity, addAmount: Double) {
        val updated = goal.copy(currentAmount = (goal.currentAmount + addAmount).coerceAtMost(goal.targetAmount))
        db.savingsGoalDao().insertGoal(updated)
    }

    suspend fun addLoan(loan: LoanEntity): Long {
        return db.loanDao().insertLoan(loan)
    }

    suspend fun payLoanEmi(loan: LoanEntity, amount: Double) {
        val updated = loan.copy(remainingAmount = (loan.remainingAmount - amount).coerceAtLeast(0.0))
        db.loanDao().insertLoan(updated)
    }

    suspend fun addAsset(asset: AssetItemEntity): Long {
        return db.assetDao().insertAsset(asset)
    }

    suspend fun updateUserProfile(profile: UserProfileEntity) {
        db.userProfileDao().insertOrUpdateProfile(profile)
    }

    suspend fun updateWidget(widget: WidgetConfigEntity) {
        db.widgetDao().updateWidget(widget)
    }

    suspend fun updateWidgetsOrder(widgets: List<WidgetConfigEntity>) {
        val reordered = widgets.mapIndexed { index, w -> w.copy(positionIndex = index) }
        db.widgetDao().insertWidgets(reordered)
    }

    suspend fun addWidgetToPreset(preset: DashboardPreset, type: WidgetType) {
        val existing = db.widgetDao().getWidgetsForPresetOnce(preset)
        val newPos = existing.size
        val newWidget = WidgetConfigEntity(
            widgetType = type,
            positionIndex = newPos,
            size = WidgetSize.MEDIUM,
            style = WidgetStyle.LUMIA_TILE,
            isHidden = false,
            layoutPreset = preset
        )
        db.widgetDao().insertWidget(newWidget)
    }

    suspend fun resetPresetWidgets(preset: DashboardPreset) {
        db.widgetDao().clearPreset(preset)
        // Re-seed defaults
        val defaults = listOf(
            WidgetConfigEntity(widgetType = WidgetType.NET_WORTH, positionIndex = 0, size = WidgetSize.LARGE, style = WidgetStyle.LUMIA_TILE, layoutPreset = preset),
            WidgetConfigEntity(widgetType = WidgetType.MONTHLY_INCOME, positionIndex = 1, size = WidgetSize.SMALL, style = WidgetStyle.ROUNDED, layoutPreset = preset),
            WidgetConfigEntity(widgetType = WidgetType.MONTHLY_EXPENSE, positionIndex = 2, size = WidgetSize.SMALL, style = WidgetStyle.ROUNDED, layoutPreset = preset),
            WidgetConfigEntity(widgetType = WidgetType.BANK_BALANCE, positionIndex = 3, size = WidgetSize.MEDIUM, style = WidgetStyle.GLASS, layoutPreset = preset),
            WidgetConfigEntity(widgetType = WidgetType.EXPENSE_CATEGORIES, positionIndex = 4, size = WidgetSize.MEDIUM, style = WidgetStyle.LUMIA_TILE, layoutPreset = preset),
            WidgetConfigEntity(widgetType = WidgetType.RECENT_TRANSACTIONS, positionIndex = 5, size = WidgetSize.LARGE, style = WidgetStyle.SOLID, layoutPreset = preset)
        )
        db.widgetDao().insertWidgets(defaults)
    }

    suspend fun generateCsvExport(): String {
        val list = transactions.first()
        val sb = StringBuilder()
        sb.append("ID,Title,Amount,Type,Category,PaymentMethod,Date,Notes\n")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        for (t in list) {
            sb.append("${t.id},\"${t.title}\",${t.amount},${t.type},\"${t.category}\",${t.paymentMethod},\"${sdf.format(Date(t.dateEpochMs))}\",\"${t.notes}\"\n")
        }
        return sb.toString()
    }
}
