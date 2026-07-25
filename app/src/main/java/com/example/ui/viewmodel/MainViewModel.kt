package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiFinancialReport
import com.example.data.ai.GeminiFinancialService
import com.example.data.ai.ParsedOcrReceipt
import com.example.data.db.AppDatabase
import com.example.data.db.SampleDataInitializer
import com.example.data.model.*
import com.example.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
    val searchQuery: String = "",
    val activePreset: DashboardPreset = DashboardPreset.PERSONAL,
    val isCustomizeMode: Boolean = false,
    val isLocked: Boolean = false,
    val isAiLoading: Boolean = false,
    val aiReport: AiFinancialReport? = null,
    val aiAnswerText: String? = null,
    val ocrScanResult: ParsedOcrReceipt? = null,
    val csvExportContent: String? = null,
    val toastMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = ExpenseRepository(db)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            SampleDataInitializer.checkAndSeedInitialData(db)
            repository.userProfile.collect { profile ->
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            activePreset = profile.activePreset,
                            isLocked = profile.isPinEnabled && profile.pinCode.isNotEmpty()
                        )
                    }
                }
            }
        }
    }

    // Repository Flows
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<TransactionEntity>> = combine(
        repository.transactions,
        _uiState.map { it.searchQuery }
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true) ||
            it.notes.contains(query, ignoreCase = true) ||
            it.paymentMethod.name.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savingsGoals: StateFlow<List<SavingsGoalEntity>> = repository.savingsGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<LoanEntity>> = repository.loans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assets: StateFlow<List<AssetItemEntity>> = repository.assets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val netWorth: StateFlow<Double> = repository.netWorth
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalIncome: StateFlow<Double> = repository.totalIncome
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenses: StateFlow<Double> = repository.totalExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cashBalance: StateFlow<Double> = repository.cashBalance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val bankBalance: StateFlow<Double> = repository.bankBalance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val upiBalance: StateFlow<Double> = repository.upiBalance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val activeWidgets: StateFlow<List<WidgetConfigEntity>> = _uiState
        .flatMapLatest { state ->
            repository.getWidgetsForPreset(state.activePreset)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setActivePreset(preset: DashboardPreset) {
        _uiState.update { it.copy(activePreset = preset) }
        viewModelScope.launch {
            userProfile.value?.let { profile ->
                repository.updateUserProfile(profile.copy(activePreset = preset))
            }
        }
    }

    fun toggleCustomizeMode() {
        _uiState.update { it.copy(isCustomizeMode = !it.isCustomizeMode) }
    }

    fun setCustomizeMode(enabled: Boolean) {
        _uiState.update { it.copy(isCustomizeMode = enabled) }
    }

    fun unlockWithPin(pin: String): Boolean {
        val currentPin = userProfile.value?.pinCode ?: ""
        if (pin == currentPin || currentPin.isEmpty()) {
            _uiState.update { it.copy(isLocked = false) }
            return true
        }
        return false
    }

    fun lockApp() {
        _uiState.update { it.copy(isLocked = true) }
    }

    fun addTransaction(
        title: String,
        amount: Double,
        category: String,
        type: TransactionType,
        paymentMethod: PaymentMethod,
        notes: String = "",
        isRecurring: Boolean = false
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    type = type,
                    paymentMethod = paymentMethod,
                    notes = notes,
                    isRecurring = isRecurring
                )
            )
            showToast("Added $title")
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            showToast("Transaction deleted")
        }
    }

    fun addSavingsGoal(title: String, category: GoalCategory, targetAmount: Double) {
        viewModelScope.launch {
            repository.addSavingsGoal(
                SavingsGoalEntity(
                    title = title,
                    category = category,
                    targetAmount = targetAmount,
                    currentAmount = 0.0,
                    targetDateEpochMs = System.currentTimeMillis() + (90 * 86400000L)
                )
            )
            showToast("Created savings goal for $title")
        }
    }

    fun addDepositToGoal(goal: SavingsGoalEntity, amount: Double) {
        viewModelScope.launch {
            repository.updateGoalProgress(goal, amount)
            showToast("Added $${String.format("%.2f", amount)} to ${goal.title}")
        }
    }

    fun addLoan(title: String, type: LoanType, totalAmount: Double, monthlyEmi: Double, lender: String) {
        viewModelScope.launch {
            repository.addLoan(
                LoanEntity(
                    title = title,
                    type = type,
                    totalAmount = totalAmount,
                    remainingAmount = totalAmount,
                    interestRate = 5.0,
                    monthlyEmi = monthlyEmi,
                    dueDateEpochMs = System.currentTimeMillis() + (30 * 86400000L),
                    lenderOrBorrower = lender
                )
            )
            showToast("Loan $title recorded")
        }
    }

    fun payLoanEmi(loan: LoanEntity) {
        viewModelScope.launch {
            repository.payLoanEmi(loan, loan.monthlyEmi)
            showToast("Paid EMI $${String.format("%.2f", loan.monthlyEmi)} for ${loan.title}")
        }
    }

    fun addAsset(name: String, category: AssetCategory, value: Double, code: String) {
        viewModelScope.launch {
            repository.addAsset(
                AssetItemEntity(
                    name = name,
                    category = category,
                    currentValue = value,
                    investedValue = value,
                    symbolCode = code
                )
            )
            showToast("Asset $name added")
        }
    }

    fun updateThemeStyle(style: ThemeStyle) {
        viewModelScope.launch {
            userProfile.value?.let { profile ->
                repository.updateUserProfile(profile.copy(themeStyle = style))
                showToast("Theme changed to ${style.label}")
            }
        }
    }

    fun updateCurrency(currencyCode: String, symbol: String) {
        viewModelScope.launch {
            userProfile.value?.let { profile ->
                repository.updateUserProfile(profile.copy(currencyCode = currencyCode, currencySymbol = symbol))
                showToast("Currency set to $currencyCode ($symbol)")
            }
        }
    }

    fun saveUserProfile(
        name: String,
        age: Int,
        gender: String,
        country: String,
        currencyCode: String,
        currencySymbol: String,
        salaryDate: Int,
        monthlyIncome: Double,
        financialGoal: String
    ) {
        viewModelScope.launch {
            val existing = userProfile.value ?: UserProfileEntity()
            val updated = existing.copy(
                name = name,
                age = age,
                gender = gender,
                country = country,
                currencyCode = currencyCode,
                currencySymbol = currencySymbol,
                salaryDate = salaryDate,
                monthlyIncome = monthlyIncome,
                financialGoal = financialGoal,
                isFirstLaunchCompleted = true
            )
            repository.updateUserProfile(updated)
            showToast("Profile saved!")
        }
    }

    fun updateWidgetStyle(widget: WidgetConfigEntity, newStyle: WidgetStyle) {
        viewModelScope.launch {
            repository.updateWidget(widget.copy(style = newStyle))
        }
    }

    fun updateWidgetSize(widget: WidgetConfigEntity, newSize: WidgetSize) {
        viewModelScope.launch {
            repository.updateWidget(widget.copy(size = newSize))
        }
    }

    fun toggleWidgetVisibility(widget: WidgetConfigEntity) {
        viewModelScope.launch {
            repository.updateWidget(widget.copy(isHidden = !widget.isHidden))
        }
    }

    fun reorderWidgets(widgets: List<WidgetConfigEntity>) {
        viewModelScope.launch {
            repository.updateWidgetsOrder(widgets)
        }
    }

    fun addWidgetToPreset(type: WidgetType) {
        viewModelScope.launch {
            repository.addWidgetToPreset(_uiState.value.activePreset, type)
            showToast("Added ${type.defaultTitle} widget")
        }
    }

    fun resetActivePresetLayout() {
        viewModelScope.launch {
            repository.resetPresetWidgets(_uiState.value.activePreset)
            showToast("Preset layout reset to default")
        }
    }

    fun runAiAnalysis() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            val inc = totalIncome.value
            val exp = totalExpenses.value
            val nw = netWorth.value
            val txList = transactions.value
            val catMap = txList.filter { it.type == TransactionType.EXPENSE }.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val report = GeminiFinancialService.getFinancialAdvice(inc, exp, nw, catMap)
            _uiState.update { it.copy(isAiLoading = false, aiReport = report) }
        }
    }

    fun askAiAssistant(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            val txSummary = "${transactions.value.size} transactions logged. Top spend: Electronics, Food."
            val answer = GeminiFinancialService.answerFinancialQuery(query, txSummary)
            _uiState.update { it.copy(isAiLoading = false, aiAnswerText = answer) }
        }
    }

    fun scanReceiptWithAi(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            val parsed = GeminiFinancialService.parseReceiptImage(bitmap)
            _uiState.update { it.copy(isAiLoading = false, ocrScanResult = parsed) }
            showToast("Scanned: ${parsed.title} ($${parsed.amount})")
        }
    }

    fun parseVoicePrompt(prompt: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            val parsed = GeminiFinancialService.parseNaturalLanguageVoice(prompt)
            _uiState.update { it.copy(isAiLoading = false, ocrScanResult = parsed) }
            showToast("Parsed: ${parsed.title}")
        }
    }

    fun clearOcrResult() {
        _uiState.update { it.copy(ocrScanResult = null) }
    }

    fun exportDataCsv() {
        viewModelScope.launch {
            val csv = repository.generateCsvExport()
            _uiState.update { it.copy(csvExportContent = csv) }
            showToast("CSV Export generated successfully!")
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }
}
