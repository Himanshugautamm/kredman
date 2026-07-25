package com.example.data.model

enum class TransactionType {
    INCOME, EXPENSE
}

enum class PaymentMethod(val label: String) {
    CASH("Cash"),
    UPI("UPI"),
    BANK("Bank Account"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    WALLET("Digital Wallet")
}

enum class ThemeStyle(val label: String) {
    NOTHING("Nothing Style"),
    LUMIA("Lumia Inspired"),
    GLASS("Glassmorphism"),
    AMOLED("AMOLED Black"),
    MATERIAL_YOU("Material You"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class WidgetType(val defaultTitle: String) {
    NET_WORTH("Net Worth"),
    CASH_BALANCE("Cash Balance"),
    BANK_BALANCE("Bank Balance"),
    UPI_BALANCE("UPI Balance"),
    MONTHLY_INCOME("Monthly Income"),
    MONTHLY_EXPENSE("Monthly Expense"),
    SAVINGS_GOALS("Savings Goals"),
    LOANS_SUMMARY("Loans & EMI"),
    RECENT_TRANSACTIONS("Recent Activity"),
    EXPENSE_CATEGORIES("Category Breakdown"),
    ASSET_PORTFOLIO("Asset Portfolio"),
    BUDGET_GAUGE("Budget Remaining"),
    AI_HEALTH_SCORE("AI Health Score"),
    QUICK_ACTIONS("Quick Actions")
}

enum class WidgetSize {
    SMALL, MEDIUM, LARGE
}

enum class WidgetStyle {
    SOLID, TRANSPARENT, GLASS, ROUNDED, LUMIA_TILE
}

enum class DashboardPreset(val label: String) {
    PERSONAL("Personal"),
    BUSINESS("Business"),
    TRAVEL("Travel"),
    MINIMAL("Minimal"),
    STUDENT("Student")
}

enum class GoalCategory(val label: String) {
    HOUSE("House"),
    BIKE("Bike"),
    CAR("Car"),
    VACATION("Vacation"),
    EMERGENCY_FUND("Emergency Fund"),
    WEDDING("Wedding"),
    EDUCATION("Education"),
    RETIREMENT("Retirement"),
    CUSTOM("Custom")
}

enum class LoanType(val label: String) {
    PERSONAL("Personal Loan"),
    BIKE("Bike Loan"),
    CAR("Car Loan"),
    HOME("Home Loan"),
    CREDIT_CARD("Credit Card Debt"),
    BORROWED_FRIEND("Borrowed"),
    LENT_FRIEND("Lent Out")
}

enum class AssetCategory(val label: String) {
    CASH("Cash"),
    BANK("Bank Account"),
    GOLD("Gold"),
    SILVER("Silver"),
    STOCKS("Stocks"),
    MUTUAL_FUNDS("Mutual Funds"),
    CRYPTO("Crypto"),
    PROPERTY("Property"),
    VEHICLES("Vehicles"),
    PF_FD("PF / FD"),
    CUSTOM("Custom Asset")
}
