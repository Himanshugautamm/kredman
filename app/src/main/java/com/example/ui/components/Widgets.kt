package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun TotalBalanceWidgetContent(
    totalBalance: Double = 285420.0,
    currencySymbol: String = "₹",
    onCurrencyChange: (String) -> Unit = {},
    isBalanceHidden: Boolean = false,
    onToggleHideBalance: () -> Unit = {}
) {
    var expandedCurrencyMenu by remember { mutableStateOf(false) }
    val currencies = listOf(
        "INR" to "₹",
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "JPY" to "¥"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E1B4B), // Deep indigo
                        Color(0xFF311042), // Dark violet
                        Color(0xFF18103C)
                    )
                )
            )
            .padding(18.dp)
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Total Balance",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onToggleHideBalance,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Balance Visibility",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Currency selector dropdown chip
                Box {
                    Surface(
                        onClick = { expandedCurrencyMenu = true },
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                when (currencySymbol) {
                                    "₹" -> "INR"
                                    "$" -> "USD"
                                    "€" -> "EUR"
                                    "£" -> "GBP"
                                    "¥" -> "JPY"
                                    else -> "INR"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expandedCurrencyMenu,
                        onDismissRequest = { expandedCurrencyMenu = false }
                    ) {
                        currencies.forEach { (code, symbol) ->
                            DropdownMenuItem(
                                text = { Text("$code ($symbol)") },
                                onClick = {
                                    onCurrencyChange(symbol)
                                    expandedCurrencyMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Amount display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = if (isBalanceHidden) "••••••••" else "$currencySymbol${String.format("%,.0f", totalBalance)}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF4ADE80),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "+8.4% vs last month",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4ADE80)
                        )
                    }
                }

                // Sparkline graph
                SparklineGraph(
                    modifier = Modifier
                        .width(110.dp)
                        .height(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Page dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(0.9f, 0.3f, 0.3f, 0.3f, 0.3f).forEach { opacity ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(if (opacity == 0.9f) 6.dp else 4.dp)
                            .background(Color.White.copy(alpha = opacity), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun SparklineGraph(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val points = listOf(
            Offset(0f, height * 0.7f),
            Offset(width * 0.2f, height * 0.5f),
            Offset(width * 0.4f, height * 0.6f),
            Offset(width * 0.6f, height * 0.3f),
            Offset(width * 0.8f, height * 0.4f),
            Offset(width, height * 0.1f)
        )

        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val current = points[i]
                cubicTo(
                    (prev.x + current.x) / 2, prev.y,
                    (prev.x + current.x) / 2, current.y,
                    current.x, current.y
                )
            }
        }

        val fillPath = androidx.compose.ui.graphics.Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFA855F7).copy(alpha = 0.4f),
                    Color(0xFFA855F7).copy(alpha = 0.0f)
                )
            )
        )

        drawPath(
            path = path,
            color = Color(0xFFC084FC),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun NetWorthWidgetContent(
    netWorth: Double,
    currencySymbol: String = "$"
) {
    Column(verticalArrangement = Arrangement.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NothingDotMatrixText(
                text = "$currencySymbol${String.format("%,.2f", netWorth)}",
                fontSize = 32.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                color = FinanceIncomeGreen.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = FinanceIncomeGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+12.4% this month", fontSize = 11.sp, color = FinanceIncomeGreen, fontWeight = FontWeight.Bold)
                }
            }
            Text("Real-time valuation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun IncomeExpenseWidgetContent(
    income: Double,
    expense: Double,
    currencySymbol: String = "$"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(FinanceIncomeGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("INCOME", fontSize = 10.sp, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("$currencySymbol${String.format("%,.0f", income)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FinanceIncomeGreen)
        }

        Divider(
            modifier = Modifier
                .height(36.dp)
                .width(1.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(FinanceExpenseRed, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("EXPENSE", fontSize = 10.sp, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("$currencySymbol${String.format("%,.0f", expense)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FinanceExpenseRed)
        }
    }
}

@Composable
fun CashBankUpiWidgetContent(
    cash: Double,
    bank: Double,
    upi: Double,
    currencySymbol: String = "$"
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Bank Account", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text("$currencySymbol${String.format("%,.2f", bank)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { 0.7f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
            color = LumiaBlue
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("UPI Wallet", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text("$currencySymbol${String.format("%,.2f", upi)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { 0.4f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
            color = LumiaGreen
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Cash in Hand", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text("$currencySymbol${String.format("%,.2f", cash)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ExpenseCategoriesPieChartContent(
    transactions: List<TransactionEntity>,
    currencySymbol: String = "$"
) {
    val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
    val grouped = expenses.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }
    val total = grouped.values.sum().coerceAtLeast(1.0)

    val colors = listOf(NothingRed, LumiaBlue, LumiaGreen, LumiaOrange, LumiaPurple, FinanceGold)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pie Chart Canvas
        Box(
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                val strokeWidth = 18.dp.toPx()
                var colorIdx = 0
                for ((_, amount) in grouped) {
                    val sweepAngle = ((amount / total) * 360f).toFloat()
                    drawArc(
                        color = colors[colorIdx % colors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    )
                    startAngle += sweepAngle
                    colorIdx++
                }
            }
            Text("$currencySymbol${String.format("%.0f", total)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Legend list
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            grouped.entries.take(4).forEachIndexed { idx, entry ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(colors[idx % colors.size], CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(entry.key, fontSize = 11.sp, maxLines = 1, modifier = Modifier.weight(1f, fill = false))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$currencySymbol${String.format("%.0f", entry.value)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SavingsGoalsWidgetContent(
    goals: List<SavingsGoalEntity>,
    currencySymbol: String = "$"
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        goals.take(3).forEach { goal ->
            val ratio = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(goal.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("${(ratio * 100).toInt()}%", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(2.dp))
                LinearProgressIndicator(
                    progress = { ratio },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = LumiaGreen,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                )
                Text(
                    "$currencySymbol${String.format("%,.0f", goal.currentAmount)} of $currencySymbol${String.format("%,.0f", goal.targetAmount)}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun LoansWidgetContent(
    loans: List<LoanEntity>,
    currencySymbol: String = "$"
) {
    val totalDebt = loans.sumOf { it.remainingAmount }
    val totalEmi = loans.sumOf { it.monthlyEmi }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Remaining Debt", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text("$currencySymbol${String.format("%,.2f", totalDebt)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FinanceExpenseRed)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Monthly EMI", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text("$currencySymbol${String.format("%,.0f", totalEmi)}/mo", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun RecentTransactionsWidgetContent(
    transactions: List<TransactionEntity>,
    currencySymbol: String = "$"
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        transactions.take(4).forEach { tx ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (tx.type == TransactionType.INCOME) FinanceIncomeGreen.copy(alpha = 0.2f)
                                else NothingDotGray,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (tx.type == TransactionType.INCOME) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (tx.type == TransactionType.INCOME) FinanceIncomeGreen else NothingRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(tx.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                        Text(tx.category, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }

                Text(
                    text = "${if (tx.type == TransactionType.INCOME) "+" else "-"}$currencySymbol${String.format("%.2f", tx.amount)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (tx.type == TransactionType.INCOME) FinanceIncomeGreen else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun AssetPortfolioWidgetContent(
    assets: List<AssetItemEntity>,
    currencySymbol: String = "$"
) {
    val totalVal = assets.sumOf { it.currentValue }
    Column {
        Text("Portfolio Value: $currencySymbol${String.format("%,.2f", totalVal)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            assets.take(3).forEach { asset ->
                Surface(
                    modifier = Modifier.weight(1f),
                    color = NothingDotGray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(asset.name, fontSize = 10.sp, maxLines = 1)
                        Text("$currencySymbol${String.format("%.0f", asset.currentValue)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetGaugeWidgetContent(
    income: Double,
    expense: Double,
    currencySymbol: String = "$"
) {
    val budget = income.coerceAtLeast(1000.0)
    val remaining = (budget - expense).coerceAtLeast(0.0)
    val ratio = (expense / budget).coerceIn(0.0, 1.0).toFloat()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Budget Remaining", fontSize = 12.sp)
            Text("$currencySymbol${String.format("%,.0f", remaining)} left", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FinanceIncomeGreen)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = if (ratio > 0.85f) NothingRed else LumiaBlue,
            trackColor = NothingDotGray
        )
    }
}

@Composable
fun AiHealthScoreWidgetContent(
    score: Int = 88,
    onRunAiAnalysis: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Financial Health", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$score", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = NothingRed)
                Text("/100", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
        Button(
            onClick = onRunAiAnalysis,
            colors = ButtonDefaults.buttonColors(containerColor = NothingRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("AI Insights", fontSize = 12.sp)
        }
    }
}
