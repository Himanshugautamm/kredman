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
import com.example.data.model.AssetCategory
import com.example.data.model.AssetItemEntity
import com.example.ui.components.NothingDotMatrixText
import com.example.ui.theme.FinanceIncomeGreen
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingDarkCard
import com.example.ui.theme.NothingRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    currencySymbol: String = "$",
    assets: List<AssetItemEntity>,
    onAddAsset: (name: String, category: AssetCategory, value: Double, code: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showCreateModal by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(AssetCategory.STOCKS) }
    var valueStr by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    val totalAssetVal = assets.sumOf { it.currentValue }

    Scaffold(
        containerColor = NothingBlack,
        topBar = {
            TopAppBar(
                title = { Text("ASSETS & PORTFOLIO") },
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
                Icon(Icons.Default.Add, contentDescription = "Add Asset")
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
                        Text("TOTAL ASSET PORTFOLIO", fontSize = 11.sp, color = FinanceIncomeGreen, fontWeight = FontWeight.Bold)
                        NothingDotMatrixText(text = "$currencySymbol${String.format("%,.2f", totalAssetVal)}", fontSize = 28.sp)
                    }
                }
            }

            items(assets, key = { it.id }) { asset ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(asset.category.label.uppercase(), fontSize = 10.sp, color = NothingRed, fontWeight = FontWeight.Bold)
                            Text(asset.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            if (asset.symbolCode.isNotEmpty()) {
                                Text("Ticker: ${asset.symbolCode}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        }

                        Text(
                            text = "$currencySymbol${String.format("%,.2f", asset.currentValue)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FinanceIncomeGreen
                        )
                    }
                }
            }
        }

        if (showCreateModal) {
            AlertDialog(
                onDismissRequest = { showCreateModal = false },
                title = { Text("Add Asset") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Asset Name (e.g., Apple Stocks, Gold Bar)") }
                        )
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Symbol / Ticker (Optional)") }
                        )
                        OutlinedTextField(
                            value = valueStr,
                            onValueChange = { valueStr = it },
                            label = { Text("Current Value ($currencySymbol)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val v = valueStr.toDoubleOrNull() ?: 0.0
                            if (name.isNotEmpty()) {
                                onAddAsset(name, category, v, code)
                            }
                            showCreateModal = false
                        }
                    ) {
                        Text("Add Asset")
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
