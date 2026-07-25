package com.example.data.db

import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SampleDataInitializer {

    suspend fun checkAndSeedInitialData(db: AppDatabase) = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileOnce()
        if (profile == null) {
            // Seed Profile
            db.userProfileDao().insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    name = "Alex Vance",
                    age = 28,
                    gender = "Male",
                    country = "United States",
                    currencyCode = "USD",
                    currencySymbol = "$",
                    salaryDate = 1,
                    monthlyIncome = 6800.0,
                    financialGoal = "Grow Net Worth & Save $20,000",
                    themeStyle = ThemeStyle.NOTHING,
                    activePreset = DashboardPreset.PERSONAL,
                    isPinEnabled = false,
                    isFirstLaunchCompleted = true
                )
            )

            // Seed Transactions
            val now = System.currentTimeMillis()
            val day = 86400000L

            val transactions = listOf(
                TransactionEntity(
                    title = "Monthly Tech Salary",
                    amount = 6800.0,
                    category = "Salary",
                    type = TransactionType.INCOME,
                    paymentMethod = PaymentMethod.BANK,
                    dateEpochMs = now - (2 * day),
                    notes = "Direct Deposit - Acme Corp"
                ),
                TransactionEntity(
                    title = "Freelance UI Design",
                    amount = 1200.0,
                    category = "Freelance",
                    type = TransactionType.INCOME,
                    paymentMethod = PaymentMethod.UPI,
                    dateEpochMs = now - (5 * day),
                    notes = "Client milestone payment"
                ),
                TransactionEntity(
                    title = "Apple Store - MacBook M3",
                    amount = 1999.0,
                    category = "Electronics",
                    type = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.CREDIT_CARD,
                    dateEpochMs = now - (1 * day),
                    notes = "Work computer upgrade"
                ),
                TransactionEntity(
                    title = "Organic Grocery Market",
                    amount = 142.50,
                    category = "Food & Grocery",
                    type = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.DEBIT_CARD,
                    dateEpochMs = now - (3 * day)
                ),
                TransactionEntity(
                    title = "Uber Ride to Airport",
                    amount = 45.20,
                    category = "Transport",
                    type = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.UPI,
                    dateEpochMs = now - (4 * day)
                ),
                TransactionEntity(
                    title = "Starbucks Reserve Coffee",
                    amount = 12.80,
                    category = "Dining",
                    type = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.WALLET,
                    dateEpochMs = now - (6 * day)
                ),
                TransactionEntity(
                    title = "Spotify & Netflix Premium",
                    amount = 28.99,
                    category = "Subscriptions",
                    type = TransactionType.EXPENSE,
                    paymentMethod = PaymentMethod.CREDIT_CARD,
                    dateEpochMs = now - (7 * day),
                    isRecurring = true
                )
            )
            for (t in transactions) {
                db.transactionDao().insertTransaction(t)
            }

            // Seed Savings Goals
            val goals = listOf(
                SavingsGoalEntity(
                    title = "Tesla Model 3 / EV",
                    category = GoalCategory.CAR,
                    targetAmount = 35000.0,
                    currentAmount = 18500.0,
                    targetDateEpochMs = now + (180 * day)
                ),
                SavingsGoalEntity(
                    title = "Emergency Fund (6 Months)",
                    category = GoalCategory.EMERGENCY_FUND,
                    targetAmount = 25000.0,
                    currentAmount = 21000.0,
                    targetDateEpochMs = now + (90 * day)
                ),
                SavingsGoalEntity(
                    title = "Tokyo & Kyoto Vacation",
                    category = GoalCategory.VACATION,
                    targetAmount = 6000.0,
                    currentAmount = 4200.0,
                    targetDateEpochMs = now + (120 * day)
                )
            )
            for (g in goals) {
                db.savingsGoalDao().insertGoal(g)
            }

            // Seed Loans
            val loans = listOf(
                LoanEntity(
                    title = "BMW i4 Vehicle Loan",
                    type = LoanType.CAR,
                    totalAmount = 42000.0,
                    remainingAmount = 18400.0,
                    interestRate = 4.5,
                    monthlyEmi = 620.0,
                    dueDateEpochMs = now + (15 * day),
                    lenderOrBorrower = "Chase Auto Finance"
                ),
                LoanEntity(
                    title = "Premium Credit Card",
                    type = LoanType.CREDIT_CARD,
                    totalAmount = 5000.0,
                    remainingAmount = 1250.0,
                    interestRate = 18.0,
                    monthlyEmi = 300.0,
                    dueDateEpochMs = now + (8 * day),
                    lenderOrBorrower = "AMEX Platinum"
                )
            )
            for (l in loans) {
                db.loanDao().insertLoan(l)
            }

            // Seed Assets
            val assets = listOf(
                AssetItemEntity(
                    name = "Primary Checking",
                    category = AssetCategory.BANK,
                    currentValue = 14250.0,
                    investedValue = 14250.0,
                    symbolCode = "CHASE"
                ),
                AssetItemEntity(
                    name = "High Yield Savings",
                    category = AssetCategory.BANK,
                    currentValue = 28500.0,
                    investedValue = 28000.0,
                    symbolCode = "HYSA"
                ),
                AssetItemEntity(
                    name = "S&P 500 ETF (VOO)",
                    category = AssetCategory.STOCKS,
                    currentValue = 38400.0,
                    investedValue = 29000.0,
                    symbolCode = "VOO"
                ),
                AssetItemEntity(
                    name = "Bitcoin (BTC)",
                    category = AssetCategory.CRYPTO,
                    currentValue = 12800.0,
                    investedValue = 8500.0,
                    symbolCode = "BTC"
                ),
                AssetItemEntity(
                    name = "Physical Gold Bullion",
                    category = AssetCategory.GOLD,
                    currentValue = 9200.0,
                    investedValue = 7200.0,
                    symbolCode = "XAU"
                )
            )
            for (a in assets) {
                db.assetDao().insertAsset(a)
            }

            // Seed Default Widget Layouts for all Presets
            seedDefaultWidgetConfigs(db)
        }
    }

    private suspend fun seedDefaultWidgetConfigs(db: AppDatabase) {
        val presets = listOf(
            DashboardPreset.PERSONAL,
            DashboardPreset.BUSINESS,
            DashboardPreset.TRAVEL,
            DashboardPreset.MINIMAL,
            DashboardPreset.STUDENT
        )

        for (preset in presets) {
            val widgets = when (preset) {
                DashboardPreset.PERSONAL -> listOf(
                    WidgetConfigEntity(widgetType = WidgetType.NET_WORTH, positionIndex = 0, size = WidgetSize.LARGE, style = WidgetStyle.LUMIA_TILE),
                    WidgetConfigEntity(widgetType = WidgetType.MONTHLY_INCOME, positionIndex = 1, size = WidgetSize.SMALL, style = WidgetStyle.ROUNDED),
                    WidgetConfigEntity(widgetType = WidgetType.MONTHLY_EXPENSE, positionIndex = 2, size = WidgetSize.SMALL, style = WidgetStyle.ROUNDED),
                    WidgetConfigEntity(widgetType = WidgetType.BANK_BALANCE, positionIndex = 3, size = WidgetSize.MEDIUM, style = WidgetStyle.GLASS),
                    WidgetConfigEntity(widgetType = WidgetType.EXPENSE_CATEGORIES, positionIndex = 4, size = WidgetSize.MEDIUM, style = WidgetStyle.LUMIA_TILE),
                    WidgetConfigEntity(widgetType = WidgetType.SAVINGS_GOALS, positionIndex = 5, size = WidgetSize.MEDIUM, style = WidgetStyle.ROUNDED),
                    WidgetConfigEntity(widgetType = WidgetType.RECENT_TRANSACTIONS, positionIndex = 6, size = WidgetSize.LARGE, style = WidgetStyle.SOLID),
                    WidgetConfigEntity(widgetType = WidgetType.AI_HEALTH_SCORE, positionIndex = 7, size = WidgetSize.MEDIUM, style = WidgetStyle.GLASS)
                )
                DashboardPreset.BUSINESS -> listOf(
                    WidgetConfigEntity(widgetType = WidgetType.NET_WORTH, positionIndex = 0, size = WidgetSize.LARGE, style = WidgetStyle.SOLID),
                    WidgetConfigEntity(widgetType = WidgetType.MONTHLY_INCOME, positionIndex = 1, size = WidgetSize.MEDIUM, style = WidgetStyle.LUMIA_TILE),
                    WidgetConfigEntity(widgetType = WidgetType.MONTHLY_EXPENSE, positionIndex = 2, size = WidgetSize.MEDIUM, style = WidgetStyle.LUMIA_TILE),
                    WidgetConfigEntity(widgetType = WidgetType.ASSET_PORTFOLIO, positionIndex = 3, size = WidgetSize.LARGE, style = WidgetStyle.ROUNDED),
                    WidgetConfigEntity(widgetType = WidgetType.LOANS_SUMMARY, positionIndex = 4, size = WidgetSize.MEDIUM, style = WidgetStyle.GLASS)
                )
                DashboardPreset.TRAVEL -> listOf(
                    WidgetConfigEntity(widgetType = WidgetType.CASH_BALANCE, positionIndex = 0, size = WidgetSize.MEDIUM, style = WidgetStyle.ROUNDED),
                    WidgetConfigEntity(widgetType = WidgetType.UPI_BALANCE, positionIndex = 1, size = WidgetSize.MEDIUM, style = WidgetStyle.ROUNDED),
                    WidgetConfigEntity(widgetType = WidgetType.MONTHLY_EXPENSE, positionIndex = 2, size = WidgetSize.LARGE, style = WidgetStyle.LUMIA_TILE),
                    WidgetConfigEntity(widgetType = WidgetType.RECENT_TRANSACTIONS, positionIndex = 3, size = WidgetSize.LARGE, style = WidgetStyle.SOLID)
                )
                DashboardPreset.MINIMAL -> listOf(
                    WidgetConfigEntity(widgetType = WidgetType.NET_WORTH, positionIndex = 0, size = WidgetSize.LARGE, style = WidgetStyle.TRANSPARENT),
                    WidgetConfigEntity(widgetType = WidgetType.BUDGET_GAUGE, positionIndex = 1, size = WidgetSize.MEDIUM, style = WidgetStyle.ROUNDED),
                    WidgetConfigEntity(widgetType = WidgetType.RECENT_TRANSACTIONS, positionIndex = 2, size = WidgetSize.LARGE, style = WidgetStyle.TRANSPARENT)
                )
                DashboardPreset.STUDENT -> listOf(
                    WidgetConfigEntity(widgetType = WidgetType.BUDGET_GAUGE, positionIndex = 0, size = WidgetSize.MEDIUM, style = WidgetStyle.ROUNDED),
                    WidgetConfigEntity(widgetType = WidgetType.CASH_BALANCE, positionIndex = 1, size = WidgetSize.SMALL, style = WidgetStyle.GLASS),
                    WidgetConfigEntity(widgetType = WidgetType.SAVINGS_GOALS, positionIndex = 2, size = WidgetSize.MEDIUM, style = WidgetStyle.LUMIA_TILE),
                    WidgetConfigEntity(widgetType = WidgetType.RECENT_TRANSACTIONS, positionIndex = 3, size = WidgetSize.LARGE, style = WidgetStyle.SOLID)
                )
            }
            db.widgetDao().insertWidgets(widgets.map { it.copy(layoutPreset = preset) })
        }
    }
}
