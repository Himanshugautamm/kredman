package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.model.ThemeStyle
import com.example.ui.screens.*
import com.example.ui.theme.ExpenseOSTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val activeWidgets by viewModel.activeWidgets.collectAsStateWithLifecycle()
            val transactions by viewModel.transactions.collectAsStateWithLifecycle()
            val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
            val loans by viewModel.loans.collectAsStateWithLifecycle()
            val assets by viewModel.assets.collectAsStateWithLifecycle()
            val netWorth by viewModel.netWorth.collectAsStateWithLifecycle()
            val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
            val totalExpenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
            val cashBalance by viewModel.cashBalance.collectAsStateWithLifecycle()
            val bankBalance by viewModel.bankBalance.collectAsStateWithLifecycle()
            val upiBalance by viewModel.upiBalance.collectAsStateWithLifecycle()

            val navController = rememberNavController()

            // Toast feedback
            LaunchedEffect(uiState.toastMessage) {
                uiState.toastMessage?.let { msg ->
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    viewModel.clearToast()
                }
            }

            val currentTheme = userProfile?.themeStyle ?: ThemeStyle.NOTHING

            ExpenseOSTheme(style = currentTheme) {
                if (uiState.isLocked) {
                    SecurityLockScreen(
                        onUnlockWithPin = { pin ->
                            viewModel.unlockWithPin(pin)
                        }
                    )
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    val isCompleted = userProfile?.isFirstLaunchCompleted ?: false
                                    if (isCompleted) {
                                        navController.navigate("dashboard") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("onboarding") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        composable("onboarding") {
                            OnboardingScreen(
                                onOnboardingComplete = { name, age, gender, country, code, symbol, salaryDate, income, goal ->
                                    viewModel.saveUserProfile(
                                        name = name,
                                        age = age,
                                        gender = gender,
                                        country = country,
                                        currencyCode = code,
                                        currencySymbol = symbol,
                                        salaryDate = salaryDate,
                                        monthlyIncome = income,
                                        financialGoal = goal
                                    )
                                    navController.navigate("dashboard") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("dashboard") {
                            DashboardScreen(
                                userProfile = userProfile,
                                activeWidgets = activeWidgets,
                                netWorth = netWorth,
                                totalIncome = totalIncome,
                                totalExpenses = totalExpenses,
                                cashBalance = cashBalance,
                                bankBalance = bankBalance,
                                upiBalance = upiBalance,
                                transactions = transactions,
                                savingsGoals = savingsGoals,
                                loans = loans,
                                assets = assets,
                                isCustomizeMode = uiState.isCustomizeMode,
                                activePreset = uiState.activePreset,
                                searchQuery = uiState.searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onPresetChange = { viewModel.setActivePreset(it) },
                                onToggleCustomizeMode = { viewModel.toggleCustomizeMode() },
                                onWidgetResize = { w ->
                                    val newSize = when (w.size) {
                                        com.example.data.model.WidgetSize.SMALL -> com.example.data.model.WidgetSize.MEDIUM
                                        com.example.data.model.WidgetSize.MEDIUM -> com.example.data.model.WidgetSize.LARGE
                                        com.example.data.model.WidgetSize.LARGE -> com.example.data.model.WidgetSize.SMALL
                                    }
                                    viewModel.updateWidgetSize(w, newSize)
                                },
                                onWidgetStyleChange = { w, s -> viewModel.updateWidgetStyle(w, s) },
                                onWidgetToggleHide = { w -> viewModel.toggleWidgetVisibility(w) },
                                onMoveWidgetUp = { idx ->
                                    val mutable = activeWidgets.toMutableList()
                                    if (idx > 0) {
                                        val item = mutable.removeAt(idx)
                                        mutable.add(idx - 1, item)
                                        viewModel.reorderWidgets(mutable)
                                    }
                                },
                                onMoveWidgetDown = { idx ->
                                    val mutable = activeWidgets.toMutableList()
                                    if (idx < mutable.size - 1) {
                                        val item = mutable.removeAt(idx)
                                        mutable.add(idx + 1, item)
                                        viewModel.reorderWidgets(mutable)
                                    }
                                },
                                onNavigateToAddTransaction = { navController.navigate("add_transaction") },
                                onNavigateToAnalytics = { navController.navigate("analytics") },
                                onNavigateToSavings = { navController.navigate("savings") },
                                onNavigateToLoans = { navController.navigate("loans") },
                                onNavigateToAssets = { navController.navigate("assets") },
                                onNavigateToAi = { navController.navigate("ai") },
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }

                        composable("add_transaction") {
                            AddTransactionScreen(
                                currencySymbol = userProfile?.currencySymbol ?: "$",
                                ocrResult = uiState.ocrScanResult,
                                isAiLoading = uiState.isAiLoading,
                                onScanReceiptRequested = { bitmap -> viewModel.scanReceiptWithAi(bitmap) },
                                onVoicePromptRequested = { prompt -> viewModel.parseVoicePrompt(prompt) },
                                onSaveTransaction = { title, amount, category, type, pm, notes, isRec ->
                                    viewModel.addTransaction(title, amount, category, type, pm, notes, isRec)
                                    viewModel.clearOcrResult()
                                },
                                onNavigateBack = {
                                    viewModel.clearOcrResult()
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("analytics") {
                            AnalyticsScreen(
                                currencySymbol = userProfile?.currencySymbol ?: "$",
                                netWorth = netWorth,
                                totalIncome = totalIncome,
                                totalExpenses = totalExpenses,
                                transactions = transactions,
                                aiReport = uiState.aiReport,
                                isAiLoading = uiState.isAiLoading,
                                onRunAiAnalysis = { viewModel.runAiAnalysis() },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("savings") {
                            SavingsGoalsScreen(
                                currencySymbol = userProfile?.currencySymbol ?: "$",
                                goals = savingsGoals,
                                onAddGoal = { title, cat, target -> viewModel.addSavingsGoal(title, cat, target) },
                                onAddDeposit = { goal, amt -> viewModel.addDepositToGoal(goal, amt) },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("loans") {
                            LoansScreen(
                                currencySymbol = userProfile?.currencySymbol ?: "$",
                                loans = loans,
                                onAddLoan = { title, type, total, emi, lender ->
                                    viewModel.addLoan(title, type, total, emi, lender)
                                },
                                onPayEmi = { loan -> viewModel.payLoanEmi(loan) },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("assets") {
                            AssetsScreen(
                                currencySymbol = userProfile?.currencySymbol ?: "$",
                                assets = assets,
                                onAddAsset = { name, cat, valAmount, code ->
                                    viewModel.addAsset(name, cat, valAmount, code)
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("ai") {
                            AiAssistantScreen(
                                currencySymbol = userProfile?.currencySymbol ?: "$",
                                aiReport = uiState.aiReport,
                                aiAnswerText = uiState.aiAnswerText,
                                isAiLoading = uiState.isAiLoading,
                                onRunAiAnalysis = { viewModel.runAiAnalysis() },
                                onAskAiQuestion = { q -> viewModel.askAiAssistant(q) },
                                onScanReceiptRequested = { bmp -> viewModel.scanReceiptWithAi(bmp) },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                userProfile = userProfile,
                                onUpdateTheme = { style -> viewModel.updateThemeStyle(style) },
                                onUpdateCurrency = { code, symbol -> viewModel.updateCurrency(code, symbol) },
                                onExportCsv = { viewModel.exportDataCsv() },
                                onLockApp = { viewModel.lockApp() },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
