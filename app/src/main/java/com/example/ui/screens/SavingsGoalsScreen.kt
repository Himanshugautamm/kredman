package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalCategory
import com.example.data.model.SavingsGoalEntity
import com.example.ui.components.NothingDotMatrixText
import com.example.ui.theme.LumiaGreen
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingDarkCard
import com.example.ui.theme.NothingRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalsScreen(
    currencySymbol: String = "$",
    goals: List<SavingsGoalEntity>,
    onAddGoal: (title: String, category: GoalCategory, targetAmount: Double) -> Unit,
    onAddDeposit: (SavingsGoalEntity, amount: Double) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedDepositGoal by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var depositAmountStr by remember { mutableStateOf("") }

    var newGoalTitle by remember { mutableStateOf("") }
    var newGoalCategory by remember { mutableStateOf(GoalCategory.VACATION) }
    var newGoalTargetStr by remember { mutableStateOf("") }

    Scaffold(
        containerColor = NothingBlack,
        topBar = {
            TopAppBar(
                title = { Text("SAVINGS GOALS") },
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
                onClick = { showCreateDialog = true },
                containerColor = NothingRed,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
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
                Text(
                    text = "Track your long-term wealth milestones & target progress",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            items(goals, key = { it.id }) { goal ->
                val ratio = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()
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
                                Text(goal.category.label.uppercase(), fontSize = 10.sp, color = NothingRed, fontWeight = FontWeight.Bold)
                                Text(goal.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("${(ratio * 100).toInt()}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LumiaGreen)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = LumiaGreen,
                            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$currencySymbol${String.format("%,.0f", goal.currentAmount)} / $currencySymbol${String.format("%,.0f", goal.targetAmount)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Button(
                                onClick = {
                                    selectedDepositGoal = goal
                                    depositAmountStr = ""
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("+ Deposit", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Add Deposit Dialog
        if (selectedDepositGoal != null) {
            AlertDialog(
                onDismissRequest = { selectedDepositGoal = null },
                title = { Text("Deposit to ${selectedDepositGoal?.title}") },
                text = {
                    OutlinedTextField(
                        value = depositAmountStr,
                        onValueChange = { depositAmountStr = it },
                        label = { Text("Amount ($currencySymbol)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = depositAmountStr.toDoubleOrNull() ?: 0.0
                            if (amt > 0 && selectedDepositGoal != null) {
                                onAddDeposit(selectedDepositGoal!!, amt)
                            }
                            selectedDepositGoal = null
                        }
                    ) {
                        Text("Add Deposit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedDepositGoal = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Create Goal Dialog
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("New Savings Goal") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = newGoalTitle,
                            onValueChange = { newGoalTitle = it },
                            label = { Text("Goal Title") }
                        )

                        OutlinedTextField(
                            value = newGoalTargetStr,
                            onValueChange = { newGoalTargetStr = it },
                            label = { Text("Target Amount ($currencySymbol)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val target = newGoalTargetStr.toDoubleOrNull() ?: 1000.0
                            if (newGoalTitle.isNotEmpty()) {
                                onAddGoal(newGoalTitle, newGoalCategory, target)
                                newGoalTitle = ""
                                newGoalTargetStr = ""
                            }
                            showCreateDialog = false
                        }
                    ) {
                        Text("Create Goal")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
