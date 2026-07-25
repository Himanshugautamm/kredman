package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ParsedOcrReceipt
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionType
import com.example.ui.components.NothingDotMatrixText
import com.example.ui.theme.FinanceExpenseRed
import com.example.ui.theme.FinanceIncomeGreen
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    currencySymbol: String = "$",
    ocrResult: ParsedOcrReceipt?,
    isAiLoading: Boolean,
    onScanReceiptRequested: (Bitmap) -> Unit,
    onVoicePromptRequested: (String) -> Unit,
    onSaveTransaction: (
        title: String,
        amount: Double,
        category: String,
        type: TransactionType,
        paymentMethod: PaymentMethod,
        notes: String,
        isRecurring: Boolean
    ) -> Unit,
    onNavigateBack: () -> Unit
) {
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food & Grocery") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CREDIT_CARD) }
    var notes by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(false) }
    var voicePromptText by remember { mutableStateOf("") }

    // Sync OCR Result if parsed by Gemini AI!
    LaunchedEffect(ocrResult) {
        if (ocrResult != null) {
            title = ocrResult.title
            amountStr = if (ocrResult.amount > 0) ocrResult.amount.toString() else amountStr
            category = ocrResult.category
            paymentMethod = ocrResult.paymentMethod
            if (ocrResult.notes.isNotEmpty()) notes = ocrResult.notes
        }
    }

    val categories = if (type == TransactionType.EXPENSE) listOf(
        "Food & Grocery", "Dining", "Electronics", "Transport", "Shopping", "Bills & Utilities", "Entertainment", "Health", "Subscriptions", "Other"
    ) else listOf(
        "Salary", "Freelance", "Business", "Rental", "Investments", "Cashback", "Gifts", "Other"
    )

    Scaffold(
        containerColor = NothingBlack,
        topBar = {
            TopAppBar(
                title = { Text(if (type == TransactionType.EXPENSE) "ADD EXPENSE" else "ADD INCOME") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Segmented Switcher
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        type = TransactionType.EXPENSE
                        if (!categories.contains(category)) category = "Food & Grocery"
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == TransactionType.EXPENSE) FinanceExpenseRed else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("EXPENSE", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        type = TransactionType.INCOME
                        if (!categories.contains(category)) category = "Salary"
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == TransactionType.INCOME) FinanceIncomeGreen else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("INCOME", fontWeight = FontWeight.Bold)
                }
            }

            // Gemini AI Smart Scanner Tools
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GEMINI AI SMART ENTRY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NothingRed)
                        if (isAiLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                // Create simulated test receipt bitmap for OCR parsing
                                val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
                                val canvas = Canvas(bitmap)
                                canvas.drawColor(Color.WHITE)
                                val paint = Paint().apply { color = Color.BLACK; textSize = 28f }
                                canvas.drawText("SUPERMARKET RECEIPT", 40f, 80f, paint)
                                canvas.drawText("GROCERIES $48.90", 40f, 160f, paint)
                                onScanReceiptRequested(bitmap)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Receipt", fontSize = 12.sp)
                        }
                    }

                    // Voice Entry Prompt Input
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = voicePromptText,
                            onValueChange = { voicePromptText = it },
                            placeholder = { Text("Or type/speak e.g. 'Spent $35 on Uber via UPI'", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { onVoicePromptRequested(voicePromptText) }) {
                            Icon(Icons.Default.Mic, contentDescription = "Parse Voice", tint = NothingRed)
                        }
                    }
                }
            }

            // Entry Form Fields
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title / Merchant") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Amount ($currencySymbol)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Chips
            Text("Category", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }

            // Payment Method
            Text("Payment Method", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                PaymentMethod.values().toList().chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { pm ->
                            FilterChip(
                                selected = paymentMethod == pm,
                                onClick = { paymentMethod = pm },
                                label = { Text(pm.label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Description") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recurring Monthly Transaction", fontSize = 13.sp)
                Switch(checked = isRecurring, onCheckedChange = { isRecurring = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    val finalTitle = title.ifBlank { "Untitled ${type.name.lowercase().capitalize()}" }
                    onSaveTransaction(finalTitle, amount, category, type, paymentMethod, notes, isRecurring)
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == TransactionType.EXPENSE) FinanceExpenseRed else FinanceIncomeGreen
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("SAVE TRANSACTION", fontWeight = FontWeight.Bold)
            }
        }
    }
}
