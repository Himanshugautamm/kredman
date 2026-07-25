package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LoanEntity
import com.example.data.model.LoanType
import com.example.ui.theme.FinanceExpenseRed
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingDarkCard
import com.example.ui.theme.NothingRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(
    currencySymbol: String = "$",
    loans: List<LoanEntity>,
    onAddLoan: (title: String, type: LoanType, totalAmount: Double, monthlyEmi: Double, lender: String) -> Unit,
    onPayEmi: (LoanEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showCreateModal by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(LoanType.PERSONAL) }
    var totalAmtStr by remember { mutableStateOf("") }
    var emiStr by remember { mutableStateOf("") }
    var lender by remember { mutableStateOf("") }

    val totalDebt = loans.sumOf { it.remainingAmount }

    Scaffold(
        containerColor = NothingBlack,
        topBar = {
            TopAppBar(
                title = { Text("LOANS & DEBTS") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateModal = true },
                containerColor = NothingRed,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Debt")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("TOTAL LIABILITIES & DEBT", fontSize = 11.sp, color = FinanceExpenseRed, fontWeight = FontWeight.Bold)
                        Text("$currencySymbol${String.format("%,.2f", totalDebt)}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(loans, key = { it.id }) { loan ->
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
                                Text(loan.type.label.uppercase(), fontSize = 10.sp, color = NothingRed, fontWeight = FontWeight.Bold)
                                Text(loan.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                if (loan.lenderOrBorrower.isNotEmpty()) {
                                    Text("Lender: ${loan.lenderOrBorrower}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }

                            Button(
                                onClick = { onPayEmi(loan) },
                                colors = ButtonDefaults.buttonColors(containerColor = NothingRed),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Pay EMI", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Remaining", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Text("$currencySymbol${String.format("%,.2f", loan.remainingAmount)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Monthly EMI", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Text("$currencySymbol${String.format("%,.0f", loan.monthlyEmi)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (showCreateModal) {
            AlertDialog(
                onDismissRequest = { showCreateModal = false },
                title = { Text("Track New Debt / Loan") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Loan Title") }
                        )
                        OutlinedTextField(
                            value = lender,
                            onValueChange = { lender = it },
                            label = { Text("Lender / Bank / Person") }
                        )
                        OutlinedTextField(
                            value = totalAmtStr,
                            onValueChange = { totalAmtStr = it },
                            label = { Text("Total Balance ($currencySymbol)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = emiStr,
                            onValueChange = { emiStr = it },
                            label = { Text("Monthly EMI ($currencySymbol)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val tot = totalAmtStr.toDoubleOrNull() ?: 1000.0
                            val emi = emiStr.toDoubleOrNull() ?: 100.0
                            if (title.isNotEmpty()) {
                                onAddLoan(title, type, tot, emi, lender)
                            }
                            showCreateModal = false
                        }
                    ) {
                        Text("Save Loan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateModal = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
