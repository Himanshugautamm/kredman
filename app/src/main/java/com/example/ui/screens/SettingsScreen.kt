package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThemeStyle
import com.example.data.model.UserProfileEntity
import com.example.ui.components.NothingDotMatrixText
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingDarkCard
import com.example.ui.theme.NothingRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userProfile: UserProfileEntity?,
    onUpdateTheme: (ThemeStyle) -> Unit,
    onUpdateCurrency: (code: String, symbol: String) -> Unit,
    onExportCsv: () -> Unit,
    onLockApp: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentTheme = userProfile?.themeStyle ?: ThemeStyle.NOTHING

    Scaffold(
        containerColor = NothingBlack,
        topBar = {
            TopAppBar(
                title = { Text("SETTINGS & PREFERENCES") },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Theme engine selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("DESIGN SYSTEM & THEME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NothingRed)
                    Text("Switch visual aesthetic philosophy instantly", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ThemeStyle.values().toList().chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { styleOption ->
                                    FilterChip(
                                        selected = currentTheme == styleOption,
                                        onClick = { onUpdateTheme(styleOption) },
                                        label = { Text(styleOption.label) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = NothingRed,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Currency Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("PRIMARY CURRENCY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NothingRed)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "USD" to "$",
                            "EUR" to "€",
                            "INR" to "₹",
                            "GBP" to "£",
                            "JPY" to "¥"
                        ).forEach { (code, symbol) ->
                            FilterChip(
                                selected = userProfile?.currencyCode == code,
                                onClick = { onUpdateCurrency(code, symbol) },
                                label = { Text("$code ($symbol)") },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }

            // Data & Cloud Sync Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("DATA & CLOUD BACKUP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NothingRed)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Offline-first Local Room Sync", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Automatic cloud sync active", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onExportCsv,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export All Transactions to CSV")
                    }
                }
            }

            // Security Lock Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("SECURITY & PRIVACY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NothingRed)

                    Button(
                        onClick = onLockApp,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NothingRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lock ExpenseOS Now")
                    }
                }
            }
        }
    }
}
