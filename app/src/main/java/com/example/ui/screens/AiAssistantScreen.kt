package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiFinancialReport
import com.example.ui.components.NothingDotMatrixText
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingDarkCard
import com.example.ui.theme.NothingRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    currencySymbol: String = "$",
    aiReport: AiFinancialReport?,
    aiAnswerText: String?,
    isAiLoading: Boolean,
    onRunAiAnalysis: () -> Unit,
    onAskAiQuestion: (String) -> Unit,
    onScanReceiptRequested: (Bitmap) -> Unit,
    onNavigateBack: () -> Unit
) {
    var userQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = NothingBlack,
        topBar = {
            TopAppBar(
                title = { Text("GEMINI AI ASSISTANT") },
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
            NothingDotMatrixText(text = "FINANCIAL COPILOT", fontSize = 24.sp)

            Text(
                text = "Ask natural language questions or analyze receipt photos with Gemini 3.5 Flash",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            // OCR Camera Scanner Card
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
                        Text("MULTIMODAL RECEIPT OCR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NothingRed)
                        IconButton(onClick = {
                            val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bitmap)
                            canvas.drawColor(Color.WHITE)
                            val paint = Paint().apply { color = Color.BLACK; textSize = 28f }
                            canvas.drawText("TACO BELL RESTAURANT", 40f, 80f, paint)
                            canvas.drawText("TOTAL: $24.80", 40f, 160f, paint)
                            onScanReceiptRequested(bitmap)
                        }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Scan", tint = NothingRed)
                        }
                    }
                    Text("Extract merchant, amount, date & payment method automatically using vision AI.", fontSize = 12.sp)
                }
            }

            // Natural Language Chat Query
            OutlinedTextField(
                value = userQuery,
                onValueChange = { userQuery = it },
                placeholder = { Text("e.g., 'Show food expenses from March' or 'How to save $500?'", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        if (userQuery.isNotEmpty()) {
                            onAskAiQuestion(userQuery)
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Ask", tint = NothingRed)
                    }
                }
            )

            if (isAiLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = NothingRed)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Gemini is analyzing...", fontSize = 13.sp)
                }
            }

            if (aiAnswerText != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NothingRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GEMINI RESPONSE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NothingRed)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(aiAnswerText, fontSize = 14.sp)
                    }
                }
            }

            // Report Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NothingDarkCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("MONTHLY SUMMARY & ADVICE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NothingRed)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(aiReport?.summary ?: "Run full analysis to calculate budget health.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRunAiAnalysis,
                        colors = ButtonDefaults.buttonColors(containerColor = NothingRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Generate Full Report")
                    }
                }
            }
        }
    }
}
