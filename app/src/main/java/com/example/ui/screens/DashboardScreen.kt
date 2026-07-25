package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingDarkCard
import com.example.ui.theme.NothingRed

enum class DashboardViewMode {
    FULL_DASHBOARD,
    CUSTOMIZE,
    MINIMAL_MODE
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    userProfile: UserProfileEntity?,
    activeWidgets: List<WidgetConfigEntity>,
    netWorth: Double,
    totalIncome: Double,
    totalExpenses: Double,
    cashBalance: Double,
    bankBalance: Double,
    upiBalance: Double,
    transactions: List<TransactionEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    loans: List<LoanEntity>,
    assets: List<AssetItemEntity>,
    isCustomizeMode: Boolean,
    activePreset: DashboardPreset,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPresetChange: (DashboardPreset) -> Unit,
    onToggleCustomizeMode: () -> Unit,
    onWidgetResize: (WidgetConfigEntity) -> Unit,
    onWidgetStyleChange: (WidgetConfigEntity, WidgetStyle) -> Unit,
    onWidgetToggleHide: (WidgetConfigEntity) -> Unit,
    onMoveWidgetUp: (Int) -> Unit,
    onMoveWidgetDown: (Int) -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSavings: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var viewMode by remember { mutableStateOf(DashboardViewMode.FULL_DASHBOARD) }
    var selectedCurrency by remember { mutableStateOf(userProfile?.currencySymbol ?: "₹") }
    var isBalanceHidden by remember { mutableStateOf(false) }
    var customizeSearchQuery by remember { mutableStateOf("") }
    var activeCategoryFilter by remember { mutableStateOf("All") }

    val userName = userProfile?.name ?: "Himanshu"

    // Automatically switch viewMode if ViewModel triggers customize mode
    LaunchedEffect(isCustomizeMode) {
        if (isCustomizeMode) {
            viewMode = DashboardViewMode.CUSTOMIZE
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (viewMode) {
            DashboardViewMode.FULL_DASHBOARD -> {
                FullDashboardView(
                    userName = userName,
                    userProfile = userProfile,
                    netWorth = netWorth,
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    currencySymbol = selectedCurrency,
                    isBalanceHidden = isBalanceHidden,
                    transactions = transactions,
                    activePreset = activePreset,
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    onPresetChange = onPresetChange,
                    onCurrencyChange = { selectedCurrency = it },
                    onToggleHideBalance = { isBalanceHidden = !isBalanceHidden },
                    onSelectViewMode = { mode ->
                        viewMode = mode
                        if (mode == DashboardViewMode.CUSTOMIZE && !isCustomizeMode) {
                            onToggleCustomizeMode()
                        }
                    },
                    onNavigateToAddTransaction = onNavigateToAddTransaction,
                    onNavigateToAnalytics = onNavigateToAnalytics,
                    onNavigateToSavings = onNavigateToSavings,
                    onNavigateToLoans = onNavigateToLoans,
                    onNavigateToAi = onNavigateToAi,
                    onNavigateToSettings = onNavigateToSettings
                )
            }

            DashboardViewMode.CUSTOMIZE -> {
                CustomizeDashboardView(
                    activeWidgets = activeWidgets,
                    searchQuery = customizeSearchQuery,
                    onSearchQueryChange = { customizeSearchQuery = it },
                    categoryFilter = activeCategoryFilter,
                    onCategoryFilterChange = { activeCategoryFilter = it },
                    onWidgetToggleHide = onWidgetToggleHide,
                    onDoneClick = {
                        viewMode = DashboardViewMode.FULL_DASHBOARD
                        if (isCustomizeMode) onToggleCustomizeMode()
                    },
                    onNavigateBack = {
                        viewMode = DashboardViewMode.FULL_DASHBOARD
                        if (isCustomizeMode) onToggleCustomizeMode()
                    }
                )
            }

            DashboardViewMode.MINIMAL_MODE -> {
                MinimalDashboardView(
                    userName = userName,
                    netWorth = netWorth,
                    currencySymbol = selectedCurrency,
                    onAddWidgetClick = {
                        viewMode = DashboardViewMode.CUSTOMIZE
                        if (!isCustomizeMode) onToggleCustomizeMode()
                    },
                    onSelectViewMode = { viewMode = it },
                    onNavigateToAddTransaction = onNavigateToAddTransaction,
                    onNavigateToAnalytics = onNavigateToAnalytics,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
fun FullDashboardView(
    userName: String,
    userProfile: UserProfileEntity?,
    netWorth: Double,
    totalIncome: Double,
    totalExpenses: Double,
    currencySymbol: String,
    isBalanceHidden: Boolean,
    transactions: List<TransactionEntity>,
    activePreset: DashboardPreset,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPresetChange: (DashboardPreset) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onToggleHideBalance: () -> Unit,
    onSelectViewMode: (DashboardViewMode) -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSavings: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF0D0B14), // Dark purple tint
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onNavigateToSettings)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Hello, $userName",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("👋", fontSize = 14.sp)
                            }
                            Text(
                                "Track • Plan • Grow",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { /* Search toggle */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                        Box {
                            IconButton(onClick = onNavigateToAi) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .padding(top = 10.dp, end = 10.dp)
                                    .size(8.dp)
                                    .background(NothingRed, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Pills row (Dashboard, Customize, Minimal)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = true,
                        onClick = { onSelectViewMode(DashboardViewMode.FULL_DASHBOARD) },
                        label = { Text("Dashboard", fontSize = 12.sp) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6366F1),
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = false,
                        onClick = { onSelectViewMode(DashboardViewMode.CUSTOMIZE) },
                        label = { Text("Customize ⚙️", fontSize = 12.sp) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            labelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = false,
                        onClick = { onSelectViewMode(DashboardViewMode.MINIMAL_MODE) },
                        label = { Text("Minimal Mode 🌱", fontSize = 12.sp) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            labelColor = Color.White
                        )
                    )
                }
            }
        },
        bottomBar = {
            CustomBottomBar(
                selectedTab = "home",
                onTabSelect = { tab ->
                    when (tab) {
                        "analytics" -> onNavigateToAnalytics()
                        "goals" -> onNavigateToSavings()
                        "profile" -> onNavigateToSettings()
                    }
                },
                onAddClick = onNavigateToAddTransaction
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total Balance Card
            item {
                TotalBalanceWidgetContent(
                    totalBalance = netWorth,
                    currencySymbol = currencySymbol,
                    onCurrencyChange = onCurrencyChange,
                    isBalanceHidden = isBalanceHidden,
                    onToggleHideBalance = onToggleHideBalance
                )
            }

            // 2x2 Grid Widgets
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Savings
                    DashboardMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccountBalanceWallet,
                        iconBg = Color(0xFF10B981).copy(alpha = 0.2f),
                        iconTint = Color(0xFF34D399),
                        title = "Savings",
                        value = "${currencySymbol}95,000",
                        subtitle = "33% of total",
                        onClick = onNavigateToSavings
                    )

                    // Debt / Loans
                    DashboardMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ShoppingBag,
                        iconBg = Color(0xFFEF4444).copy(alpha = 0.2f),
                        iconTint = Color(0xFFF87171),
                        title = "Debt / Loans",
                        value = "${currencySymbol}1,20,000",
                        subtitle = "2 active loans",
                        onClick = onNavigateToLoans
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Monthly Income
                    DashboardMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.VerticalAlignBottom,
                        iconBg = Color(0xFF10B981).copy(alpha = 0.2f),
                        iconTint = Color(0xFF34D399),
                        title = "Monthly Income",
                        value = "${currencySymbol}42,000",
                        subtitle = "Next: 1 Aug",
                        onClick = {}
                    )

                    // Monthly Expense
                    DashboardMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ShoppingCart,
                        iconBg = Color(0xFFEC4899).copy(alpha = 0.2f),
                        iconTint = Color(0xFFF472B6),
                        title = "Monthly Expense",
                        value = "${currencySymbol}18,450",
                        subtitle = "Budget left: 56%",
                        hasProgress = true,
                        progress = 0.44f,
                        onClick = onNavigateToAnalytics
                    )
                }
            }

            // Quick Actions Section
            item {
                Column {
                    Text(
                        "Quick Actions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.Work,
                            label = "Add Income",
                            bgGradient = listOf(Color(0xFF059669), Color(0xFF10B981)),
                            onClick = onNavigateToAddTransaction
                        )
                        QuickActionButton(
                            icon = Icons.Default.LocalFireDepartment,
                            label = "Add Expense",
                            bgGradient = listOf(Color(0xFFDC2626), Color(0xFFEF4444)),
                            onClick = onNavigateToAddTransaction
                        )
                        QuickActionButton(
                            icon = Icons.Default.QrCodeScanner,
                            label = "Scan Bill",
                            bgGradient = listOf(Color(0xFFD97706), Color(0xFFF59E0B)),
                            onClick = onNavigateToAi
                        )
                        QuickActionButton(
                            icon = Icons.Default.SwapHoriz,
                            label = "Transfer",
                            bgGradient = listOf(Color(0xFF2563EB), Color(0xFF3B82F6)),
                            onClick = {}
                        )
                        QuickActionButton(
                            icon = Icons.Default.MoreHoriz,
                            label = "More",
                            bgGradient = listOf(Color(0xFF4B5563), Color(0xFF6B7280)),
                            onClick = onNavigateToSettings
                        )
                    }
                }
            }

            // Recent Transactions Section
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent Transactions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        TextTextButton(
                            onClick = onNavigateToAnalytics,
                            text = "View All",
                            color = Color(0xFFA855F7)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TransactionRowItem(
                            icon = Icons.Default.ShoppingCart,
                            iconBg = Color(0xFF10B981).copy(alpha = 0.2f),
                            iconTint = Color(0xFF34D399),
                            title = "Grocery Shopping",
                            subtitle = "Food & Beverages",
                            amount = "- ${currencySymbol}2,450",
                            date = "Today",
                            isExpense = true
                        )

                        TransactionRowItem(
                            icon = Icons.Default.Business,
                            iconBg = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                            iconTint = Color(0xFFA78BFA),
                            title = "Salary - Wipro",
                            subtitle = "Income",
                            amount = "+ ${currencySymbol}42,000",
                            date = "Yesterday",
                            isExpense = false
                        )

                        TransactionRowItem(
                            icon = Icons.Default.LocalGasStation,
                            iconBg = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            iconTint = Color(0xFFFBBF24),
                            title = "Fuel",
                            subtitle = "Transport",
                            amount = "- ${currencySymbol}1,200",
                            date = "23 Jul",
                            isExpense = true
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    value: String,
    subtitle: String,
    hasProgress: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = Color(0xFF181528),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(title, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(4.dp))

            if (hasProgress) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = iconTint,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(subtitle, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    bgGradient: List<Color>,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Brush.linearGradient(bgGradient), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
fun TransactionRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    amount: String,
    date: String,
    isExpense: Boolean
) {
    Surface(
        color = Color(0xFF181528),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Text(subtitle, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    amount,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) Color.White else Color(0xFF34D399)
                )
                Text(date, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun TextTextButton(onClick: () -> Unit, text: String, color: Color) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun CustomizeDashboardView(
    activeWidgets: List<WidgetConfigEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categoryFilter: String,
    onCategoryFilterChange: (String) -> Unit,
    onWidgetToggleHide: (WidgetConfigEntity) -> Unit,
    onDoneClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF0D0B14),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Customize Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Add, remove and arrange widgets. Your dashboard, your style!", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                    }

                    TextTextButton(onClick = { /* Reset */ }, text = "Reset", color = Color(0xFFA855F7))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search widgets input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search widgets...", fontSize = 13.sp, color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF181528),
                        unfocusedContainerColor = Color(0xFF181528),
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Widgets Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Widgets (Drag to reorder)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Button(
                        onClick = onDoneClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Done", fontSize = 12.sp)
                    }
                }
            }

            // Active Widgets Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Balance 2x1
                    ActiveWidgetTile(
                        modifier = Modifier.weight(2f),
                        title = "Total Balance",
                        size = "2 × 1",
                        icon = Icons.Default.AccountBalance,
                        isON = true,
                        onToggle = {}
                    )
                    // Savings 1x1
                    ActiveWidgetTile(
                        modifier = Modifier.weight(1f),
                        title = "Savings",
                        size = "1 × 1",
                        icon = Icons.Default.Savings,
                        isON = true,
                        onToggle = {}
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Debt / Loans 1x1
                    ActiveWidgetTile(
                        modifier = Modifier.weight(1f),
                        title = "Debt / Loans",
                        size = "1 × 1",
                        icon = Icons.Default.Home,
                        isON = true,
                        onToggle = {}
                    )
                    // Monthly Expense 1x1
                    ActiveWidgetTile(
                        modifier = Modifier.weight(1f),
                        title = "Monthly Expense",
                        size = "1 × 1",
                        icon = Icons.Default.BarChart,
                        isON = true,
                        onToggle = {}
                    )
                }
            }

            // Add More Widgets Section
            item {
                Column {
                    Text("Add More Widgets", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Categories filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Income", "Expenses", "Investments", "Loans").forEach { cat ->
                            FilterChip(
                                selected = categoryFilter == cat,
                                onClick = { onCategoryFilterChange(cat) },
                                label = { Text(cat, fontSize = 11.sp) },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF6366F1),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.08f),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grid of available widgets to add
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AvailableWidgetTile(Modifier.weight(1f), "Income Sources", "1 × 1", Icons.Default.VerticalAlignBottom)
                            AvailableWidgetTile(Modifier.weight(1f), "Budget Left", "1 × 1", Icons.Default.PieChart)
                            AvailableWidgetTile(Modifier.weight(1f), "Investment", "1 × 1", Icons.Default.TrendingUp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AvailableWidgetTile(Modifier.weight(2f), "Net Worth", "2 × 1", Icons.Default.AccountBalanceWallet)
                            AvailableWidgetTile(Modifier.weight(1f), "Upcoming Bills", "1 × 1", Icons.Default.Receipt)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AvailableWidgetTile(Modifier.weight(1f), "Subscription", "1 × 1", Icons.Default.CardMembership)
                            AvailableWidgetTile(Modifier.weight(1f), "Goals", "1 × 1", Icons.Default.EmojiEvents)
                            AvailableWidgetTile(Modifier.weight(1f), "Calendar", "2 × 1", Icons.Default.CalendarToday)
                        }
                    }
                }
            }

            // Appearance Section
            item {
                Column {
                    Text("Appearance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppearanceOptionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.NightsStay,
                            title = "Theme",
                            value = "Dark"
                        )
                        AppearanceOptionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Wallpaper,
                            title = "Wallpaper",
                            value = "Custom"
                        )
                        AppearanceOptionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Style,
                            title = "Card Style",
                            value = "Glass"
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ActiveWidgetTile(
    modifier: Modifier = Modifier,
    title: String,
    size: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isON: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF181528),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF2E1065), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(18.dp))
                }

                // Drag handle dots icon
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(size, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                Switch(
                    checked = isON,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF10B981)
                    ),
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}

@Composable
fun AvailableWidgetTile(
    modifier: Modifier = Modifier,
    title: String,
    size: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF181528),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                }

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color(0xFF6366F1), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.White, maxLines = 1)
            Text(size, fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun AppearanceOptionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF181528),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF1E1B4B), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF818CF8), modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(title, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun MinimalDashboardView(
    userName: String,
    netWorth: Double,
    currencySymbol: String,
    onAddWidgetClick: () -> Unit,
    onSelectViewMode: (DashboardViewMode) -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Mountain Wallpaper Background
        Image(
            painter = painterResource(id = R.drawable.img_wallpaper_mountain),
            contentDescription = "Wallpaper",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onNavigateToSettings)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Hello, $userName", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Minimal Mode 🌱", fontSize = 11.sp, color = Color(0xFF34D399))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { onSelectViewMode(DashboardViewMode.FULL_DASHBOARD) }) {
                            Icon(Icons.Default.GridView, contentDescription = "Full Dashboard", tint = Color.White)
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    }
                }
            },
            bottomBar = {
                CustomBottomBar(
                    selectedTab = "home",
                    onTabSelect = { tab ->
                        when (tab) {
                            "analytics" -> onNavigateToAnalytics()
                            "profile" -> onNavigateToSettings()
                        }
                    },
                    onAddClick = onNavigateToAddTransaction
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Hero Giant Amount Display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currencySymbol${String.format("%,.0f", netWorth)}",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )

                    Text(
                        "Net Worth",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.25f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("8.4% this month", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pill badges row
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Text("Savings ${currencySymbol}95K", fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Text("Debt ${currencySymbol}1.2L", fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                }

                // Dotted Card: + Add Widget
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onAddWidgetClick)
                        .drawWithContent {
                            drawContent()
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.3f),
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx())
                            )
                        }
                        .background(Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+ Add Widget", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Keep it minimal. Add only what you need.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    }
                }

                // Floating motivation pill
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        "Small steps. Big freedom. 💛",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomBottomBar(
    selectedTab: String,
    onTabSelect: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        color = Color(0xFF100D1B).copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onTabSelect("home") }) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = if (selectedTab == "home") Color(0xFFA855F7) else Color.White.copy(alpha = 0.5f))
            }

            IconButton(onClick = { onTabSelect("analytics") }) {
                Icon(Icons.Default.BarChart, contentDescription = "Analytics", tint = Color.White.copy(alpha = 0.5f))
            }

            // Glowing Radial Gradient Center Add Button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFC084FC),
                                Color(0xFF6366F1),
                                Color(0xFF3B82F6)
                            )
                        )
                    )
                    .clickable(onClick = onAddClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(28.dp))
            }

            IconButton(onClick = { onTabSelect("goals") }) {
                Icon(Icons.Default.EmojiEvents, contentDescription = "Goals", tint = Color.White.copy(alpha = 0.5f))
            }

            IconButton(onClick = { onTabSelect("profile") }) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White.copy(alpha = 0.5f))
            }
        }
    }
}
