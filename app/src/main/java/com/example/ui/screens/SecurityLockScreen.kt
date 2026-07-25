package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NothingDotMatrixText
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingDarkCard
import com.example.ui.theme.NothingRed

@Composable
fun SecurityLockScreen(
    onUnlockWithPin: (String) -> Boolean
) {
    var pinInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = NothingRed, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))

        NothingDotMatrixText(text = "EXPENSE OS LOCKED", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Enter Security PIN or Biometric Fingerprint", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))

        Spacer(modifier = Modifier.height(32.dp))

        // PIN Dot indicators
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            (1..4).forEach { idx ->
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (pinInput.length >= idx) NothingRed else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                )
            }
        }

        if (showError) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Incorrect Security PIN", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Keypad grid
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("BIO", "0", "DEL")
        )

        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                row.forEach { key ->
                    Surface(
                        onClick = {
                            when (key) {
                                "DEL" -> if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                "BIO" -> {
                                    // Biometric unlock bypass
                                    onUnlockWithPin("")
                                }
                                else -> {
                                    if (pinInput.length < 4) {
                                        pinInput += key
                                        if (pinInput.length == 4) {
                                            val success = onUnlockWithPin(pinInput)
                                            if (!success) {
                                                showError = true
                                                pinInput = ""
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        shape = CircleShape,
                        color = NothingDarkCard,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (key == "BIO") {
                                Icon(Icons.Default.Fingerprint, contentDescription = "Biometric", tint = NothingRed)
                            } else {
                                Text(key, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
