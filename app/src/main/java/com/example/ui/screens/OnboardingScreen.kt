package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NothingDotMatrixText
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingDarkCard
import com.example.ui.theme.NothingRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: (
        name: String,
        age: Int,
        gender: String,
        country: String,
        currencyCode: String,
        currencySymbol: String,
        salaryDate: Int,
        monthlyIncome: Double,
        financialGoal: String
    ) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedAuthMethod by remember { mutableStateOf("") }

    // Profile fields
    var name by remember { mutableStateOf("Alex Vance") }
    var ageStr by remember { mutableStateOf("28") }
    var gender by remember { mutableStateOf("Male") }
    var country by remember { mutableStateOf("United States") }
    var currencyCode by remember { mutableStateOf("USD") }
    var currencySymbol by remember { mutableStateOf("$") }
    var salaryDateStr by remember { mutableStateOf("1") }
    var incomeStr by remember { mutableStateOf("6800") }
    var financialGoal by remember { mutableStateOf("Save $20,000 & Grow Assets") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        NothingDotMatrixText(
            text = if (currentStep == 1) "WELCOME TO OS" else "YOUR PROFILE",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (currentStep == 1) "Choose sign-in method to sync across devices"
            else "Configure your personal financial settings",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (currentStep == 1) {
            // Step 1: Sign in methods
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AuthOptionCard(
                    title = "Continue with Google",
                    subtitle = "One-tap secure Google Account sync",
                    icon = Icons.Default.AccountCircle,
                    isSelected = selectedAuthMethod == "GOOGLE",
                    onClick = {
                        selectedAuthMethod = "GOOGLE"
                        currentStep = 2
                    }
                )

                AuthOptionCard(
                    title = "Phone Number OTP",
                    subtitle = "SMS verification code",
                    icon = Icons.Default.Phone,
                    isSelected = selectedAuthMethod == "PHONE",
                    onClick = {
                        selectedAuthMethod = "PHONE"
                        currentStep = 2
                    }
                )

                AuthOptionCard(
                    title = "Email & Password",
                    subtitle = "Traditional encrypted login",
                    icon = Icons.Default.Email,
                    isSelected = selectedAuthMethod == "EMAIL",
                    onClick = {
                        selectedAuthMethod = "EMAIL"
                        currentStep = 2
                    }
                )
            }
        } else {
            // Step 2: Profile creation
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("Country") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Gender radio pills
                Text("Gender", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Male", "Female", "Other").forEach { option ->
                        FilterChip(
                            selected = gender == option,
                            onClick = { gender = option },
                            label = { Text(option) },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                // Currency selector
                Text("Preferred Currency", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "USD" to "$",
                        "EUR" to "€",
                        "INR" to "₹",
                        "GBP" to "£",
                        "JPY" to "¥"
                    ).forEach { (code, symbol) ->
                        FilterChip(
                            selected = currencyCode == code,
                            onClick = {
                                currencyCode = code
                                currencySymbol = symbol
                            },
                            label = { Text("$code ($symbol)") },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = incomeStr,
                        onValueChange = { incomeStr = it },
                        label = { Text("Monthly Income ($currencySymbol)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = salaryDateStr,
                        onValueChange = { salaryDateStr = it },
                        label = { Text("Salary Date (1-31)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = financialGoal,
                    onValueChange = { financialGoal = it },
                    label = { Text("Financial Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val age = ageStr.toIntOrNull() ?: 25
                        val salaryDate = salaryDateStr.toIntOrNull() ?: 1
                        val income = incomeStr.toDoubleOrNull() ?: 5000.0
                        onOnboardingComplete(
                            name, age, gender, country, currencyCode, currencySymbol,
                            salaryDate, income, financialGoal
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NothingRed),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("FINISH & LAUNCH EXPENSE OS", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AuthOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = NothingRed, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}
