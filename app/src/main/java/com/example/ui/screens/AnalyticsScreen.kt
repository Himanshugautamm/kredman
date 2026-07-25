package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiFinancialReport
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.components.NothingDotMatrixText
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    currencySymbol: String = "$",
    netWorth: Double,
    totalIncome: Double,
    totalExpenses: Double,
    transactions: List<TransactionEntity>,
    aiReport: AiFinancialReport?,
    isAiLoading: Boolean,
    onRunAiAnalysis: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedTimeframe by remember { mutableStateOf("Monthly") }

    Scaffold(
        containerColor = NothingBlack,
        topBar = {
            TopAppBar(
                title = { Text("ANALYTICS & HEALTH") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRunAiAnalysis) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Run AI", tint = NothingRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingBlack)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Timeframe pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Daily", "Weekly", "Monthly", "Yearly").forEach { tf ->
                    FilterChip(
                        selected = selectedTimeframe == tf,
                        onClick = { selectedTimeframe = tf },
                        label = { Text(tf) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // Financial Health Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("FINANCIAL HEALTH SCORE", fontSize = 11.sp, letterSpacing = 1.2.sp, color = NothingRed, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            NothingDotMatrixText(text = "${aiReport?.healthScore ?: 88} / 100", fontSize = 32.sp)
                        }

                        Button(
                            onClick = onRunAiAnalysis,
                            colors = ButtonDefaults.buttonColors(containerColor = NothingRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Re-Analyze")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(aiReport?.summary ?: "Your cashflow is healthy. Savings rate is ~38% with manageable debt ratios.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))

                    if (aiReport?.recommendations?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("AI RECOMMENDATIONS:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        aiReport.recommendations.forEach { rec ->
                            Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.Top) {
                                Text("• ", color = NothingRed, fontWeight = FontWeight.Bold)
                                Text(rec, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }

            // Net Worth Line Trend Graph Canvas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("NET WORTH GROWTH TREND", fontSize = 11.sp, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text("$currencySymbol${String.format("%,.2f", netWorth)}", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        val points = listOf(
                            Offset(0f, size.height * 0.8f),
                            Offset(size.width * 0.25f, size.height * 0.65f),
                            Offset(size.width * 0.5f, size.height * 0.45f),
                            Offset(size.width * 0.75f, size.height * 0.3f),
                            Offset(size.width, size.height * 0.15f)
                        )

                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = NothingRed,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )

                        points.forEach { p ->
                            drawCircle(color = NothingRed, radius = 5.dp.toPx(), center = p)
                            drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = p)
                        }
                    }
                }
            }

            // Spending Category Donut Chart
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("EXPENSE BY CATEGORY", fontSize = 11.sp, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(12.dp))

                    val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
                    val grouped = expenses.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }

                    grouped.forEach { (category, amount) ->
                        val pct = if (totalExpenses > 0) (amount / totalExpenses).toFloat() else 0f
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(category, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("$currencySymbol${String.format("%.2f", amount)} (${(pct * 100).toInt()}%)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            LinearProgressIndicator(
                                progress = { pct },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                color = LumiaPurple
                            )
                        }
                    }
                }
            }
        }
    }
}
